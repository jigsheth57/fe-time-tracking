package org.springframework.boot.loader;

import java.util.List;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarLauncher extends ExecutableArchiveLauncher {
   private static final AsciiBytes LIB = new AsciiBytes("lib/");

   protected boolean isNestedArchive(Archive.Entry entry) {
      return !entry.isDirectory() && entry.getName().startsWith(LIB);
   }

   protected void postProcessClassPathArchives(List archives) throws Exception {
      archives.add(0, this.getArchive());
   }

   public static void main(String[] args) {
      (new JarLauncher()).launch(args);
   }
}
