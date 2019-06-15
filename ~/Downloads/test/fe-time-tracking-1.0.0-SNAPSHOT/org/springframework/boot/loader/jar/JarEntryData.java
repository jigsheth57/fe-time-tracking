package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.util.AsciiBytes;

public final class JarEntryData {
   private static final long LOCAL_FILE_HEADER_SIZE = 30L;
   private static final AsciiBytes SLASH = new AsciiBytes("/");
   private final JarFile source;
   private final byte[] header;
   private AsciiBytes name;
   private final byte[] extra;
   private final AsciiBytes comment;
   private final long localHeaderOffset;
   private RandomAccessData data;
   private SoftReference entry;
   JarFile nestedJar;

   public JarEntryData(JarFile source, byte[] header, InputStream inputStream) throws IOException {
      this.source = source;
      this.header = header;
      long nameLength = Bytes.littleEndianValue(header, 28, 2);
      long extraLength = Bytes.littleEndianValue(header, 30, 2);
      long commentLength = Bytes.littleEndianValue(header, 32, 2);
      this.name = new AsciiBytes(Bytes.get(inputStream, nameLength));
      this.extra = Bytes.get(inputStream, extraLength);
      this.comment = new AsciiBytes(Bytes.get(inputStream, commentLength));
      this.localHeaderOffset = Bytes.littleEndianValue(header, 42, 4);
   }

   private JarEntryData(JarEntryData master, JarFile source, AsciiBytes name) {
      this.header = master.header;
      this.extra = master.extra;
      this.comment = master.comment;
      this.localHeaderOffset = master.localHeaderOffset;
      this.source = source;
      this.name = name;
   }

   void setName(AsciiBytes name) {
      this.name = name;
   }

   JarFile getSource() {
      return this.source;
   }

   InputStream getInputStream() throws IOException {
      InputStream inputStream = this.getData().getInputStream(RandomAccessData.ResourceAccess.PER_READ);
      if (this.getMethod() == 8) {
         inputStream = new ZipInflaterInputStream((InputStream)inputStream, this.getSize());
      }

      return (InputStream)inputStream;
   }

   public RandomAccessData getData() throws IOException {
      if (this.data == null) {
         byte[] localHeader = Bytes.get(this.source.getData().getSubsection(this.localHeaderOffset, 30L));
         long nameLength = Bytes.littleEndianValue(localHeader, 26, 2);
         long extraLength = Bytes.littleEndianValue(localHeader, 28, 2);
         this.data = this.source.getData().getSubsection(this.localHeaderOffset + 30L + nameLength + extraLength, (long)this.getCompressedSize());
      }

      return this.data;
   }

   JarEntry asJarEntry() {
      JarEntry entry = this.entry == null ? null : (JarEntry)this.entry.get();
      if (entry == null) {
         entry = new JarEntry(this);
         entry.setCompressedSize((long)this.getCompressedSize());
         entry.setMethod(this.getMethod());
         entry.setCrc(this.getCrc());
         entry.setSize((long)this.getSize());
         entry.setExtra(this.getExtra());
         entry.setComment(this.getComment().toString());
         entry.setSize((long)this.getSize());
         entry.setTime(this.getTime());
         this.entry = new SoftReference(entry);
      }

      return entry;
   }

   public AsciiBytes getName() {
      return this.name;
   }

   public boolean isDirectory() {
      return this.name.endsWith(SLASH);
   }

   public int getMethod() {
      return (int)Bytes.littleEndianValue(this.header, 10, 2);
   }

   public long getTime() {
      long date = Bytes.littleEndianValue(this.header, 14, 2);
      long time = Bytes.littleEndianValue(this.header, 12, 2);
      return this.decodeMsDosFormatDateTime(date, time).getTimeInMillis();
   }

   private Calendar decodeMsDosFormatDateTime(long date, long time) {
      int year = (int)(date >> 9 & 127L) + 1980;
      int month = (int)(date >> 5 & 15L) - 1;
      int day = (int)(date & 31L);
      int hours = (int)(time >> 11 & 31L);
      int minutes = (int)(time >> 5 & 63L);
      int seconds = (int)(time << 1 & 62L);
      return new GregorianCalendar(year, month, day, hours, minutes, seconds);
   }

   public long getCrc() {
      return Bytes.littleEndianValue(this.header, 16, 4);
   }

   public int getCompressedSize() {
      return (int)Bytes.littleEndianValue(this.header, 20, 4);
   }

   public int getSize() {
      return (int)Bytes.littleEndianValue(this.header, 24, 4);
   }

   public byte[] getExtra() {
      return this.extra;
   }

   public AsciiBytes getComment() {
      return this.comment;
   }

   JarEntryData createFilteredCopy(JarFile jarFile, AsciiBytes name) {
      return new JarEntryData(this, jarFile, name);
   }

   static JarEntryData fromInputStream(JarFile source, InputStream inputStream) throws IOException {
      byte[] header = new byte[46];
      return !Bytes.fill(inputStream, header) ? null : new JarEntryData(source, header, inputStream);
   }
}
