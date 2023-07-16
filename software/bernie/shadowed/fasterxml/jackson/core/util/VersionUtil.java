package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;

public class VersionUtil {
   private static final Pattern V_SEP = Pattern.compile("[-_./;:]");

   protected VersionUtil() {
   }

   /** @deprecated */
   @Deprecated
   public Version version() {
      return Version.unknownVersion();
   }

   public static Version versionFor(Class<?> cls) {
      Version version = packageVersionFor(cls);
      return version == null ? Version.unknownVersion() : version;
   }

   public static Version packageVersionFor(Class<?> cls) {
      Version v = null;

      try {
         String versionInfoClassName = cls.getPackage().getName() + ".PackageVersion";
         Class vClass = Class.forName(versionInfoClassName, true, cls.getClassLoader());

         try {
            v = ((Versioned)vClass.newInstance()).version();
         } catch (Exception var5) {
            throw new IllegalArgumentException("Failed to get Versioned out of " + vClass);
         }
      } catch (Exception var6) {
      }

      return v == null ? Version.unknownVersion() : v;
   }

   /** @deprecated */
   @Deprecated
   public static Version mavenVersionFor(ClassLoader cl, String groupId, String artifactId) {
      InputStream pomProperties = cl.getResourceAsStream("META-INF/maven/" + groupId.replaceAll("\\.", "/") + "/" + artifactId + "/pom.properties");
      if (pomProperties != null) {
         Version var8;
         try {
            Properties props = new Properties();
            props.load(pomProperties);
            String versionStr = props.getProperty("version");
            String pomPropertiesArtifactId = props.getProperty("artifactId");
            String pomPropertiesGroupId = props.getProperty("groupId");
            var8 = parseVersion(versionStr, pomPropertiesGroupId, pomPropertiesArtifactId);
         } catch (IOException var12) {
            return Version.unknownVersion();
         } finally {
            _close(pomProperties);
         }

         return var8;
      } else {
         return Version.unknownVersion();
      }
   }

   public static Version parseVersion(String s, String groupId, String artifactId) {
      if (s != null && (s = s.trim()).length() > 0) {
         String[] parts = V_SEP.split(s);
         return new Version(parseVersionPart(parts[0]), parts.length > 1 ? parseVersionPart(parts[1]) : 0, parts.length > 2 ? parseVersionPart(parts[2]) : 0, parts.length > 3 ? parts[3] : null, groupId, artifactId);
      } else {
         return Version.unknownVersion();
      }
   }

   protected static int parseVersionPart(String s) {
      int number = 0;
      int i = 0;

      for(int len = s.length(); i < len; ++i) {
         char c = s.charAt(i);
         if (c > '9' || c < '0') {
            break;
         }

         number = number * 10 + (c - 48);
      }

      return number;
   }

   private static final void _close(Closeable c) {
      try {
         c.close();
      } catch (IOException var2) {
      }

   }

   public static final void throwInternal() {
      throw new RuntimeException("Internal error: this code path should never get executed");
   }
}
