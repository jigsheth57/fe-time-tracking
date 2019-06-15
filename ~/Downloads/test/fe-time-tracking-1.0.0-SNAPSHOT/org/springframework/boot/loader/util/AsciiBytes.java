package org.springframework.boot.loader.util;

import java.nio.charset.Charset;

public final class AsciiBytes {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private static final int INITIAL_HASH = 7;
   private static final int MULTIPLIER = 31;
   private final byte[] bytes;
   private final int offset;
   private final int length;
   private String string;

   public AsciiBytes(String string) {
      this(string.getBytes(UTF_8));
      this.string = string;
   }

   public AsciiBytes(byte[] bytes) {
      this(bytes, 0, bytes.length);
   }

   public AsciiBytes(byte[] bytes, int offset, int length) {
      if (offset >= 0 && length >= 0 && offset + length <= bytes.length) {
         this.bytes = bytes;
         this.offset = offset;
         this.length = length;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int length() {
      return this.length;
   }

   public boolean startsWith(AsciiBytes prefix) {
      if (this == prefix) {
         return true;
      } else if (prefix.length > this.length) {
         return false;
      } else {
         for(int i = 0; i < prefix.length; ++i) {
            if (this.bytes[i + this.offset] != prefix.bytes[i + prefix.offset]) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean endsWith(AsciiBytes postfix) {
      if (this == postfix) {
         return true;
      } else if (postfix.length > this.length) {
         return false;
      } else {
         for(int i = 0; i < postfix.length; ++i) {
            if (this.bytes[this.offset + (this.length - 1) - i] != postfix.bytes[postfix.offset + (postfix.length - 1) - i]) {
               return false;
            }
         }

         return true;
      }
   }

   public AsciiBytes substring(int beginIndex) {
      return this.substring(beginIndex, this.length);
   }

   public AsciiBytes substring(int beginIndex, int endIndex) {
      int length = endIndex - beginIndex;
      if (this.offset + length > this.length) {
         throw new IndexOutOfBoundsException();
      } else {
         return new AsciiBytes(this.bytes, this.offset + beginIndex, length);
      }
   }

   public AsciiBytes append(String string) {
      return string != null && string.length() != 0 ? this.append(string.getBytes(UTF_8)) : this;
   }

   public AsciiBytes append(AsciiBytes asciiBytes) {
      return asciiBytes != null && asciiBytes.length() != 0 ? this.append(asciiBytes.bytes) : this;
   }

   public AsciiBytes append(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         byte[] combined = new byte[this.length + bytes.length];
         System.arraycopy(this.bytes, this.offset, combined, 0, this.length);
         System.arraycopy(bytes, 0, combined, this.length, bytes.length);
         return new AsciiBytes(combined);
      } else {
         return this;
      }
   }

   public String toString() {
      if (this.string == null) {
         this.string = new String(this.bytes, this.offset, this.length, UTF_8);
      }

      return this.string;
   }

   public int hashCode() {
      int hash = 7;

      for(int i = 0; i < this.length; ++i) {
         hash = 31 * hash + this.bytes[this.offset + i];
      }

      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this == obj) {
         return true;
      } else {
         if (obj.getClass().equals(AsciiBytes.class)) {
            AsciiBytes other = (AsciiBytes)obj;
            if (this.length == other.length) {
               for(int i = 0; i < this.length; ++i) {
                  if (this.bytes[this.offset + i] != other.bytes[other.offset + i]) {
                     return false;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}
