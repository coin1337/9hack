package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.PackageVersion;

public abstract class NopAnnotationIntrospector extends AnnotationIntrospector implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final NopAnnotationIntrospector instance = new NopAnnotationIntrospector() {
      private static final long serialVersionUID = 1L;

      public Version version() {
         return PackageVersion.VERSION;
      }
   };

   public Version version() {
      return Version.unknownVersion();
   }
}
