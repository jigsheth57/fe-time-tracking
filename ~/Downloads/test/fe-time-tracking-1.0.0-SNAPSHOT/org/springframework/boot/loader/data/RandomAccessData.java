package org.springframework.boot.loader.data;

import java.io.IOException;
import java.io.InputStream;

public interface RandomAccessData {
   InputStream getInputStream(RandomAccessData.ResourceAccess var1) throws IOException;

   RandomAccessData getSubsection(long var1, long var3);

   long getSize();

   public static enum ResourceAccess {
      ONCE,
      PER_READ;
   }
}
