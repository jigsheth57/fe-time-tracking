package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.data.RandomAccessDataFile;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarFile extends java.util.jar.JarFile implements Iterable {
   private static final AsciiBytes META_INF = new AsciiBytes("META-INF/");
   private static final AsciiBytes MANIFEST_MF = new AsciiBytes("META-INF/MANIFEST.MF");
   private static final AsciiBytes SIGNATURE_FILE_EXTENSION = new AsciiBytes(".SF");
   private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
   private static final String HANDLERS_PACKAGE = "org.springframework.boot.loader";
   private static final AsciiBytes SLASH = new AsciiBytes("/");
   private final RandomAccessDataFile rootFile;
   private final String pathFromRoot;
   private final RandomAccessData data;
   private final List entries;
   private SoftReference entriesByName;
   private boolean signed;
   private JarEntryData manifestEntry;
   private SoftReference manifest;
   private URL url;

   public JarFile(File file) throws IOException {
      this(new RandomAccessDataFile(file));
   }

   JarFile(RandomAccessDataFile file) throws IOException {
      this(file, "", file);
   }

   private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data) throws IOException {
      super(rootFile.getFile());
      CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
      this.rootFile = rootFile;
      this.pathFromRoot = pathFromRoot;
      this.data = this.getArchiveData(endRecord, data);
      this.entries = this.loadJarEntries(endRecord);
   }

   private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, List entries, JarEntryFilter... filters) throws IOException {
      super(rootFile.getFile());
      this.rootFile = rootFile;
      this.pathFromRoot = pathFromRoot;
      this.data = data;
      this.entries = this.filterEntries(entries, filters);
   }

   private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data) {
      long offset = endRecord.getStartOfArchive(data);
      return offset == 0L ? data : data.getSubsection(offset, data.getSize() - offset);
   }

   private List loadJarEntries(CentralDirectoryEndRecord endRecord) throws IOException {
      RandomAccessData centralDirectory = endRecord.getCentralDirectory(this.data);
      int numberOfRecords = endRecord.getNumberOfRecords();
      List entries = new ArrayList(numberOfRecords);
      InputStream inputStream = centralDirectory.getInputStream(RandomAccessData.ResourceAccess.ONCE);

      try {
         for(JarEntryData entry = JarEntryData.fromInputStream(this, inputStream); entry != null; entry = JarEntryData.fromInputStream(this, inputStream)) {
            entries.add(entry);
            this.processEntry(entry);
         }
      } finally {
         inputStream.close();
      }

      return entries;
   }

   private List filterEntries(List entries, JarEntryFilter[] filters) {
      List filteredEntries = new ArrayList(entries.size());
      Iterator i$ = entries.iterator();

      while(i$.hasNext()) {
         JarEntryData entry = (JarEntryData)i$.next();
         AsciiBytes name = entry.getName();
         JarEntryFilter[] arr$ = filters;
         int len$ = filters.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            JarEntryFilter filter = arr$[i$];
            name = filter != null && name != null ? filter.apply(name, entry) : name;
         }

         if (name != null) {
            JarEntryData filteredCopy = entry.createFilteredCopy(this, name);
            filteredEntries.add(filteredCopy);
            this.processEntry(filteredCopy);
         }
      }

      return filteredEntries;
   }

   private void processEntry(JarEntryData entry) {
      AsciiBytes name = entry.getName();
      if (name.startsWith(META_INF)) {
         this.processMetaInfEntry(name, entry);
      }

   }

   private void processMetaInfEntry(AsciiBytes name, JarEntryData entry) {
      if (name.equals(MANIFEST_MF)) {
         this.manifestEntry = entry;
      }

      if (name.endsWith(SIGNATURE_FILE_EXTENSION)) {
         this.signed = true;
      }

   }

   protected final RandomAccessDataFile getRootJarFile() {
      return this.rootFile;
   }

   RandomAccessData getData() {
      return this.data;
   }

   public Manifest getManifest() throws IOException {
      if (this.manifestEntry == null) {
         return null;
      } else {
         Manifest manifest = this.manifest == null ? null : (Manifest)this.manifest.get();
         if (manifest == null) {
            InputStream inputStream = this.manifestEntry.getInputStream();

            try {
               manifest = new Manifest(inputStream);
            } finally {
               inputStream.close();
            }

            this.manifest = new SoftReference(manifest);
         }

         return manifest;
      }
   }

   public Enumeration entries() {
      final Iterator iterator = this.iterator();
      return new Enumeration() {
         public boolean hasMoreElements() {
            return iterator.hasNext();
         }

         public java.util.jar.JarEntry nextElement() {
            return ((JarEntryData)iterator.next()).asJarEntry();
         }
      };
   }

   public Iterator iterator() {
      return this.entries.iterator();
   }

   public JarEntry getJarEntry(String name) {
      return (JarEntry)this.getEntry(name);
   }

   public ZipEntry getEntry(String name) {
      JarEntryData jarEntryData = this.getJarEntryData(name);
      return jarEntryData == null ? null : jarEntryData.asJarEntry();
   }

   public JarEntryData getJarEntryData(String name) {
      return name == null ? null : this.getJarEntryData(new AsciiBytes(name));
   }

   public JarEntryData getJarEntryData(AsciiBytes name) {
      if (name == null) {
         return null;
      } else {
         Map entriesByName = this.entriesByName == null ? null : (Map)this.entriesByName.get();
         if (entriesByName == null) {
            entriesByName = new HashMap();
            Iterator i$ = this.entries.iterator();

            while(i$.hasNext()) {
               JarEntryData entry = (JarEntryData)i$.next();
               ((Map)entriesByName).put(entry.getName(), entry);
            }

            this.entriesByName = new SoftReference(entriesByName);
         }

         JarEntryData entryData = (JarEntryData)((Map)entriesByName).get(name);
         if (entryData == null && !name.endsWith(SLASH)) {
            entryData = (JarEntryData)((Map)entriesByName).get(name.append(SLASH));
         }

         return entryData;
      }
   }

   boolean isSigned() {
      return this.signed;
   }

   void setupEntryCertificates() {
      try {
         JarInputStream inputStream = new JarInputStream(this.getData().getInputStream(RandomAccessData.ResourceAccess.ONCE));

         try {
            for(java.util.jar.JarEntry entry = inputStream.getNextJarEntry(); entry != null; entry = inputStream.getNextJarEntry()) {
               inputStream.closeEntry();
               JarEntry jarEntry = this.getJarEntry(entry.getName());
               if (jarEntry != null) {
                  jarEntry.setupCertificates(entry);
               }
            }
         } finally {
            inputStream.close();
         }

      } catch (IOException var8) {
         throw new IllegalStateException(var8);
      }
   }

   public synchronized InputStream getInputStream(ZipEntry ze) throws IOException {
      return this.getContainedEntry(ze).getSource().getInputStream();
   }

   public synchronized JarFile getNestedJarFile(ZipEntry ze) throws IOException {
      return this.getNestedJarFile(this.getContainedEntry(ze).getSource());
   }

   public synchronized JarFile getNestedJarFile(JarEntryData sourceEntry) throws IOException {
      try {
         if (sourceEntry.nestedJar == null) {
            sourceEntry.nestedJar = this.createJarFileFromEntry(sourceEntry);
         }

         return sourceEntry.nestedJar;
      } catch (IOException var3) {
         throw new IOException("Unable to open nested jar file '" + sourceEntry.getName() + "'", var3);
      }
   }

   private JarFile createJarFileFromEntry(JarEntryData sourceEntry) throws IOException {
      return sourceEntry.isDirectory() ? this.createJarFileFromDirectoryEntry(sourceEntry) : this.createJarFileFromFileEntry(sourceEntry);
   }

   private JarFile createJarFileFromDirectoryEntry(JarEntryData sourceEntry) throws IOException {
      final AsciiBytes sourceName = sourceEntry.getName();
      JarEntryFilter filter = new JarEntryFilter() {
         public AsciiBytes apply(AsciiBytes name, JarEntryData entryData) {
            return name.startsWith(sourceName) && !name.equals(sourceName) ? name.substring(sourceName.length()) : null;
         }
      };
      return new JarFile(this.rootFile, this.pathFromRoot + "!/" + sourceEntry.getName().substring(0, sourceName.length() - 1), this.data, this.entries, new JarEntryFilter[]{filter});
   }

   private JarFile createJarFileFromFileEntry(JarEntryData sourceEntry) throws IOException {
      if (sourceEntry.getMethod() != 0) {
         throw new IllegalStateException("Unable to open nested entry '" + sourceEntry.getName() + "'. It has been compressed and nested " + "jar files must be stored without compression. Please check the " + "mechanism used to create your executable jar file");
      } else {
         return new JarFile(this.rootFile, this.pathFromRoot + "!/" + sourceEntry.getName(), sourceEntry.getData());
      }
   }

   public synchronized JarFile getFilteredJarFile(JarEntryFilter... filters) throws IOException {
      return new JarFile(this.rootFile, this.pathFromRoot, this.data, this.entries, filters);
   }

   private JarEntry getContainedEntry(ZipEntry zipEntry) throws IOException {
      if (zipEntry instanceof JarEntry && ((JarEntry)zipEntry).getSource().getSource() == this) {
         return (JarEntry)zipEntry;
      } else {
         throw new IllegalArgumentException("ZipEntry must be contained in this file");
      }
   }

   public int size() {
      return (int)this.data.getSize();
   }

   public void close() throws IOException {
      this.rootFile.close();
   }

   public URL getUrl() throws MalformedURLException {
      if (this.url == null) {
         Handler handler = new Handler(this);
         String file = this.rootFile.getFile().toURI() + this.pathFromRoot + "!/";
         file = file.replace("file:////", "file://");
         this.url = new URL("jar", "", -1, file, handler);
      }

      return this.url;
   }

   public String toString() {
      return this.getName();
   }

   public String getName() {
      String path = this.pathFromRoot;
      return this.rootFile.getFile() + path;
   }

   public static void registerUrlProtocolHandler() {
      String handlers = System.getProperty("java.protocol.handler.pkgs");
      System.setProperty("java.protocol.handler.pkgs", "".equals(handlers) ? "org.springframework.boot.loader" : handlers + "|" + "org.springframework.boot.loader");
      resetCachedUrlHandlers();
   }

   private static void resetCachedUrlHandlers() {
      try {
         URL.setURLStreamHandlerFactory((URLStreamHandlerFactory)null);
      } catch (Error var1) {
         ;
      }

   }
}
