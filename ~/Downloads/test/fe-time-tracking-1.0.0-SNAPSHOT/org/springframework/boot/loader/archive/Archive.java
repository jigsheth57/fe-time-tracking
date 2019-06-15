package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public abstract class Archive {
   public abstract URL getUrl() throws MalformedURLException;

   public String getMainClass() throws Exception {
      Manifest manifest = this.getManifest();
      String mainClass = null;
      if (manifest != null) {
         mainClass = manifest.getMainAttributes().getValue("Start-Class");
      }

      if (mainClass == null) {
         throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
      } else {
         return mainClass;
      }
   }

   public String toString() {
      try {
         return this.getUrl().toString();
      } catch (Exception var2) {
         return "archive";
      }
   }

   public abstract Manifest getManifest() throws IOException;

   public abstract Collection getEntries();

   public abstract List getNestedArchives(Archive.EntryFilter var1) throws IOException;

   public abstract Archive getFilteredArchive(Archive.EntryRenameFilter var1) throws IOException;

   public interface EntryRenameFilter {
      AsciiBytes apply(AsciiBytes var1, Archive.Entry var2);
   }

   public interface EntryFilter {
      boolean matches(Archive.Entry var1);
   }

   public interface Entry {
      boolean isDirectory();

      AsciiBytes getName();
   }
}
