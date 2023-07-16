package software.bernie.shadowed.fasterxml.jackson.core;

import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.core.io.SerializedString;

public class JsonpCharacterEscapes extends CharacterEscapes {
   private static final long serialVersionUID = 1L;
   private static final int[] asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
   private static final SerializedString escapeFor2028 = new SerializedString("\\u2028");
   private static final SerializedString escapeFor2029 = new SerializedString("\\u2029");
   private static final JsonpCharacterEscapes sInstance = new JsonpCharacterEscapes();

   public static JsonpCharacterEscapes instance() {
      return sInstance;
   }

   public SerializableString getEscapeSequence(int ch) {
      switch(ch) {
      case 8232:
         return escapeFor2028;
      case 8233:
         return escapeFor2029;
      default:
         return null;
      }
   }

   public int[] getEscapeCodesForAscii() {
      return asciiEscapes;
   }
}
