package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;

public class EnumResolver implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Class<Enum<?>> _enumClass;
   protected final Enum<?>[] _enums;
   protected final HashMap<String, Enum<?>> _enumsById;
   protected final Enum<?> _defaultValue;

   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue) {
      this._enumClass = enumClass;
      this._enums = enums;
      this._enumsById = map;
      this._defaultValue = defaultValue;
   }

   public static EnumResolver constructFor(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
      Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
      if (enumValues == null) {
         throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
      } else {
         String[] names = ai.findEnumValues(enumCls, enumValues, new String[enumValues.length]);
         HashMap<String, Enum<?>> map = new HashMap();
         int i = 0;

         for(int len = enumValues.length; i < len; ++i) {
            String name = names[i];
            if (name == null) {
               name = enumValues[i].name();
            }

            map.put(name, enumValues[i]);
         }

         Enum<?> defaultEnum = ai.findDefaultEnumValue(enumCls);
         return new EnumResolver(enumCls, enumValues, map, defaultEnum);
      }
   }

   /** @deprecated */
   @Deprecated
   public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls) {
      return constructUsingToString(enumCls, (AnnotationIntrospector)null);
   }

   public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
      Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumValues.length;

      while(true) {
         --i;
         if (i < 0) {
            Enum<?> defaultEnum = ai == null ? null : ai.findDefaultEnumValue(enumCls);
            return new EnumResolver(enumCls, enumValues, map, defaultEnum);
         }

         Enum<?> e = enumValues[i];
         map.put(e.toString(), e);
      }
   }

   public static EnumResolver constructUsingMethod(Class<Enum<?>> enumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
      Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumValues.length;

      while(true) {
         --i;
         if (i < 0) {
            Enum<?> defaultEnum = ai != null ? ai.findDefaultEnumValue(enumCls) : null;
            return new EnumResolver(enumCls, enumValues, map, defaultEnum);
         }

         Enum en = enumValues[i];

         try {
            Object o = accessor.getValue(en);
            if (o != null) {
               map.put(o.toString(), en);
            }
         } catch (Exception var8) {
            throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + var8.getMessage());
         }
      }
   }

   public static EnumResolver constructUnsafe(Class<?> rawEnumCls, AnnotationIntrospector ai) {
      return constructFor(rawEnumCls, ai);
   }

   public static EnumResolver constructUnsafeUsingToString(Class<?> rawEnumCls, AnnotationIntrospector ai) {
      return constructUsingToString(rawEnumCls, ai);
   }

   public static EnumResolver constructUnsafeUsingMethod(Class<?> rawEnumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
      return constructUsingMethod(rawEnumCls, accessor, ai);
   }

   public CompactStringObjectMap constructLookup() {
      return CompactStringObjectMap.construct(this._enumsById);
   }

   public Enum<?> findEnum(String key) {
      return (Enum)this._enumsById.get(key);
   }

   public Enum<?> getEnum(int index) {
      return index >= 0 && index < this._enums.length ? this._enums[index] : null;
   }

   public Enum<?> getDefaultValue() {
      return this._defaultValue;
   }

   public Enum<?>[] getRawEnums() {
      return this._enums;
   }

   public List<Enum<?>> getEnums() {
      ArrayList<Enum<?>> enums = new ArrayList(this._enums.length);
      Enum[] arr$ = this._enums;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Enum<?> e = arr$[i$];
         enums.add(e);
      }

      return enums;
   }

   public Collection<String> getEnumIds() {
      return this._enumsById.keySet();
   }

   public Class<Enum<?>> getEnumClass() {
      return this._enumClass;
   }

   public int lastValidIndex() {
      return this._enums.length - 1;
   }
}
