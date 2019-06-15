package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler extends URLStreamHandler {
   private static final String FILE_PROTOCOL = "file:";
   private static final String SEPARATOR = "!/";
   private static final String[] FALLBACK_HANDLERS = new String[]{"sun.net.www.protocol.jar.Handler"};
   private static final Method OPEN_CONNECTION_METHOD;
   private static SoftReference rootFileCache;
   private final Logger logger;
   private final JarFile jarFile;
   private URLStreamHandler fallbackHandler;

   public Handler() {
      this((JarFile)null);
   }

   public Handler(JarFile jarFile) {
      this.logger = Logger.getLogger(this.getClass().getName());
      this.jarFile = jarFile;
   }

   protected URLConnection openConnection(URL url) throws IOException {
      if (this.jarFile != null) {
         return new JarURLConnection(url, this.jarFile);
      } else {
         try {
            return new JarURLConnection(url, this.getRootJarFileFromUrl(url));
         } catch (Exception var3) {
            return this.openFallbackConnection(url, var3);
         }
      }
   }

   private URLConnection openFallbackConnection(URL url, Exception reason) throws IOException {
      try {
         return this.openConnection(this.getFallbackHandler(), url);
      } catch (Exception var4) {
         if (reason instanceof IOException) {
            this.logger.log(Level.FINEST, "Unable to open fallback handler", var4);
            throw (IOException)reason;
         } else {
            this.logger.log(Level.WARNING, "Unable to open fallback handler", var4);
            if (reason instanceof RuntimeException) {
               throw (RuntimeException)reason;
            } else {
               throw new IllegalStateException(reason);
            }
         }
      }
   }

   private URLStreamHandler getFallbackHandler() {
      if (this.fallbackHandler != null) {
         return this.fallbackHandler;
      } else {
         String[] arr$ = FALLBACK_HANDLERS;
         int len$ = arr$.length;
         int i$ = 0;

         while(i$ < len$) {
            String handlerClassName = arr$[i$];

            try {
               Class handlerClass = Class.forName(handlerClassName);
               this.fallbackHandler = (URLStreamHandler)handlerClass.newInstance();
               return this.fallbackHandler;
            } catch (Exception var6) {
               ++i$;
            }
         }

         throw new IllegalStateException("Unable to find fallback handler");
      }
   }

   private URLConnection openConnection(URLStreamHandler handler, URL url) throws Exception {
      if (OPEN_CONNECTION_METHOD == null) {
         throw new IllegalStateException("Unable to invoke fallback open connection method");
      } else {
         OPEN_CONNECTION_METHOD.setAccessible(true);
         return (URLConnection)OPEN_CONNECTION_METHOD.invoke(handler, url);
      }
   }

   public JarFile getRootJarFileFromUrl(URL url) throws IOException {
      String spec = url.getFile();
      int separatorIndex = spec.indexOf("!/");
      if (separatorIndex == -1) {
         throw new MalformedURLException("Jar URL does not contain !/ separator");
      } else {
         String name = spec.substring(0, separatorIndex);
         return this.getRootJarFile(name);
      }
   }

   private JarFile getRootJarFile(String name) throws IOException {
      try {
         if (!name.startsWith("file:")) {
            throw new IllegalStateException("Not a file URL");
         } else {
            String path = name.substring("file:".length());
            File file = new File(URLDecoder.decode(path, "UTF-8"));
            Map cache = (Map)rootFileCache.get();
            JarFile jarFile = cache == null ? null : (JarFile)cache.get(file);
            if (jarFile == null) {
               jarFile = new JarFile(file);
               addToRootFileCache(file, jarFile);
            }

            return jarFile;
         }
      } catch (Exception var6) {
         throw new IOException("Unable to open root Jar file '" + name + "'", var6);
      }
   }

   static void addToRootFileCache(File sourceFile, JarFile jarFile) {
      Map cache = (Map)rootFileCache.get();
      if (cache == null) {
         cache = new ConcurrentHashMap();
         rootFileCache = new SoftReference(cache);
      }

      ((Map)cache).put(sourceFile, jarFile);
   }

   public static void setUseFastConnectionExceptions(boolean useFastConnectionExceptions) {
      JarURLConnection.setUseFastExceptions(useFastConnectionExceptions);
   }

   static {
      Method method = null;

      try {
         method = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class);
      } catch (Exception var2) {
         ;
      }

      OPEN_CONNECTION_METHOD = method;
      rootFileCache = new SoftReference((Object)null);
   }
}
