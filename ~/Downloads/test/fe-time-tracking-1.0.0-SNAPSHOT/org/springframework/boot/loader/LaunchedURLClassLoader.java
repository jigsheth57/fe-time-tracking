package org.springframework.boot.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.boot.loader.jar.JarFile;

public class LaunchedURLClassLoader extends URLClassLoader {
   private static LaunchedURLClassLoader.LockProvider LOCK_PROVIDER = setupLockProvider();
   private final ClassLoader rootClassLoader;

   public LaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
      this.rootClassLoader = this.findRootClassLoader(parent);
   }

   private ClassLoader findRootClassLoader(ClassLoader classLoader) {
      while(classLoader != null) {
         if (classLoader.getParent() == null) {
            return classLoader;
         }

         classLoader = classLoader.getParent();
      }

      return null;
   }

   public URL getResource(String name) {
      URL url = null;
      if (this.rootClassLoader != null) {
         url = this.rootClassLoader.getResource(name);
      }

      return url == null ? this.findResource(name) : url;
   }

   public URL findResource(String name) {
      try {
         return name.equals("") && this.hasURLs() ? this.getURLs()[0] : super.findResource(name);
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }

   public Enumeration findResources(String name) throws IOException {
      return name.equals("") && this.hasURLs() ? Collections.enumeration(Arrays.asList(this.getURLs())) : super.findResources(name);
   }

   private boolean hasURLs() {
      return this.getURLs().length > 0;
   }

   public Enumeration getResources(String name) throws IOException {
      if (this.rootClassLoader == null) {
         return this.findResources(name);
      } else {
         final Enumeration rootResources = this.rootClassLoader.getResources(name);
         final Enumeration localResources = this.findResources(name);
         return new Enumeration() {
            public boolean hasMoreElements() {
               return rootResources.hasMoreElements() || localResources.hasMoreElements();
            }

            public URL nextElement() {
               return rootResources.hasMoreElements() ? (URL)rootResources.nextElement() : (URL)localResources.nextElement();
            }
         };
      }
   }

   protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
      synchronized(LOCK_PROVIDER.getLock(this, name)) {
         Class loadedClass = this.findLoadedClass(name);
         if (loadedClass == null) {
            Handler.setUseFastConnectionExceptions(true);

            try {
               loadedClass = this.doLoadClass(name);
            } finally {
               Handler.setUseFastConnectionExceptions(false);
            }
         }

         if (resolve) {
            this.resolveClass(loadedClass);
         }

         return loadedClass;
      }
   }

   private Class doLoadClass(String name) throws ClassNotFoundException {
      try {
         if (this.rootClassLoader != null) {
            return this.rootClassLoader.loadClass(name);
         }
      } catch (Exception var4) {
         ;
      }

      try {
         this.findPackage(name);
         Class cls = this.findClass(name);
         return cls;
      } catch (Exception var3) {
         return super.loadClass(name, false);
      }
   }

   private void findPackage(String name) throws ClassNotFoundException {
      int lastDot = name.lastIndexOf(46);
      if (lastDot != -1) {
         String packageName = name.substring(0, lastDot);
         if (this.getPackage(packageName) == null) {
            try {
               this.definePackageForFindClass(name, packageName);
            } catch (Exception var5) {
               ;
            }
         }
      }

   }

   private void definePackageForFindClass(final String name, final String packageName) {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws ClassNotFoundException {
               String path = name.replace('.', '/').concat(".class");
               URL[] arr$ = LaunchedURLClassLoader.this.getURLs();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  URL url = arr$[i$];

                  try {
                     if (url.getContent() instanceof JarFile) {
                        JarFile jarFile = (JarFile)url.getContent();
                        if (jarFile.getJarEntryData(path) != null && jarFile.getManifest() != null) {
                           LaunchedURLClassLoader.this.definePackage(packageName, jarFile.getManifest(), url);
                           return null;
                        }
                     }
                  } catch (IOException var7) {
                     ;
                  }
               }

               return null;
            }
         }, AccessController.getContext());
      } catch (PrivilegedActionException var4) {
         ;
      }

   }

   private static LaunchedURLClassLoader.LockProvider setupLockProvider() {
      try {
         ClassLoader.registerAsParallelCapable();
         return new LaunchedURLClassLoader.Java7LockProvider();
      } catch (NoSuchMethodError var1) {
         return new LaunchedURLClassLoader.LockProvider();
      }
   }

   private static class Java7LockProvider extends LaunchedURLClassLoader.LockProvider {
      private Java7LockProvider() {
         super(null);
      }

      public Object getLock(LaunchedURLClassLoader classLoader, String className) {
         return classLoader.getClassLoadingLock(className);
      }

      // $FF: synthetic method
      Java7LockProvider(Object x0) {
         this();
      }
   }

   private static class LockProvider {
      private LockProvider() {
      }

      public Object getLock(LaunchedURLClassLoader classLoader, String className) {
         return classLoader;
      }

      // $FF: synthetic method
      LockProvider(Object x0) {
         this();
      }
   }
}
