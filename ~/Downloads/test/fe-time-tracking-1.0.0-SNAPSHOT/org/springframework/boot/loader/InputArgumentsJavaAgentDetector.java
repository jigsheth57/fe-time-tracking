package org.springframework.boot.loader;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InputArgumentsJavaAgentDetector implements JavaAgentDetector {
   private static final String JAVA_AGENT_PREFIX = "-javaagent:";
   private final Set javaAgentJars;

   public InputArgumentsJavaAgentDetector() {
      this(getInputArguments());
   }

   InputArgumentsJavaAgentDetector(List inputArguments) {
      this.javaAgentJars = this.getJavaAgentJars(inputArguments);
   }

   private static List getInputArguments() {
      try {
         return (List)AccessController.doPrivileged(new PrivilegedAction() {
            public List run() {
               return ManagementFactory.getRuntimeMXBean().getInputArguments();
            }
         });
      } catch (Exception var1) {
         return Collections.emptyList();
      }
   }

   private Set getJavaAgentJars(List inputArguments) {
      Set javaAgentJars = new HashSet();
      Iterator i$ = inputArguments.iterator();

      while(i$.hasNext()) {
         String argument = (String)i$.next();
         String path = this.getJavaAgentJarPath(argument);
         if (path != null) {
            try {
               javaAgentJars.add((new File(path)).getCanonicalFile().toURI().toURL());
            } catch (IOException var7) {
               throw new IllegalStateException("Failed to determine canonical path of Java agent at path '" + path + "'");
            }
         }
      }

      return javaAgentJars;
   }

   private String getJavaAgentJarPath(String arg) {
      if (arg.startsWith("-javaagent:")) {
         String path = arg.substring("-javaagent:".length());
         int equalsIndex = path.indexOf(61);
         if (equalsIndex > -1) {
            path = path.substring(0, equalsIndex);
         }

         return path;
      } else {
         return null;
      }
   }

   public boolean isJavaAgentJar(URL url) {
      return this.javaAgentJars.contains(url);
   }
}
