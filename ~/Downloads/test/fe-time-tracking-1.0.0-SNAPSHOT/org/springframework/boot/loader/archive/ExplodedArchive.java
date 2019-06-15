package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public class ExplodedArchive extends Archive {
   private static final Set SKIPPED_NAMES = new HashSet(Arrays.asList(".", ".."));
   private static final AsciiBytes MANIFEST_ENTRY_NAME = new AsciiBytes("META-INF/MANIFEST.MF");
   private final File root;
   private Map entries;
   private Manifest manifest;
   private boolean filtered;

   public ExplodedArchive(File root) {
      this(root, true);
   }

   public ExplodedArchive(File root, boolean recursive) {
      this.entries = new LinkedHashMap();
      this.filtered = false;
      if (root.exists() && root.isDirectory()) {
         this.root = root;
         this.buildEntries(root, recursive);
         this.entries = Collections.unmodifiableMap(this.entries);
      } else {
         throw new IllegalArgumentException("Invalid source folder " + root);
      }
   }

   private ExplodedArchive(File root, Map entries) {
      this.entries = new LinkedHashMap();
      this.filtered = false;
      this.root = root;
      this.filtered = true;
      this.entries = Collections.unmodifiableMap(entries);
   }

   private void buildEntries(File file, boolean recursive) {
      if (!file.equals(this.root)) {
         String name = file.toURI().getPath().substring(this.root.toURI().getPath().length());
         ExplodedArchive.FileEntry entry = new ExplodedArchive.FileEntry(new AsciiBytes(name), file);
         this.entries.put(entry.getName(), entry);
      }

      if (file.isDirectory()) {
         File[] files = file.listFiles();
         if (files == null) {
            return;
         }

         File[] arr$ = files;
         int len$ = files.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            File child = arr$[i$];
            if (!SKIPPED_NAMES.contains(child.getName()) && (file.equals(this.root) || recursive || file.getName().equals("META-INF"))) {
               this.buildEntries(child, recursive);
            }
         }
      }

   }

   public URL getUrl() throws MalformedURLException {
      ExplodedArchive.FilteredURLStreamHandler handler = this.filtered ? new ExplodedArchive.FilteredURLStreamHandler() : null;
      return new URL("file", "", -1, this.root.toURI().getPath(), handler);
   }

   public Manifest getManifest() throws IOException {
      if (this.manifest == null && this.entries.containsKey(MANIFEST_ENTRY_NAME)) {
         ExplodedArchive.FileEntry entry = (ExplodedArchive.FileEntry)this.entries.get(MANIFEST_ENTRY_NAME);
         FileInputStream inputStream = new FileInputStream(entry.getFile());

         try {
            this.manifest = new Manifest(inputStream);
         } finally {
            inputStream.close();
         }
      }

      return this.manifest;
   }

   public List getNestedArchives(Archive.EntryFilter filter) throws IOException {
      List nestedArchives = new ArrayList();
      Iterator i$ = this.getEntries().iterator();

      while(i$.hasNext()) {
         Archive.Entry entry = (Archive.Entry)i$.next();
         if (filter.matches(entry)) {
            nestedArchives.add(this.getNestedArchive(entry));
         }
      }

      return Collections.unmodifiableList(nestedArchives);
   }

   public Collection getEntries() {
      return Collections.unmodifiableCollection(this.entries.values());
   }

   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
      File file = ((ExplodedArchive.FileEntry)entry).getFile();
      return (Archive)(file.isDirectory() ? new ExplodedArchive(file) : new JarFileArchive(file));
   }

   public Archive getFilteredArchive(Archive.EntryRenameFilter filter) throws IOException {
      Map filteredEntries = new LinkedHashMap();
      Iterator i$ = this.entries.entrySet().iterator();

      while(i$.hasNext()) {
         Entry entry = (Entry)i$.next();
         AsciiBytes filteredName = filter.apply((AsciiBytes)entry.getKey(), (Archive.Entry)entry.getValue());
         if (filteredName != null) {
            filteredEntries.put(filteredName, new ExplodedArchive.FileEntry(filteredName, ((ExplodedArchive.FileEntry)entry.getValue()).getFile()));
         }
      }

      return new ExplodedArchive(this.root, filteredEntries);
   }

   private static class FileNotFoundURLConnection extends URLConnection {
      private final String name;

      public FileNotFoundURLConnection(URL url, String name) {
         super(url);
         this.name = name;
      }

      public void connect() throws IOException {
         throw new FileNotFoundException(this.name);
      }
   }

   private class FilteredURLStreamHandler extends URLStreamHandler {
      protected URLConnection openConnection(URL url) throws IOException {
         String name = url.getPath().substring(ExplodedArchive.this.root.toURI().getPath().length());
         return (URLConnection)(ExplodedArchive.this.entries.containsKey(new AsciiBytes(name)) ? (new URL(url.toString())).openConnection() : new ExplodedArchive.FileNotFoundURLConnection(url, name));
      }
   }

   private class FileEntry implements Archive.Entry {
      private final AsciiBytes name;
      private final File file;

      public FileEntry(AsciiBytes name, File file) {
         this.name = name;
         this.file = file;
      }

      public File getFile() {
         return this.file;
      }

      public boolean isDirectory() {
         return this.file.isDirectory();
      }

      public AsciiBytes getName() {
         return this.name;
      }
   }
}
