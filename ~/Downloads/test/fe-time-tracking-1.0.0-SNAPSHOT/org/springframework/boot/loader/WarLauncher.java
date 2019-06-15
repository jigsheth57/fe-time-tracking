package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.util.AsciiBytes;

public class WarLauncher extends ExecutableArchiveLauncher {
   private static final AsciiBytes WEB_INF = new AsciiBytes("WEB-INF/");
   private static final AsciiBytes WEB_INF_CLASSES;
   private static final AsciiBytes WEB_INF_LIB;
   private static final AsciiBytes WEB_INF_LIB_PROVIDED;

   public WarLauncher() {
   }

   WarLauncher(Archive archive) {
      super(archive);
   }

   public boolean isNestedArchive(Archive.Entry entry) {
      if (entry.isDirectory()) {
         return entry.getName().equals(WEB_INF_CLASSES);
      } else {
         return entry.getName().startsWith(WEB_INF_LIB) || entry.getName().startsWith(WEB_INF_LIB_PROVIDED);
      }
   }

   public static void main(String[] args) {
      (new WarLauncher()).launch(args);
   }

   static {
      WEB_INF_CLASSES = WEB_INF.append("classes/");
      WEB_INF_LIB = WEB_INF.append("lib/");
      WEB_INF_LIB_PROVIDED = WEB_INF.append("lib-provided/");
   }
}
