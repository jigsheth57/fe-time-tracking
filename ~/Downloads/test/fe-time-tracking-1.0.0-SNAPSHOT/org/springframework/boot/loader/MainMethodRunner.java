package org.springframework.boot.loader;

import java.lang.reflect.Method;

public class MainMethodRunner implements Runnable {
   private final String mainClassName;
   private final String[] args;

   public MainMethodRunner(String mainClass, String[] args) {
      this.mainClassName = mainClass;
      this.args = args == null ? null : (String[])args.clone();
   }

   public void run() {
      try {
         Class mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
         Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
         if (mainMethod == null) {
            throw new IllegalStateException(this.mainClassName + " does not have a main method");
         }

         mainMethod.invoke((Object)null, this.args);
      } catch (Exception var3) {
         var3.printStackTrace();
         System.exit(1);
      }

   }
}
