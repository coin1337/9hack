package software.bernie.shadowed.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.TimeZone;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat {
   String DEFAULT_LOCALE = "##default";
   String DEFAULT_TIMEZONE = "##default";

   String pattern() default "";

   JsonFormat.Shape shape() default JsonFormat.Shape.ANY;

   String locale() default "##default";

   String timezone() default "##default";

   OptBoolean lenient() default OptBoolean.DEFAULT;

   JsonFormat.Feature[] with() default {};

   JsonFormat.Feature[] without() default {};

   public static class Value implements JacksonAnnotationValue<JsonFormat>, Serializable {
      private static final long serialVersionUID = 1L;
      private static final JsonFormat.Value EMPTY = new JsonFormat.Value();
      private final String _pattern;
      private final JsonFormat.Shape _shape;
      private final Locale _locale;
      private final String _timezoneStr;
      private final Boolean _lenient;
      private final JsonFormat.Features _features;
      private transient TimeZone _timezone;

      public Value() {
         this("", JsonFormat.Shape.ANY, (String)"", (String)"", (JsonFormat.Features)JsonFormat.Features.empty(), (Boolean)null);
      }

      public Value(JsonFormat ann) {
         this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), JsonFormat.Features.construct(ann), ann.lenient().asBoolean());
      }

      public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f, Boolean lenient) {
         this(p, sh, localeStr != null && localeStr.length() != 0 && !"##default".equals(localeStr) ? new Locale(localeStr) : null, tzStr != null && tzStr.length() != 0 && !"##default".equals(tzStr) ? tzStr : null, (TimeZone)null, f, lenient);
      }

      public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f, Boolean lenient) {
         this._pattern = p;
         this._shape = sh == null ? JsonFormat.Shape.ANY : sh;
         this._locale = l;
         this._timezone = tz;
         this._timezoneStr = null;
         this._features = f == null ? JsonFormat.Features.empty() : f;
         this._lenient = lenient;
      }

      public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f, Boolean lenient) {
         this._pattern = p;
         this._shape = sh == null ? JsonFormat.Shape.ANY : sh;
         this._locale = l;
         this._timezone = tz;
         this._timezoneStr = tzStr;
         this._features = f == null ? JsonFormat.Features.empty() : f;
         this._lenient = lenient;
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f) {
         this(p, sh, l, tzStr, tz, f, (Boolean)null);
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f) {
         this(p, sh, (String)localeStr, (String)tzStr, (JsonFormat.Features)f, (Boolean)null);
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f) {
         this(p, sh, (Locale)l, (TimeZone)tz, (JsonFormat.Features)f, (Boolean)null);
      }

      public static final JsonFormat.Value empty() {
         return EMPTY;
      }

      public static JsonFormat.Value merge(JsonFormat.Value base, JsonFormat.Value overrides) {
         return base == null ? overrides : base.withOverrides(overrides);
      }

      public static JsonFormat.Value mergeAll(JsonFormat.Value... values) {
         JsonFormat.Value result = null;
         JsonFormat.Value[] arr$ = values;
         int len$ = values.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            JsonFormat.Value curr = arr$[i$];
            if (curr != null) {
               result = result == null ? curr : result.withOverrides(curr);
            }
         }

         return result;
      }

      public static final JsonFormat.Value from(JsonFormat ann) {
         return ann == null ? EMPTY : new JsonFormat.Value(ann);
      }

      public final JsonFormat.Value withOverrides(JsonFormat.Value overrides) {
         if (overrides != null && overrides != EMPTY && overrides != this) {
            if (this == EMPTY) {
               return overrides;
            } else {
               String p = overrides._pattern;
               if (p == null || p.isEmpty()) {
                  p = this._pattern;
               }

               JsonFormat.Shape sh = overrides._shape;
               if (sh == JsonFormat.Shape.ANY) {
                  sh = this._shape;
               }

               Locale l = overrides._locale;
               if (l == null) {
                  l = this._locale;
               }

               JsonFormat.Features f = this._features;
               if (f == null) {
                  f = overrides._features;
               } else {
                  f = f.withOverrides(overrides._features);
               }

               Boolean lenient = overrides._lenient;
               if (lenient == null) {
                  lenient = this._lenient;
               }

               String tzStr = overrides._timezoneStr;
               TimeZone tz;
               if (tzStr != null && !tzStr.isEmpty()) {
                  tz = overrides._timezone;
               } else {
                  tzStr = this._timezoneStr;
                  tz = this._timezone;
               }

               return new JsonFormat.Value(p, sh, l, tzStr, tz, f, lenient);
            }
         } else {
            return this;
         }
      }

      public static JsonFormat.Value forPattern(String p) {
         return new JsonFormat.Value(p, (JsonFormat.Shape)null, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), (Boolean)null);
      }

      public static JsonFormat.Value forShape(JsonFormat.Shape sh) {
         return new JsonFormat.Value((String)null, sh, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), (Boolean)null);
      }

      public static JsonFormat.Value forLeniency(boolean lenient) {
         return new JsonFormat.Value((String)null, (JsonFormat.Shape)null, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), lenient);
      }

      public JsonFormat.Value withPattern(String p) {
         return new JsonFormat.Value(p, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public JsonFormat.Value withShape(JsonFormat.Shape s) {
         return s == this._shape ? this : new JsonFormat.Value(this._pattern, s, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public JsonFormat.Value withLocale(Locale l) {
         return new JsonFormat.Value(this._pattern, this._shape, l, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public JsonFormat.Value withTimeZone(TimeZone tz) {
         return new JsonFormat.Value(this._pattern, this._shape, this._locale, (String)null, tz, this._features, this._lenient);
      }

      public JsonFormat.Value withLenient(Boolean lenient) {
         return lenient == this._lenient ? this : new JsonFormat.Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, lenient);
      }

      public JsonFormat.Value withFeature(JsonFormat.Feature f) {
         JsonFormat.Features newFeats = this._features.with(f);
         return newFeats == this._features ? this : new JsonFormat.Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
      }

      public JsonFormat.Value withoutFeature(JsonFormat.Feature f) {
         JsonFormat.Features newFeats = this._features.without(f);
         return newFeats == this._features ? this : new JsonFormat.Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
      }

      public Class<JsonFormat> valueFor() {
         return JsonFormat.class;
      }

      public String getPattern() {
         return this._pattern;
      }

      public JsonFormat.Shape getShape() {
         return this._shape;
      }

      public Locale getLocale() {
         return this._locale;
      }

      public Boolean getLenient() {
         return this._lenient;
      }

      public boolean isLenient() {
         return Boolean.TRUE.equals(this._lenient);
      }

      public String timeZoneAsString() {
         return this._timezone != null ? this._timezone.getID() : this._timezoneStr;
      }

      public TimeZone getTimeZone() {
         TimeZone tz = this._timezone;
         if (tz == null) {
            if (this._timezoneStr == null) {
               return null;
            }

            tz = TimeZone.getTimeZone(this._timezoneStr);
            this._timezone = tz;
         }

         return tz;
      }

      public boolean hasShape() {
         return this._shape != JsonFormat.Shape.ANY;
      }

      public boolean hasPattern() {
         return this._pattern != null && this._pattern.length() > 0;
      }

      public boolean hasLocale() {
         return this._locale != null;
      }

      public boolean hasTimeZone() {
         return this._timezone != null || this._timezoneStr != null && !this._timezoneStr.isEmpty();
      }

      public boolean hasLenient() {
         return this._lenient != null;
      }

      public Boolean getFeature(JsonFormat.Feature f) {
         return this._features.get(f);
      }

      public JsonFormat.Features getFeatures() {
         return this._features;
      }

      public String toString() {
         return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s)", this._pattern, this._shape, this._lenient, this._locale, this._timezoneStr);
      }

      public int hashCode() {
         int hash = this._timezoneStr == null ? 1 : this._timezoneStr.hashCode();
         if (this._pattern != null) {
            hash ^= this._pattern.hashCode();
         }

         hash += this._shape.hashCode();
         if (this._lenient != null) {
            hash ^= this._lenient.hashCode();
         }

         if (this._locale != null) {
            hash += this._locale.hashCode();
         }

         hash ^= this._features.hashCode();
         return hash;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            JsonFormat.Value other = (JsonFormat.Value)o;
            if (this._shape == other._shape && this._features.equals(other._features)) {
               return _equal(this._lenient, other._lenient) && _equal(this._timezoneStr, other._timezoneStr) && _equal(this._pattern, other._pattern) && _equal(this._timezone, other._timezone) && _equal(this._locale, other._locale);
            } else {
               return false;
            }
         }
      }

      private static <T> boolean _equal(T value1, T value2) {
         if (value1 == null) {
            return value2 == null;
         } else {
            return value2 == null ? false : value1.equals(value2);
         }
      }
   }

   public static class Features {
      private final int _enabled;
      private final int _disabled;
      private static final JsonFormat.Features EMPTY = new JsonFormat.Features(0, 0);

      private Features(int e, int d) {
         this._enabled = e;
         this._disabled = d;
      }

      public static JsonFormat.Features empty() {
         return EMPTY;
      }

      public static JsonFormat.Features construct(JsonFormat f) {
         return construct(f.with(), f.without());
      }

      public static JsonFormat.Features construct(JsonFormat.Feature[] enabled, JsonFormat.Feature[] disabled) {
         int e = 0;
         JsonFormat.Feature[] arr$ = enabled;
         int len$ = enabled.length;

         int len$;
         for(len$ = 0; len$ < len$; ++len$) {
            JsonFormat.Feature f = arr$[len$];
            e |= 1 << f.ordinal();
         }

         int d = 0;
         JsonFormat.Feature[] arr$ = disabled;
         len$ = disabled.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            JsonFormat.Feature f = arr$[i$];
            d |= 1 << f.ordinal();
         }

         return new JsonFormat.Features(e, d);
      }

      public JsonFormat.Features withOverrides(JsonFormat.Features overrides) {
         if (overrides == null) {
            return this;
         } else {
            int overrideD = overrides._disabled;
            int overrideE = overrides._enabled;
            if (overrideD == 0 && overrideE == 0) {
               return this;
            } else if (this._enabled == 0 && this._disabled == 0) {
               return overrides;
            } else {
               int newE = this._enabled & ~overrideD | overrideE;
               int newD = this._disabled & ~overrideE | overrideD;
               return newE == this._enabled && newD == this._disabled ? this : new JsonFormat.Features(newE, newD);
            }
         }
      }

      public JsonFormat.Features with(JsonFormat.Feature... features) {
         int e = this._enabled;
         JsonFormat.Feature[] arr$ = features;
         int len$ = features.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            JsonFormat.Feature f = arr$[i$];
            e |= 1 << f.ordinal();
         }

         return e == this._enabled ? this : new JsonFormat.Features(e, this._disabled);
      }

      public JsonFormat.Features without(JsonFormat.Feature... features) {
         int d = this._disabled;
         JsonFormat.Feature[] arr$ = features;
         int len$ = features.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            JsonFormat.Feature f = arr$[i$];
            d |= 1 << f.ordinal();
         }

         return d == this._disabled ? this : new JsonFormat.Features(this._enabled, d);
      }

      public Boolean get(JsonFormat.Feature f) {
         int mask = 1 << f.ordinal();
         if ((this._disabled & mask) != 0) {
            return Boolean.FALSE;
         } else {
            return (this._enabled & mask) != 0 ? Boolean.TRUE : null;
         }
      }

      public int hashCode() {
         return this._disabled + this._enabled;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            JsonFormat.Features other = (JsonFormat.Features)o;
            return other._enabled == this._enabled && other._disabled == this._disabled;
         }
      }
   }

   public static enum Feature {
      ACCEPT_SINGLE_VALUE_AS_ARRAY,
      ACCEPT_CASE_INSENSITIVE_PROPERTIES,
      WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
      WRITE_DATES_WITH_ZONE_ID,
      WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
      WRITE_SORTED_MAP_ENTRIES,
      ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
   }

   public static enum Shape {
      ANY,
      NATURAL,
      SCALAR,
      ARRAY,
      OBJECT,
      NUMBER,
      NUMBER_FLOAT,
      NUMBER_INT,
      STRING,
      BOOLEAN;

      public boolean isNumeric() {
         return this == NUMBER || this == NUMBER_INT || this == NUMBER_FLOAT;
      }

      public boolean isStructured() {
         return this == OBJECT || this == ARRAY;
      }
   }
}
