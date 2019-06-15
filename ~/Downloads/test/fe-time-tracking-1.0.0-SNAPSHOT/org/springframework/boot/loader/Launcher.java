package org.springframework.boot.loader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

public abstract class Launcher {
   protected Logger logger = Logger.getLogger(Launcher.class.getName());
   private static final String RUNNER_CLASS = Launcher.class.getPackage().getName() + ".MainMethodRunner";

   protected void launch(String[] args) {
      try {
         JarFile.registerUrlProtocolHandler();
         ClassLoader classLoader = this.createClassLoader(this.getClassPathArchives());
         this.launch(args, this.getMainClass(), classLoader);
      } catch (Exception var3) {
         var3.printStackTrace();
         System.exit(1);
      }

   }

   protected ClassLoader createClassLoader(List archives) throws Exception {
      List urls = new ArrayList(archives.size());
      Iterator i$ = archives.iterator();

      while(i$.hasNext()) {
         Archive archive = (Archive)i$.next();
         urls.add(archive.getUrl());
      }

      return this.createClassLoader((URL[])urls.toArray(new URL[urls.size()]));
   }

   protected ClassLoader createClassLoader(URL[] urls) throws Exception {
      return new LaunchedURLClassLoader(urls, this.getClass().getClassLoader());
   }

   protected void launch(String[] args, String mainClass, ClassLoader classLoader) throws Exception {
      Runnable runner = this.createMainMethodRunner(mainClass, args, classLoader);
      Thread runnerThread = new Thread(runner);
      runnerThread.setContextClassLoader(classLoader);
      runnerThread.setName(Thread.currentThread().getName());
      runnerThread.start();
   }

   protected Runnable createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) throws Exception {
      Class runnerClass = classLoader.loadClass(RUNNER_CLASS);
      Constructor constructor = runnerClass.getConstructor(String.class, String[].class);
      return (Runnable)constructor.newInstance(mainClass, args);
   }

   protected abstract String getMainClass() throws Exception;

   protected abstract List getClassPathArchives() throws Exception;

   protected final Archive createArchive() throws Exception {
      ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
      CodeSource codeSource = protectionDomain.getCodeSource();
      URI location = codeSource == null ? null : codeSource.getLocation().toURI();
      String path = location == null ? null : location.getSchemeSpecificPart();
      if (path == null) {
         throw new IllegalStateException("Unable to determine code source archive");
      } else {
         File root = new File(path);
         if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
         } else {
            return (Archive)(root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
         }
      }
   }
}
