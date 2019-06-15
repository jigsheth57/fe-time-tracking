package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.boot.loader.data.RandomAccessData;

class Bytes {
   private static final byte[] EMPTY_BYTES = new byte[0];

   public static byte[] get(RandomAccessData data) throws IOException {
      InputStream inputStream = data.getInputStream(RandomAccessData.ResourceAccess.ONCE);

      byte[] var2;
      try {
         var2 = get(inputStream, data.getSize());
      } finally {
         inputStream.close();
      }

      return var2;
   }

   public static byte[] get(InputStream inputStream, long length) throws IOException {
      if (length == 0L) {
         return EMPTY_BYTES;
      } else {
         byte[] bytes = new byte[(int)length];
         if (!fill(inputStream, bytes)) {
            throw new IOException("Unable to read bytes");
         } else {
            return bytes;
         }
      }
   }

   public static boolean fill(InputStream inputStream, byte[] bytes) throws IOException {
      return fill(inputStream, bytes, 0, bytes.length);
   }

   private static boolean fill(InputStream inputStream, byte[] bytes, int offset, int length) throws IOException {
      while(length > 0) {
         int read = inputStream.read(bytes, offset, length);
         if (read == -1) {
            return false;
         }

         offset += read;
         length = -read;
      }

      return true;
   }

   public static long littleEndianValue(byte[] bytes, int offset, int length) {
      long value = 0L;

      for(int i = length - 1; i >= 0; --i) {
         value = value << 8 | (long)(bytes[offset + i] & 255);
      }

      return value;
   }
}
