package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.jar.JarEntryData;
import org.springframework.boot.loader.jar.JarEntryFilter;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarFileArchive extends Archive {
   private static final AsciiBytes UNPACK_MARKER = new AsciiBytes("UNPACK:");
   private static final int BUFFER_SIZE = 32768;
   private final JarFile jarFile;
   private final List entries;
   private URL url;

   public JarFileArchive(File file) throws IOException {
      this(file, (URL)null);
   }

   public JarFileArchive(File file, URL url) throws IOException {
      this(new JarFile(file));
      this.url = url;
   }

   public JarFileArchive(JarFile jarFile) {
      this.jarFile = jarFile;
      ArrayList jarFileEntries = new ArrayList();
      Iterator i$ = jarFile.iterator();

      while(i$.hasNext()) {
         JarEntryData data = (JarEntryData)i$.next();
         jarFileEntries.add(new JarFileArchive.JarFileEntry(data));
      }

      this.entries = Collections.unmodifiableList(jarFileEntries);
   }

   public URL getUrl() throws MalformedURLException {
      return this.url != null ? this.url : this.jarFile.getUrl();
   }

   public Manifest getManifest() throws IOException {
      return this.jarFile.getManifest();
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
      return Collections.unmodifiableCollection(this.entries);
   }

   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
      JarEntryData data = ((JarFileArchive.JarFileEntry)entry).getJarEntryData();
      if (data.getComment().startsWith(UNPACK_MARKER)) {
         return this.getUnpackedNestedArchive(data);
      } else {
         JarFile jarFile = this.jarFile.getNestedJarFile(data);
         return new JarFileArchive(jarFile);
      }
   }

   private Archive getUnpackedNestedArchive(JarEntryData data) throws IOException {
      AsciiBytes hash = data.getComment().substring(UNPACK_MARKER.length());
      String name = data.getName().toString();
      if (name.lastIndexOf("/") != -1) {
         name = name.substring(name.lastIndexOf("/") + 1);
      }

      File file = new File(this.getTempUnpackFolder(), hash.toString() + "-" + name);
      if (!file.exists() || file.length() != (long)data.getSize()) {
         this.unpack(data, file);
      }

      return new JarFileArchive(file, file.toURI().toURL());
   }

   private File getTempUnpackFolder() {
      File tempFolder = new File(System.getProperty("java.io.tmpdir"));
      File unpackFolder = new File(tempFolder, "spring-boot-libs");
      unpackFolder.mkdirs();
      return unpackFolder;
   }

   private void unpack(JarEntryData data, File file) throws IOException {
      InputStream inputStream = data.getData().getInputStream(RandomAccessData.ResourceAccess.ONCE);

      try {
         FileOutputStream outputStream = new FileOutputStream(file);

         try {
            byte[] buffer = new byte['耀'];
            boolean var6 = true;

            int bytesRead;
            while((bytesRead = inputStream.read(buffer)) != -1) {
               outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
         } finally {
            outputStream.close();
         }
      } finally {
         inputStream.close();
      }
   }

   public Archive getFilteredArchive(final Archive.EntryRenameFilter filter) throws IOException {
      JarFile filteredJar = this.jarFile.getFilteredJarFile(new JarEntryFilter() {
         public AsciiBytes apply(AsciiBytes name, JarEntryData entryData) {
            return filter.apply(name, new JarFileArchive.JarFileEntry(entryData));
         }
      });
      return new JarFileArchive(filteredJar);
   }

   private static class JarFileEntry implements Archive.Entry {
      private final JarEntryData entryData;

      public JarFileEntry(JarEntryData entryData) {
         this.entryData = entryData;
      }

      public JarEntryData getJarEntryData() {
         return this.entryData;
      }

      public boolean isDirectory() {
         return this.entryData.isDirectory();
      }

      public AsciiBytes getName() {
         return this.entryData.getName();
      }
   }
}
