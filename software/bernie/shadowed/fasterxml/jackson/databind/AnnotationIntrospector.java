package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonInject;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonSerialize;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedClass;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.VisibilityChecker;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.NamedType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public abstract class AnnotationIntrospector implements Versioned, Serializable {
   public static AnnotationIntrospector nopInstance() {
      return NopAnnotationIntrospector.instance;
   }

   public static AnnotationIntrospector pair(AnnotationIntrospector a1, AnnotationIntrospector a2) {
      return new AnnotationIntrospectorPair(a1, a2);
   }

   public Collection<AnnotationIntrospector> allIntrospectors() {
      return Collections.singletonList(this);
   }

   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
      result.add(this);
      return result;
   }

   public abstract Version version();

   public boolean isAnnotationBundle(Annotation ann) {
      return false;
   }

   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      return null;
   }

   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
      return objectIdInfo;
   }

   public PropertyName findRootName(AnnotatedClass ac) {
      return null;
   }

   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
      return JsonIgnoreProperties.Value.empty();
   }

   public Boolean isIgnorableType(AnnotatedClass ac) {
      return null;
   }

   public Object findFilterId(Annotated ann) {
      return null;
   }

   public Object findNamingStrategy(AnnotatedClass ac) {
      return null;
   }

   public String findClassDescription(AnnotatedClass ac) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public String[] findPropertiesToIgnore(Annotated ac) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
      return null;
   }

   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      return checker;
   }

   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
      return null;
   }

   public List<NamedType> findSubtypes(Annotated a) {
      return null;
   }

   public String findTypeName(AnnotatedClass ac) {
      return null;
   }

   public Boolean isTypeId(AnnotatedMember member) {
      return null;
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
      return null;
   }

   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
      return null;
   }

   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return false;
   }

   public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
      Object id = this.findInjectableValueId(m);
      return id != null ? JacksonInject.Value.forId(id) : null;
   }

   public Boolean hasRequiredMarker(AnnotatedMember m) {
      return null;
   }

   public Class<?>[] findViews(Annotated a) {
      return null;
   }

   public JsonFormat.Value findFormat(Annotated memberOrClass) {
      return JsonFormat.Value.empty();
   }

   public PropertyName findWrapperName(Annotated ann) {
      return null;
   }

   public String findPropertyDefaultValue(Annotated ann) {
      return null;
   }

   public String findPropertyDescription(Annotated ann) {
      return null;
   }

   public Integer findPropertyIndex(Annotated ann) {
      return null;
   }

   public String findImplicitPropertyName(AnnotatedMember member) {
      return null;
   }

   public List<PropertyName> findPropertyAliases(Annotated ann) {
      return null;
   }

   public JsonProperty.Access findPropertyAccess(Annotated ann) {
      return null;
   }

   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Object findInjectableValueId(AnnotatedMember m) {
      return null;
   }

   public Object findSerializer(Annotated am) {
      return null;
   }

   public Object findKeySerializer(Annotated am) {
      return null;
   }

   public Object findContentSerializer(Annotated am) {
      return null;
   }

   public Object findNullSerializer(Annotated am) {
      return null;
   }

   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
      return null;
   }

   public Object findSerializationConverter(Annotated a) {
      return null;
   }

   public Object findSerializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public JsonInclude.Value findPropertyInclusion(Annotated a) {
      return JsonInclude.Value.empty();
   }

   /** @deprecated */
   @Deprecated
   public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
      return defValue;
   }

   /** @deprecated */
   @Deprecated
   public JsonInclude.Include findSerializationInclusionForContent(Annotated a, JsonInclude.Include defValue) {
      return defValue;
   }

   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationType(Annotated a) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
      return null;
   }

   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      return null;
   }

   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      return null;
   }

   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
   }

   public PropertyName findNameForSerialization(Annotated a) {
      return null;
   }

   public Boolean hasAsValue(Annotated a) {
      return a instanceof AnnotatedMethod && this.hasAsValueAnnotation((AnnotatedMethod)a) ? true : null;
   }

   public Boolean hasAnyGetter(Annotated a) {
      return a instanceof AnnotatedMethod && this.hasAnyGetterAnnotation((AnnotatedMethod)a) ? true : null;
   }

   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      return names;
   }

   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public String findEnumValue(Enum<?> value) {
      return value.name();
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   public Object findDeserializer(Annotated am) {
      return null;
   }

   public Object findKeyDeserializer(Annotated am) {
      return null;
   }

   public Object findContentDeserializer(Annotated am) {
      return null;
   }

   public Object findDeserializationConverter(Annotated a) {
      return null;
   }

   public Object findDeserializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
      return null;
   }

   public Object findValueInstantiator(AnnotatedClass ac) {
      return null;
   }

   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      return null;
   }

   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      return null;
   }

   public PropertyName findNameForDeserialization(Annotated a) {
      return null;
   }

   public Boolean hasAnySetter(Annotated a) {
      return null;
   }

   public JsonSetter.Value findSetterInfo(Annotated a) {
      return JsonSetter.Value.empty();
   }

   public Boolean findMergeInfo(Annotated a) {
      return null;
   }

   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
      if (this.hasCreatorAnnotation(a)) {
         JsonCreator.Mode mode = this.findCreatorBinding(a);
         if (mode == null) {
            mode = JsonCreator.Mode.DEFAULT;
         }

         return mode;
      } else {
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean hasCreatorAnnotation(Annotated a) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public JsonCreator.Mode findCreatorBinding(Annotated a) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   protected <A extends Annotation> A _findAnnotation(Annotated annotated, Class<A> annoClass) {
      return annotated.getAnnotation(annoClass);
   }

   protected boolean _hasAnnotation(Annotated annotated, Class<? extends Annotation> annoClass) {
      return annotated.hasAnnotation(annoClass);
   }

   protected boolean _hasOneOf(Annotated annotated, Class<? extends Annotation>[] annoClasses) {
      return annotated.hasOneOf(annoClasses);
   }

   public static class ReferenceProperty {
      private final AnnotationIntrospector.ReferenceProperty.Type _type;
      private final String _name;

      public ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type t, String n) {
         this._type = t;
         this._name = n;
      }

      public static AnnotationIntrospector.ReferenceProperty managed(String name) {
         return new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE, name);
      }

      public static AnnotationIntrospector.ReferenceProperty back(String name) {
         return new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, name);
      }

      public AnnotationIntrospector.ReferenceProperty.Type getType() {
         return this._type;
      }

      public String getName() {
         return this._name;
      }

      public boolean isManagedReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE;
      }

      public boolean isBackReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE;
      }

      public static enum Type {
         MANAGED_REFERENCE,
         BACK_REFERENCE;
      }
   }
}
