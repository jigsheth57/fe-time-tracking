package org.springframework.boot.loader.jar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.util.AsciiBytes;

class JarURLConnection extends java.net.JarURLConnection {
   private static final FileNotFoundException FILE_NOT_FOUND_EXCEPTION = new FileNotFoundException();
   private static final String SEPARATOR = "!/";
   private static final URL EMPTY_JAR_URL;
   private static final JarURLConnection.JarEntryName EMPTY_JAR_ENTRY_NAME;
   private static ThreadLocal useFastExceptions;
   private final JarFile jarFile;
   private JarEntryData jarEntryData;
   private URL jarFileUrl;
   private JarURLConnection.JarEntryName jarEntryName;

   protected JarURLConnection(URL url, JarFile jarFile) throws IOException {
      super(EMPTY_JAR_URL);
      this.url = url;

      String spec;
      int separator;
      for(spec = url.getFile().substring(jarFile.getUrl().getFile().length()); (separator = spec.indexOf("!/")) > 0; spec = spec.substring(separator + "!/".length())) {
         jarFile = this.getNestedJarFile(jarFile, spec.substring(0, separator));
      }

      this.jarFile = jarFile;
      this.jarEntryName = this.getJarEntryName(spec);
   }

   private JarFile getNestedJarFile(JarFile jarFile, String name) throws IOException {
      JarEntry jarEntry = jarFile.getJarEntry(name);
      if (jarEntry == null) {
         this.throwFileNotFound(jarEntry, jarFile);
      }

      return jarFile.getNestedJarFile((ZipEntry)jarEntry);
   }

   private JarURLConnection.JarEntryName getJarEntryName(String spec) {
      return spec.length() == 0 ? EMPTY_JAR_ENTRY_NAME : new JarURLConnection.JarEntryName(spec);
   }

   public void connect() throws IOException {
      if (!this.jarEntryName.isEmpty()) {
         this.jarEntryData = this.jarFile.getJarEntryData(this.jarEntryName.asAsciiBytes());
         if (this.jarEntryData == null) {
            this.throwFileNotFound(this.jarEntryName, this.jarFile);
         }
      }

      this.connected = true;
   }

   private void throwFileNotFound(Object entry, JarFile jarFile) throws FileNotFoundException {
      if (Boolean.TRUE.equals(useFastExceptions.get())) {
         throw FILE_NOT_FOUND_EXCEPTION;
      } else {
         throw new FileNotFoundException("JAR entry " + entry + " not found in " + jarFile.getName());
      }
   }

   public Manifest getManifest() throws IOException {
      Manifest var1;
      try {
         var1 = super.getManifest();
      } finally {
         this.connected = false;
      }

      return var1;
   }

   public JarFile getJarFile() throws IOException {
      this.connect();
      return this.jarFile;
   }

   public URL getJarFileURL() {
      if (this.jarFileUrl == null) {
         this.jarFileUrl = this.buildJarFileUrl();
      }

      return this.jarFileUrl;
   }

   private URL buildJarFileUrl() {
      try {
         String spec = this.jarFile.getUrl().getFile();
         if (spec.endsWith("!/")) {
            spec = spec.substring(0, spec.length() - "!/".length());
         }

         return spec.indexOf("!/") == -1 ? new URL(spec) : new URL("jar:" + spec);
      } catch (MalformedURLException var2) {
         throw new IllegalStateException(var2);
      }
   }

   public JarEntry getJarEntry() throws IOException {
      this.connect();
      return this.jarEntryData == null ? null : this.jarEntryData.asJarEntry();
   }

   public String getEntryName() {
      return this.jarEntryName.toString();
   }

   public InputStream getInputStream() throws IOException {
      this.connect();
      if (this.jarEntryName.isEmpty()) {
         throw new IOException("no entry name specified");
      } else {
         return this.jarEntryData.getInputStream();
      }
   }

   public int getContentLength() {
      try {
         this.connect();
         return this.jarEntryData != null ? this.jarEntryData.getSize() : this.jarFile.size();
      } catch (IOException var2) {
         return -1;
      }
   }

   public Object getContent() throws IOException {
      this.connect();
      return this.jarEntryData == null ? this.jarFile : super.getContent();
   }

   public String getContentType() {
      return this.jarEntryName.getContentType();
   }

   static void setUseFastExceptions(boolean useFastExceptions) {
      useFastExceptions.set(useFastExceptions);
   }

   static {
      try {
         EMPTY_JAR_URL = new URL("jar:", (String)null, 0, "file:!/", new URLStreamHandler() {
            protected URLConnection openConnection(URL u) throws IOException {
               return null;
            }
         });
      } catch (MalformedURLException var1) {
         throw new IllegalStateException(var1);
      }

      EMPTY_JAR_ENTRY_NAME = new JarURLConnection.JarEntryName("");
      useFastExceptions = new ThreadLocal();
   }

   private static class JarEntryName {
      private final AsciiBytes name;
      private String contentType;

      public JarEntryName(String spec) {
         this.name = this.decode(spec);
      }

      private AsciiBytes decode(String source) {
         int length = source == null ? 0 : source.length();
         if (length != 0 && source.indexOf(37) >= 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(length);

            for(int i = 0; i < length; ++i) {
               int ch = source.charAt(i);
               if (ch == '%') {
                  if (i + 2 >= length) {
                     throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                  }

                  ch = this.decodeEscapeSequence(source, i);
                  i += 2;
               }

               bos.write(ch);
            }

            return new AsciiBytes(bos.toByteArray());
         } else {
            return new AsciiBytes(source);
         }
      }

      private char decodeEscapeSequence(String source, int i) {
         int hi = Character.digit(source.charAt(i + 1), 16);
         int lo = Character.digit(source.charAt(i + 2), 16);
         if (hi != -1 && lo != -1) {
            return (char)((hi << 4) + lo);
         } else {
            throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
         }
      }

      public String toString() {
         return this.name.toString();
      }

      public AsciiBytes asAsciiBytes() {
         return this.name;
      }

      public boolean isEmpty() {
         return this.name.length() == 0;
      }

      public String getContentType() {
         if (this.contentType == null) {
            this.contentType = this.deduceContentType();
         }

         return this.contentType;
      }

      private String deduceContentType() {
         String type = this.isEmpty() ? "x-java/jar" : null;
         type = type != null ? type : URLConnection.guessContentTypeFromName(this.toString());
         type = type != null ? type : "content/unknown";
         return type;
      }
   }
}
