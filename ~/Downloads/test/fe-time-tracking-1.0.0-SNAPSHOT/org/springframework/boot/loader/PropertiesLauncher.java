package org.springframework.boot.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.FilteredArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.util.AsciiBytes;
import org.springframework.boot.loader.util.SystemPropertyUtils;

public class PropertiesLauncher extends Launcher {
   private final Logger logger = Logger.getLogger(Launcher.class.getName());
   public static final String MAIN = "loader.main";
   public static final String PATH = "loader.path";
   public static final String HOME = "loader.home";
   public static final String ARGS = "loader.args";
   public static final String CONFIG_NAME = "loader.config.name";
   public static final String CONFIG_LOCATION = "loader.config.location";
   public static final String SET_SYSTEM_PROPERTIES = "loader.system";
   private static final List DEFAULT_PATHS = Arrays.asList();
   private static final Pattern WORD_SEPARATOR = Pattern.compile("\\W+");
   private static final URL[] EMPTY_URLS = new URL[0];
   private final File home;
   private List paths;
   private final Properties properties;
   private Archive parent;

   public PropertiesLauncher() {
      this.paths = new ArrayList(DEFAULT_PATHS);
      this.properties = new Properties();
      if (!this.isDebug()) {
         this.logger.setLevel(Level.SEVERE);
      }

      try {
         this.home = this.getHomeDirectory();
         this.initializeProperties(this.home);
         this.initializePaths();
         this.parent = this.createArchive();
      } catch (Exception var2) {
         throw new IllegalStateException(var2);
      }
   }

   private boolean isDebug() {
      String debug = System.getProperty("debug");
      if (debug != null && !"false".equals(debug)) {
         return true;
      } else {
         debug = System.getProperty("DEBUG");
         if (debug != null && !"false".equals(debug)) {
            return true;
         } else {
            debug = System.getenv("DEBUG");
            return debug != null && !"false".equals(debug);
         }
      }
   }

   protected File getHomeDirectory() {
      return new File(SystemPropertyUtils.resolvePlaceholders(System.getProperty("loader.home", "${user.dir}")));
   }

   private void initializeProperties(File home) throws Exception, IOException {
      String config = "classpath:" + SystemPropertyUtils.resolvePlaceholders(SystemPropertyUtils.getProperty("loader.config.name", "application")) + ".properties";
      config = SystemPropertyUtils.resolvePlaceholders(SystemPropertyUtils.getProperty("loader.config.location", config));
      InputStream resource = this.getResource(config);
      if (resource != null) {
         this.logger.info("Found: " + config);

         try {
            this.properties.load(resource);
         } finally {
            resource.close();
         }

         Iterator i$ = Collections.list(this.properties.propertyNames()).iterator();

         Object key;
         String value;
         while(i$.hasNext()) {
            key = i$.next();
            value = this.properties.getProperty((String)key);
            String value = SystemPropertyUtils.resolvePlaceholders(this.properties, value);
            if (value != null) {
               this.properties.put(key, value);
            }
         }

         if (SystemPropertyUtils.resolvePlaceholders("${loader.system:false}").equals("true")) {
            this.logger.info("Adding resolved properties to System properties");
            i$ = Collections.list(this.properties.propertyNames()).iterator();

            while(i$.hasNext()) {
               key = i$.next();
               value = this.properties.getProperty((String)key);
               System.setProperty((String)key, value);
            }
         }
      } else {
         this.logger.info("Not found: " + config);
      }

   }

   private InputStream getResource(String config) throws Exception {
      if (config.startsWith("classpath:")) {
         return this.getClasspathResource(config.substring("classpath:".length()));
      } else {
         config = this.stripFileUrlPrefix(config);
         return this.isUrl(config) ? this.getURLResource(config) : this.getFileResource(config);
      }
   }

   private String stripFileUrlPrefix(String config) {
      if (config.startsWith("file:")) {
         config = config.substring("file:".length());
         if (config.startsWith("//")) {
            config = config.substring(2);
         }
      }

      return config;
   }

   private boolean isUrl(String config) {
      return config.contains("://");
   }

   private InputStream getClasspathResource(String config) {
      while(config.startsWith("/")) {
         config = config.substring(1);
      }

      config = "/" + config;
      this.logger.fine("Trying classpath: " + config);
      return this.getClass().getResourceAsStream(config);
   }

   private InputStream getFileResource(String config) throws Exception {
      File file = new File(config);
      this.logger.fine("Trying file: " + config);
      return file.canRead() ? new FileInputStream(file) : null;
   }

   private InputStream getURLResource(String config) throws Exception {
      URL url = new URL(config);
      if (this.exists(url)) {
         URLConnection con = url.openConnection();

         try {
            return con.getInputStream();
         } catch (IOException var5) {
            if (con instanceof HttpURLConnection) {
               ((HttpURLConnection)con).disconnect();
            }

            throw var5;
         }
      } else {
         return null;
      }
   }

   private boolean exists(URL url) throws IOException {
      URLConnection connection = url.openConnection();

      try {
         connection.setUseCaches(connection.getClass().getSimpleName().startsWith("JNLP"));
         if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            httpConnection.setRequestMethod("HEAD");
            int responseCode = httpConnection.getResponseCode();
            boolean var5;
            if (responseCode == 200) {
               var5 = true;
               return var5;
            }

            if (responseCode == 404) {
               var5 = false;
               return var5;
            }
         }

         boolean var9 = connection.getContentLength() >= 0;
         return var9;
      } finally {
         if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection)connection).disconnect();
         }

      }
   }

   private void initializePaths() throws IOException {
      String path = SystemPropertyUtils.getProperty("loader.path");
      if (path == null) {
         path = this.properties.getProperty("loader.path");
      }

      if (path != null) {
         this.paths = this.parsePathsProperty(SystemPropertyUtils.resolvePlaceholders(path));
      }

      this.logger.info("Nested archive paths: " + this.paths);
   }

   private List parsePathsProperty(String commaSeparatedPaths) {
      List paths = new ArrayList();
      String[] arr$ = commaSeparatedPaths.split(",");
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String path = arr$[i$];
         path = this.cleanupPath(path);
         if (!path.equals("")) {
            paths.add(path);
         }
      }

      if (paths.isEmpty()) {
         paths.add("lib");
      }

      return paths;
   }

   protected String[] getArgs(String... args) throws Exception {
      String loaderArgs = this.getProperty("loader.args");
      if (loaderArgs != null) {
         String[] defaultArgs = loaderArgs.split("\\s+");
         String[] additionalArgs = args;
         args = new String[defaultArgs.length + args.length];
         System.arraycopy(defaultArgs, 0, args, 0, defaultArgs.length);
         System.arraycopy(additionalArgs, 0, args, defaultArgs.length, additionalArgs.length);
      }

      return args;
   }

   protected String getMainClass() throws Exception {
      String mainClass = this.getProperty("loader.main", "Start-Class");
      if (mainClass == null) {
         throw new IllegalStateException("No 'loader.main' or 'Start-Class' specified");
      } else {
         return mainClass;
      }
   }

   protected ClassLoader createClassLoader(List archives) throws Exception {
      ClassLoader loader = super.createClassLoader(archives);
      String customLoaderClassName = this.getProperty("loader.classLoader");
      if (customLoaderClassName != null) {
         loader = this.wrapWithCustomClassLoader(loader, customLoaderClassName);
         this.logger.info("Using custom class loader: " + customLoaderClassName);
      }

      return loader;
   }

   private ClassLoader wrapWithCustomClassLoader(ClassLoader parent, String loaderClassName) throws Exception {
      Class loaderClass = Class.forName(loaderClassName, true, parent);

      try {
         return (ClassLoader)loaderClass.getConstructor(ClassLoader.class).newInstance(parent);
      } catch (NoSuchMethodException var6) {
         try {
            return (ClassLoader)loaderClass.getConstructor(URL[].class, ClassLoader.class).newInstance(new URL[0], parent);
         } catch (NoSuchMethodException var5) {
            return (ClassLoader)loaderClass.newInstance();
         }
      }
   }

   private String getProperty(String propertyKey) throws Exception {
      return this.getProperty(propertyKey, (String)null);
   }

   private String getProperty(String propertyKey, String manifestKey) throws Exception {
      if (manifestKey == null) {
         manifestKey = propertyKey.replace(".", "-");
         manifestKey = toCamelCase(manifestKey);
      }

      String property = SystemPropertyUtils.getProperty(propertyKey);
      String value;
      if (property != null) {
         value = SystemPropertyUtils.resolvePlaceholders(property);
         this.logger.fine("Property '" + propertyKey + "' from environment: " + value);
         return value;
      } else if (this.properties.containsKey(propertyKey)) {
         value = SystemPropertyUtils.resolvePlaceholders(this.properties.getProperty(propertyKey));
         this.logger.fine("Property '" + propertyKey + "' from properties: " + value);
         return value;
      } else {
         Manifest manifest;
         String value;
         try {
            manifest = (new ExplodedArchive(this.home, false)).getManifest();
            if (manifest != null) {
               value = manifest.getMainAttributes().getValue(manifestKey);
               this.logger.fine("Property '" + manifestKey + "' from home directory manifest: " + value);
               return value;
            }
         } catch (IllegalStateException var6) {
            ;
         }

         manifest = this.createArchive().getManifest();
         if (manifest != null) {
            value = manifest.getMainAttributes().getValue(manifestKey);
            if (value != null) {
               this.logger.fine("Property '" + manifestKey + "' from archive manifest: " + value);
               return value;
            }
         }

         return null;
      }
   }

   protected List getClassPathArchives() throws Exception {
      List lib = new ArrayList();
      Iterator i$ = this.paths.iterator();

      while(i$.hasNext()) {
         String path = (String)i$.next();
         Iterator i$ = this.getClassPathArchives(path).iterator();

         while(i$.hasNext()) {
            Archive archive = (Archive)i$.next();
            List nested = new ArrayList(archive.getNestedArchives(new PropertiesLauncher.ArchiveEntryFilter()));
            nested.add(0, archive);
            lib.addAll(nested);
         }
      }

      this.addParentClassLoaderEntries(lib);
      Collections.reverse(lib);
      return lib;
   }

   private List getClassPathArchives(String path) throws Exception {
      String root = this.cleanupPath(this.stripFileUrlPrefix(path));
      List lib = new ArrayList();
      File file = new File(root);
      if (!this.isAbsolutePath(root)) {
         file = new File(this.home, root);
      }

      if (file.isDirectory()) {
         this.logger.info("Adding classpath entries from " + file);
         Archive archive = new ExplodedArchive(file, false);
         lib.add(archive);
      }

      Archive archive = this.getArchive(file);
      if (archive != null) {
         this.logger.info("Adding classpath entries from archive " + archive.getUrl() + root);
         lib.add(archive);
      }

      Archive nested = this.getNestedArchive(root);
      if (nested != null) {
         this.logger.info("Adding classpath entries from nested " + nested.getUrl() + root);
         lib.add(nested);
      }

      return lib;
   }

   private boolean isAbsolutePath(String root) {
      return root.contains(":") || root.startsWith("/");
   }

   private Archive getArchive(File file) throws IOException {
      String name = file.getName().toLowerCase();
      return !name.endsWith(".jar") && !name.endsWith(".zip") ? null : new JarFileArchive(file);
   }

   private Archive getNestedArchive(String root) throws Exception {
      if (!root.startsWith("/") && !this.parent.getUrl().equals(this.home.toURI().toURL())) {
         Archive.EntryFilter filter = new PropertiesLauncher.PrefixMatchingArchiveFilter(root);
         return this.parent.getNestedArchives(filter).isEmpty() ? null : new FilteredArchive(this.parent, filter);
      } else {
         return null;
      }
   }

   private void addParentClassLoaderEntries(List lib) throws IOException, URISyntaxException {
      ClassLoader parentClassLoader = this.getClass().getClassLoader();
      List urls = new ArrayList();
      URL[] arr$ = this.getURLs(parentClassLoader);
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         URL url = arr$[i$];
         if (!url.toString().endsWith(".jar") && !url.toString().endsWith(".zip")) {
            String name;
            if (url.toString().endsWith("/*")) {
               name = url.getFile();
               File dir = new File(name.substring(0, name.length() - 1));
               if (dir.exists()) {
                  urls.add(new ExplodedArchive(new File(name.substring(0, name.length() - 1)), false));
               }
            } else {
               name = URLDecoder.decode(url.getFile(), "UTF-8");
               urls.add(new ExplodedArchive(new File(name)));
            }
         } else {
            urls.add(new JarFileArchive(new File(url.toURI())));
         }
      }

      this.addNestedArchivesFromParent(urls);
      Iterator i$ = urls.iterator();

      while(i$.hasNext()) {
         Archive archive = (Archive)i$.next();
         if (this.findArchive(lib, archive) < 0) {
            lib.add(archive);
         }
      }

   }

   private void addNestedArchivesFromParent(List urls) {
      int index = this.findArchive(urls, this.parent);
      if (index >= 0) {
         try {
            Archive nested = this.getNestedArchive("lib/");
            if (nested != null) {
               List extra = new ArrayList(nested.getNestedArchives(new PropertiesLauncher.ArchiveEntryFilter()));
               urls.addAll(index + 1, extra);
            }
         } catch (Exception var5) {
            ;
         }
      }

   }

   private int findArchive(List urls, Archive archive) {
      if (archive == null) {
         return -1;
      } else {
         int i = 0;

         for(Iterator i$ = urls.iterator(); i$.hasNext(); ++i) {
            Archive url = (Archive)i$.next();
            if (url.toString().equals(archive.toString())) {
               return i;
            }
         }

         return -1;
      }
   }

   private URL[] getURLs(ClassLoader classLoader) {
      return classLoader instanceof URLClassLoader ? ((URLClassLoader)classLoader).getURLs() : EMPTY_URLS;
   }

   private String cleanupPath(String path) {
      path = path.trim();
      if (path.startsWith("./")) {
         path = path.substring(2);
      }

      if (!path.toLowerCase().endsWith(".jar") && !path.toLowerCase().endsWith(".zip")) {
         if (path.endsWith("/*")) {
            path = path.substring(0, path.length() - 1);
         } else if (!path.endsWith("/") && !path.equals(".")) {
            path = path + "/";
         }

         return path;
      } else {
         return path;
      }
   }

   public static void main(String[] args) throws Exception {
      PropertiesLauncher launcher = new PropertiesLauncher();
      args = launcher.getArgs(args);
      launcher.launch(args);
   }

   public static String toCamelCase(CharSequence string) {
      if (string == null) {
         return null;
      } else {
         StringBuilder builder = new StringBuilder();
         Matcher matcher = WORD_SEPARATOR.matcher(string);

         int pos;
         for(pos = 0; matcher.find(); pos = matcher.end()) {
            builder.append(capitalize(string.subSequence(pos, matcher.end()).toString()));
         }

         builder.append(capitalize(string.subSequence(pos, string.length()).toString()));
         return builder.toString();
      }
   }

   private static Object capitalize(String str) {
      StringBuilder sb = new StringBuilder(str.length());
      sb.append(Character.toUpperCase(str.charAt(0)));
      sb.append(str.substring(1));
      return sb.toString();
   }

   private static final class PrefixMatchingArchiveFilter implements Archive.EntryFilter {
      private final AsciiBytes prefix;
      private final PropertiesLauncher.ArchiveEntryFilter filter;

      private PrefixMatchingArchiveFilter(String prefix) {
         this.filter = new PropertiesLauncher.ArchiveEntryFilter();
         this.prefix = new AsciiBytes(prefix);
      }

      public boolean matches(Archive.Entry entry) {
         return entry.getName().startsWith(this.prefix) && this.filter.matches(entry);
      }

      // $FF: synthetic method
      PrefixMatchingArchiveFilter(String x0, Object x1) {
         this(x0);
      }
   }

   private static final class ArchiveEntryFilter implements Archive.EntryFilter {
      private static final AsciiBytes DOT_JAR = new AsciiBytes(".jar");
      private static final AsciiBytes DOT_ZIP = new AsciiBytes(".zip");

      private ArchiveEntryFilter() {
      }

      public boolean matches(Archive.Entry entry) {
         return entry.getName().endsWith(DOT_JAR) || entry.getName().endsWith(DOT_ZIP);
      }

      // $FF: synthetic method
      ArchiveEntryFilter(Object x0) {
         this();
      }
   }
}
