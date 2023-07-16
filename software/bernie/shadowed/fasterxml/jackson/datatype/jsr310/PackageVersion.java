package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310;

import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;
import software.bernie.shadowed.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion implements Versioned {
   public static final Version VERSION = VersionUtil.parseVersion("2.9.0", "software.bernie.shadowed.fasterxml.jackson.datatype", "jackson-datatype-jsr310");

   public Version version() {
      return VERSION;
   }
}
