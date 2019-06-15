package org.springframework.boot.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.loader.archive.Archive;

public abstract class ExecutableArchiveLauncher extends Launcher {
   private final Archive archive;
   private final JavaAgentDetector javaAgentDetector;

   public ExecutableArchiveLauncher() {
      this((JavaAgentDetector)(new InputArgumentsJavaAgentDetector()));
   }

   public ExecutableArchiveLauncher(JavaAgentDetector javaAgentDetector) {
      try {
         this.archive = this.createArchive();
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }

      this.javaAgentDetector = javaAgentDetector;
   }

   ExecutableArchiveLauncher(Archive archive) {
      this.javaAgentDetector = new InputArgumentsJavaAgentDetector();
      this.archive = archive;
   }

   protected final Archive getArchive() {
      return this.archive;
   }

   protected String getMainClass() throws Exception {
      return this.archive.getMainClass();
   }

   protected List getClassPathArchives() throws Exception {
      List archives = new ArrayList(this.archive.getNestedArchives(new Archive.EntryFilter() {
         public boolean matches(Archive.Entry entry) {
            return ExecutableArchiveLauncher.this.isNestedArchive(entry);
         }
      }));
      this.postProcessClassPathArchives(archives);
      return archives;
   }

   protected ClassLoader createClassLoader(URL[] urls) throws Exception {
      Set copy = new LinkedHashSet(urls.length);
      ClassLoader loader = getDefaultClassLoader();
      if (loader instanceof URLClassLoader) {
         URL[] arr$ = ((URLClassLoader)loader).getURLs();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            URL url = arr$[i$];
            if (this.addDefaultClassloaderUrl(urls, url)) {
               copy.add(url);
            }
         }
      }

      Collections.addAll(copy, urls);
      return super.createClassLoader((URL[])copy.toArray(new URL[copy.size()]));
   }

   private boolean addDefaultClassloaderUrl(URL[] urls, URL url) {
      String jarUrl = "jar:" + url + "!/";
      URL[] arr$ = urls;
      int len$ = urls.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         URL nestedUrl = arr$[i$];
         if (nestedUrl.equals(url) || nestedUrl.toString().equals(jarUrl)) {
            return false;
         }
      }

      return !this.javaAgentDetector.isJavaAgentJar(url);
   }

   protected abstract boolean isNestedArchive(Archive.Entry var1);

   protected void postProcessClassPathArchives(List archives) throws Exception {
   }

   private static ClassLoader getDefaultClassLoader() {
      ClassLoader classloader = null;

      try {
         classloader = Thread.currentThread().getContextClassLoader();
      } catch (Throwable var2) {
         ;
      }

      if (classloader == null) {
         classloader = ExecutableArchiveLauncher.class.getClassLoader();
      }

      return classloader;
   }
}
