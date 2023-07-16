package software.bernie.shadowed.fasterxml.jackson.core;

public interface FormatFeature {
   boolean enabledByDefault();

   int getMask();

   boolean enabledIn(int var1);
}
