package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonAutoDetect;
import software.bernie.shadowed.fasterxml.jackson.annotation.PropertyAccessor;

public interface VisibilityChecker<T extends VisibilityChecker<T>> {
   T with(JsonAutoDetect var1);

   T withOverrides(JsonAutoDetect.Value var1);

   T with(JsonAutoDetect.Visibility var1);

   T withVisibility(PropertyAccessor var1, JsonAutoDetect.Visibility var2);

   T withGetterVisibility(JsonAutoDetect.Visibility var1);

   T withIsGetterVisibility(JsonAutoDetect.Visibility var1);

   T withSetterVisibility(JsonAutoDetect.Visibility var1);

   T withCreatorVisibility(JsonAutoDetect.Visibility var1);

   T withFieldVisibility(JsonAutoDetect.Visibility var1);

   boolean isGetterVisible(Method var1);

   boolean isGetterVisible(AnnotatedMethod var1);

   boolean isIsGetterVisible(Method var1);

   boolean isIsGetterVisible(AnnotatedMethod var1);

   boolean isSetterVisible(Method var1);

   boolean isSetterVisible(AnnotatedMethod var1);

   boolean isCreatorVisible(Member var1);

   boolean isCreatorVisible(AnnotatedMember var1);

   boolean isFieldVisible(Field var1);

   boolean isFieldVisible(AnnotatedField var1);

   public static class Std implements VisibilityChecker<VisibilityChecker.Std>, Serializable {
      private static final long serialVersionUID = 1L;
      protected static final VisibilityChecker.Std DEFAULT;
      protected final JsonAutoDetect.Visibility _getterMinLevel;
      protected final JsonAutoDetect.Visibility _isGetterMinLevel;
      protected final JsonAutoDetect.Visibility _setterMinLevel;
      protected final JsonAutoDetect.Visibility _creatorMinLevel;
      protected final JsonAutoDetect.Visibility _fieldMinLevel;

      public static VisibilityChecker.Std defaultInstance() {
         return DEFAULT;
      }

      public Std(JsonAutoDetect ann) {
         this._getterMinLevel = ann.getterVisibility();
         this._isGetterMinLevel = ann.isGetterVisibility();
         this._setterMinLevel = ann.setterVisibility();
         this._creatorMinLevel = ann.creatorVisibility();
         this._fieldMinLevel = ann.fieldVisibility();
      }

      public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field) {
         this._getterMinLevel = getter;
         this._isGetterMinLevel = isGetter;
         this._setterMinLevel = setter;
         this._creatorMinLevel = creator;
         this._fieldMinLevel = field;
      }

      public Std(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            this._getterMinLevel = DEFAULT._getterMinLevel;
            this._isGetterMinLevel = DEFAULT._isGetterMinLevel;
            this._setterMinLevel = DEFAULT._setterMinLevel;
            this._creatorMinLevel = DEFAULT._creatorMinLevel;
            this._fieldMinLevel = DEFAULT._fieldMinLevel;
         } else {
            this._getterMinLevel = v;
            this._isGetterMinLevel = v;
            this._setterMinLevel = v;
            this._creatorMinLevel = v;
            this._fieldMinLevel = v;
         }

      }

      public static VisibilityChecker.Std construct(JsonAutoDetect.Value vis) {
         return DEFAULT.withOverrides(vis);
      }

      protected VisibilityChecker.Std _with(JsonAutoDetect.Visibility g, JsonAutoDetect.Visibility isG, JsonAutoDetect.Visibility s, JsonAutoDetect.Visibility cr, JsonAutoDetect.Visibility f) {
         return g == this._getterMinLevel && isG == this._isGetterMinLevel && s == this._setterMinLevel && cr == this._creatorMinLevel && f == this._fieldMinLevel ? this : new VisibilityChecker.Std(g, isG, s, cr, f);
      }

      public VisibilityChecker.Std with(JsonAutoDetect ann) {
         return ann != null ? this._with(this._defaultOrOverride(this._getterMinLevel, ann.getterVisibility()), this._defaultOrOverride(this._isGetterMinLevel, ann.isGetterVisibility()), this._defaultOrOverride(this._setterMinLevel, ann.setterVisibility()), this._defaultOrOverride(this._creatorMinLevel, ann.creatorVisibility()), this._defaultOrOverride(this._fieldMinLevel, ann.fieldVisibility())) : this;
      }

      public VisibilityChecker.Std withOverrides(JsonAutoDetect.Value vis) {
         return vis != null ? this._with(this._defaultOrOverride(this._getterMinLevel, vis.getGetterVisibility()), this._defaultOrOverride(this._isGetterMinLevel, vis.getIsGetterVisibility()), this._defaultOrOverride(this._setterMinLevel, vis.getSetterVisibility()), this._defaultOrOverride(this._creatorMinLevel, vis.getCreatorVisibility()), this._defaultOrOverride(this._fieldMinLevel, vis.getFieldVisibility())) : this;
      }

      private JsonAutoDetect.Visibility _defaultOrOverride(JsonAutoDetect.Visibility defaults, JsonAutoDetect.Visibility override) {
         return override == JsonAutoDetect.Visibility.DEFAULT ? defaults : override;
      }

      public VisibilityChecker.Std with(JsonAutoDetect.Visibility v) {
         return v == JsonAutoDetect.Visibility.DEFAULT ? DEFAULT : new VisibilityChecker.Std(v);
      }

      public VisibilityChecker.Std withVisibility(PropertyAccessor method, JsonAutoDetect.Visibility v) {
         switch(method) {
         case GETTER:
            return this.withGetterVisibility(v);
         case SETTER:
            return this.withSetterVisibility(v);
         case CREATOR:
            return this.withCreatorVisibility(v);
         case FIELD:
            return this.withFieldVisibility(v);
         case IS_GETTER:
            return this.withIsGetterVisibility(v);
         case ALL:
            return this.with(v);
         default:
            return this;
         }
      }

      public VisibilityChecker.Std withGetterVisibility(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            v = DEFAULT._getterMinLevel;
         }

         return this._getterMinLevel == v ? this : new VisibilityChecker.Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            v = DEFAULT._isGetterMinLevel;
         }

         return this._isGetterMinLevel == v ? this : new VisibilityChecker.Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withSetterVisibility(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            v = DEFAULT._setterMinLevel;
         }

         return this._setterMinLevel == v ? this : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            v = DEFAULT._creatorMinLevel;
         }

         return this._creatorMinLevel == v ? this : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withFieldVisibility(JsonAutoDetect.Visibility v) {
         if (v == JsonAutoDetect.Visibility.DEFAULT) {
            v = DEFAULT._fieldMinLevel;
         }

         return this._fieldMinLevel == v ? this : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
      }

      public boolean isCreatorVisible(Member m) {
         return this._creatorMinLevel.isVisible(m);
      }

      public boolean isCreatorVisible(AnnotatedMember m) {
         return this.isCreatorVisible(m.getMember());
      }

      public boolean isFieldVisible(Field f) {
         return this._fieldMinLevel.isVisible(f);
      }

      public boolean isFieldVisible(AnnotatedField f) {
         return this.isFieldVisible(f.getAnnotated());
      }

      public boolean isGetterVisible(Method m) {
         return this._getterMinLevel.isVisible(m);
      }

      public boolean isGetterVisible(AnnotatedMethod m) {
         return this.isGetterVisible(m.getAnnotated());
      }

      public boolean isIsGetterVisible(Method m) {
         return this._isGetterMinLevel.isVisible(m);
      }

      public boolean isIsGetterVisible(AnnotatedMethod m) {
         return this.isIsGetterVisible(m.getAnnotated());
      }

      public boolean isSetterVisible(Method m) {
         return this._setterMinLevel.isVisible(m);
      }

      public boolean isSetterVisible(AnnotatedMethod m) {
         return this.isSetterVisible(m.getAnnotated());
      }

      public String toString() {
         return String.format("[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]", this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
      }

      static {
         DEFAULT = new VisibilityChecker.Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
      }
   }
}
