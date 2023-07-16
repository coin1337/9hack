package org.apache.commons.lang3.text;

import java.util.Map;

/** @deprecated */
@Deprecated
public abstract class StrLookup<V> {
   private static final StrLookup<String> NONE_LOOKUP = new StrLookup.MapStrLookup((Map)null);
   private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new StrLookup.SystemPropertiesStrLookup();

   public static StrLookup<?> noneLookup() {
      return NONE_LOOKUP;
   }

   public static StrLookup<String> systemPropertiesLookup() {
      return SYSTEM_PROPERTIES_LOOKUP;
   }

   public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
      return new StrLookup.MapStrLookup(map);
   }

   protected StrLookup() {
   }

   public abstract String lookup(String var1);

   private static class SystemPropertiesStrLookup extends StrLookup<String> {
      private SystemPropertiesStrLookup() {
      }

      public String lookup(String key) {
         if (!key.isEmpty()) {
            try {
               return System.getProperty(key);
            } catch (SecurityException var3) {
            }
         }

         return null;
      }

      // $FF: synthetic method
      SystemPropertiesStrLookup(Object x0) {
         this();
      }
   }

   static class MapStrLookup<V> extends StrLookup<V> {
      private final Map<String, V> map;

      MapStrLookup(Map<String, V> map) {
         this.map = map;
      }

      public String lookup(String key) {
         if (this.map == null) {
            return null;
         } else {
            Object obj = this.map.get(key);
            return obj == null ? null : obj.toString();
         }
      }
   }
}
