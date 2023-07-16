package software.bernie.shadowed.fasterxml.jackson.databind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedClass;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedField;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeBindings;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public abstract class BeanDescription {
   protected final JavaType _type;

   protected BeanDescription(JavaType type) {
      this._type = type;
   }

   public JavaType getType() {
      return this._type;
   }

   public Class<?> getBeanClass() {
      return this._type.getRawClass();
   }

   public boolean isNonStaticInnerClass() {
      return this.getClassInfo().isNonStaticInnerClass();
   }

   public abstract AnnotatedClass getClassInfo();

   public abstract ObjectIdInfo getObjectIdInfo();

   public abstract boolean hasKnownClassAnnotations();

   /** @deprecated */
   @Deprecated
   public abstract TypeBindings bindingsForBeanType();

   /** @deprecated */
   @Deprecated
   public abstract JavaType resolveType(Type var1);

   public abstract Annotations getClassAnnotations();

   public abstract List<BeanPropertyDefinition> findProperties();

   public abstract Set<String> getIgnoredPropertyNames();

   public abstract List<BeanPropertyDefinition> findBackReferences();

   /** @deprecated */
   @Deprecated
   public abstract Map<String, AnnotatedMember> findBackReferenceProperties();

   public abstract List<AnnotatedConstructor> getConstructors();

   public abstract List<AnnotatedMethod> getFactoryMethods();

   public abstract AnnotatedConstructor findDefaultConstructor();

   public abstract Constructor<?> findSingleArgConstructor(Class<?>... var1);

   public abstract Method findFactoryMethod(Class<?>... var1);

   public abstract AnnotatedMember findJsonValueAccessor();

   public abstract AnnotatedMember findAnyGetter();

   public abstract AnnotatedMember findAnySetterAccessor();

   public abstract AnnotatedMethod findMethod(String var1, Class<?>[] var2);

   /** @deprecated */
   @Deprecated
   public abstract AnnotatedMethod findJsonValueMethod();

   /** @deprecated */
   @Deprecated
   public AnnotatedMethod findAnySetter() {
      AnnotatedMember m = this.findAnySetterAccessor();
      return m instanceof AnnotatedMethod ? (AnnotatedMethod)m : null;
   }

   /** @deprecated */
   @Deprecated
   public AnnotatedMember findAnySetterField() {
      AnnotatedMember m = this.findAnySetterAccessor();
      return m instanceof AnnotatedField ? m : null;
   }

   public abstract JsonInclude.Value findPropertyInclusion(JsonInclude.Value var1);

   public abstract JsonFormat.Value findExpectedFormat(JsonFormat.Value var1);

   public abstract Converter<Object, Object> findSerializationConverter();

   public abstract Converter<Object, Object> findDeserializationConverter();

   public String findClassDescription() {
      return null;
   }

   public abstract Map<Object, AnnotatedMember> findInjectables();

   public abstract Class<?> findPOJOBuilder();

   public abstract JsonPOJOBuilder.Value findPOJOBuilderConfig();

   public abstract Object instantiateBean(boolean var1);

   public abstract Class<?>[] findDefaultViews();
}
