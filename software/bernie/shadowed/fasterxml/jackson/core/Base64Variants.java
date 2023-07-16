package software.bernie.shadowed.fasterxml.jackson.core;

public final class Base64Variants {
   static final String STD_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
   public static final Base64Variant MIME = new Base64Variant("MIME", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", true, '=', 76);
   public static final Base64Variant MIME_NO_LINEFEEDS;
   public static final Base64Variant PEM;
   public static final Base64Variant MODIFIED_FOR_URL;

   public static Base64Variant getDefaultVariant() {
      return MIME_NO_LINEFEEDS;
   }

   public static Base64Variant valueOf(String name) throws IllegalArgumentException {
      if (MIME._name.equals(name)) {
         return MIME;
      } else if (MIME_NO_LINEFEEDS._name.equals(name)) {
         return MIME_NO_LINEFEEDS;
      } else if (PEM._name.equals(name)) {
         return PEM;
      } else if (MODIFIED_FOR_URL._name.equals(name)) {
         return MODIFIED_FOR_URL;
      } else {
         if (name == null) {
            name = "<null>";
         } else {
            name = "'" + name + "'";
         }

         throw new IllegalArgumentException("No Base64Variant with name " + name);
      }
   }

   static {
      MIME_NO_LINEFEEDS = new Base64Variant(MIME, "MIME-NO-LINEFEEDS", Integer.MAX_VALUE);
      PEM = new Base64Variant(MIME, "PEM", true, '=', 64);
      StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
      sb.setCharAt(sb.indexOf("+"), '-');
      sb.setCharAt(sb.indexOf("/"), '_');
      MODIFIED_FOR_URL = new Base64Variant("MODIFIED-FOR-URL", sb.toString(), false, '\u0000', Integer.MAX_VALUE);
   }
}
