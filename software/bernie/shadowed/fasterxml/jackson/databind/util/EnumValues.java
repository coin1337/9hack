package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;

public final class EnumValues implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Class<Enum<?>> _enumClass;
   private final Enum<?>[] _values;
   private final SerializableString[] _textual;
   private transient EnumMap<?, SerializableString> _asMap;

   private EnumValues(Class<Enum<?>> enumClass, SerializableString[] textual) {
      this._enumClass = enumClass;
      this._values = (Enum[])enumClass.getEnumConstants();
      this._textual = textual;
   }

   public static EnumValues construct(SerializationConfig config, Class<Enum<?>> enumClass) {
      return config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING) ? constructFromToString(config, enumClass) : constructFromName(config, enumClass);
   }

   public static EnumValues constructFromName(MapperConfig<?> config, Class<Enum<?>> enumClass) {
      Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
      Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
      if (enumValues == null) {
         throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
      } else {
         String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, enumValues, new String[enumValues.length]);
         SerializableString[] textual = new SerializableString[enumValues.length];
         int i = 0;

         for(int len = enumValues.length; i < len; ++i) {
            Enum<?> en = enumValues[i];
            String name = names[i];
            if (name == null) {
               name = en.name();
            }

            textual[en.ordinal()] = config.compileString(name);
         }

         return new EnumValues(enumClass, textual);
      }
   }

   public static EnumValues constructFromToString(MapperConfig<?> config, Class<Enum<?>> enumClass) {
      Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
      Enum<?>[] values = (Enum[])cls.getEnumConstants();
      if (values == null) {
         throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
      } else {
         SerializableString[] textual = new SerializableString[values.length];
         Enum[] arr$ = values;
         int len$ = values.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Enum<?> en = arr$[i$];
            textual[en.ordinal()] = config.compileString(en.toString());
         }

         return new EnumValues(enumClass, textual);
      }
   }

   public SerializableString serializedValueFor(Enum<?> key) {
      return this._textual[key.ordinal()];
   }

   public Collection<SerializableString> values() {
      return Arrays.asList(this._textual);
   }

   public List<Enum<?>> enums() {
      return Arrays.asList(this._values);
   }

   public EnumMap<?, SerializableString> internalMap() {
      EnumMap<?, SerializableString> result = this._asMap;
      if (result == null) {
         Map<Enum<?>, SerializableString> map = new LinkedHashMap();
         Enum[] arr$ = this._values;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Enum<?> en = arr$[i$];
            map.put(en, this._textual[en.ordinal()]);
         }

         result = new EnumMap(map);
      }

      return result;
   }

   public Class<Enum<?>> getEnumClass() {
      return this._enumClass;
   }
}
