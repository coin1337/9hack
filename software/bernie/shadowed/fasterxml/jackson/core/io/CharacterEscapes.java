package software.bernie.shadowed.fasterxml.jackson.core.io;

import java.io.Serializable;
import java.util.Arrays;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;

public abstract class CharacterEscapes implements Serializable {
   public static final int ESCAPE_NONE = 0;
   public static final int ESCAPE_STANDARD = -1;
   public static final int ESCAPE_CUSTOM = -2;

   public abstract int[] getEscapeCodesForAscii();

   public abstract SerializableString getEscapeSequence(int var1);

   public static int[] standardAsciiEscapesForJSON() {
      int[] esc = CharTypes.get7BitOutputEscapes();
      return Arrays.copyOf(esc, esc.length);
   }
}
