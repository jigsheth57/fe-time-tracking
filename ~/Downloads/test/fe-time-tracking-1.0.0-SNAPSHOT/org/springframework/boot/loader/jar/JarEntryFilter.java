package org.springframework.boot.loader.jar;

import org.springframework.boot.loader.util.AsciiBytes;

public interface JarEntryFilter {
   AsciiBytes apply(AsciiBytes var1, JarEntryData var2);
}
