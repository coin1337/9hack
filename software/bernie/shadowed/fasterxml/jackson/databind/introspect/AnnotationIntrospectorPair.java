package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonInject;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonSerialize;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.NamedType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class AnnotationIntrospectorPair extends AnnotationIntrospector implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final AnnotationIntrospector _primary;
   protected final AnnotationIntrospector _secondary;

   public AnnotationIntrospectorPair(AnnotationIntrospector p, AnnotationIntrospector s) {
      this._primary = p;
      this._secondary = s;
   }

   public Version version() {
      return this._primary.version();
   }

   public static AnnotationIntrospector create(AnnotationIntrospector primary, AnnotationIntrospector secondary) {
      if (primary == null) {
         return secondary;
      } else {
         return (AnnotationIntrospector)(secondary == null ? primary : new AnnotationIntrospectorPair(primary, secondary));
      }
   }

   public Collection<AnnotationIntrospector> allIntrospectors() {
      return this.allIntrospectors(new ArrayList());
   }

   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
      this._primary.allIntrospectors(result);
      this._secondary.allIntrospectors(result);
      return result;
   }

   public boolean isAnnotationBundle(Annotation ann) {
      return this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann);
   }

   public PropertyName findRootName(AnnotatedClass ac) {
      PropertyName name1 = this._primary.findRootName(ac);
      if (name1 == null) {
         return this._secondary.findRootName(ac);
      } else if (name1.hasSimpleName()) {
         return name1;
      } else {
         PropertyName name2 = this._secondary.findRootName(ac);
         return name2 == null ? name1 : name2;
      }
   }

   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated a) {
      JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnorals(a);
      JsonIgnoreProperties.Value v1 = this._primary.findPropertyIgnorals(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   public Boolean isIgnorableType(AnnotatedClass ac) {
      Boolean result = this._primary.isIgnorableType(ac);
      if (result == null) {
         result = this._secondary.isIgnorableType(ac);
      }

      return result;
   }

   public Object findFilterId(Annotated ann) {
      Object id = this._primary.findFilterId(ann);
      if (id == null) {
         id = this._secondary.findFilterId(ann);
      }

      return id;
   }

   public Object findNamingStrategy(AnnotatedClass ac) {
      Object str = this._primary.findNamingStrategy(ac);
      if (str == null) {
         str = this._secondary.findNamingStrategy(ac);
      }

      return str;
   }

   public String findClassDescription(AnnotatedClass ac) {
      String str = this._primary.findClassDescription(ac);
      if (str == null || str.isEmpty()) {
         str = this._secondary.findClassDescription(ac);
      }

      return str;
   }

   /** @deprecated */
   @Deprecated
   public String[] findPropertiesToIgnore(Annotated ac) {
      String[] result = this._primary.findPropertiesToIgnore(ac);
      if (result == null) {
         result = this._secondary.findPropertiesToIgnore(ac);
      }

      return result;
   }

   /** @deprecated */
   @Deprecated
   public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
      String[] result = this._primary.findPropertiesToIgnore(ac, forSerialization);
      if (result == null) {
         result = this._secondary.findPropertiesToIgnore(ac, forSerialization);
      }

      return result;
   }

   /** @deprecated */
   @Deprecated
   public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
      Boolean result = this._primary.findIgnoreUnknownProperties(ac);
      if (result == null) {
         result = this._secondary.findIgnoreUnknownProperties(ac);
      }

      return result;
   }

   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      checker = this._secondary.findAutoDetectVisibility(ac, checker);
      return this._primary.findAutoDetectVisibility(ac, checker);
   }

   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
      if (b == null) {
         b = this._secondary.findTypeResolver(config, ac, baseType);
      }

      return b;
   }

   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
      if (b == null) {
         b = this._secondary.findPropertyTypeResolver(config, am, baseType);
      }

      return b;
   }

   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
      if (b == null) {
         b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
      }

      return b;
   }

   public List<NamedType> findSubtypes(Annotated a) {
      List<NamedType> types1 = this._primary.findSubtypes(a);
      List<NamedType> types2 = this._secondary.findSubtypes(a);
      if (types1 != null && !types1.isEmpty()) {
         if (types2 != null && !types2.isEmpty()) {
            ArrayList<NamedType> result = new ArrayList(types1.size() + types2.size());
            result.addAll(types1);
            result.addAll(types2);
            return result;
         } else {
            return types1;
         }
      } else {
         return types2;
      }
   }

   public String findTypeName(AnnotatedClass ac) {
      String name = this._primary.findTypeName(ac);
      if (name == null || name.length() == 0) {
         name = this._secondary.findTypeName(ac);
      }

      return name;
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
      AnnotationIntrospector.ReferenceProperty r = this._primary.findReferenceType(member);
      return r == null ? this._secondary.findReferenceType(member) : r;
   }

   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
      NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
      return r == null ? this._secondary.findUnwrappingNameTransformer(member) : r;
   }

   public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
      JacksonInject.Value r = this._primary.findInjectableValue(m);
      return r == null ? this._secondary.findInjectableValue(m) : r;
   }

   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m);
   }

   public Boolean hasRequiredMarker(AnnotatedMember m) {
      Boolean r = this._primary.hasRequiredMarker(m);
      return r == null ? this._secondary.hasRequiredMarker(m) : r;
   }

   /** @deprecated */
   @Deprecated
   public Object findInjectableValueId(AnnotatedMember m) {
      Object r = this._primary.findInjectableValueId(m);
      return r == null ? this._secondary.findInjectableValueId(m) : r;
   }

   public Object findSerializer(Annotated am) {
      Object r = this._primary.findSerializer(am);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findSerializer(am), JsonSerializer.None.class);
   }

   public Object findKeySerializer(Annotated a) {
      Object r = this._primary.findKeySerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findKeySerializer(a), JsonSerializer.None.class);
   }

   public Object findContentSerializer(Annotated a) {
      Object r = this._primary.findContentSerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findContentSerializer(a), JsonSerializer.None.class);
   }

   public Object findNullSerializer(Annotated a) {
      Object r = this._primary.findNullSerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findNullSerializer(a), JsonSerializer.None.class);
   }

   /** @deprecated */
   @Deprecated
   public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
      defValue = this._secondary.findSerializationInclusion(a, defValue);
      defValue = this._primary.findSerializationInclusion(a, defValue);
      return defValue;
   }

   /** @deprecated */
   @Deprecated
   public JsonInclude.Include findSerializationInclusionForContent(Annotated a, JsonInclude.Include defValue) {
      defValue = this._secondary.findSerializationInclusionForContent(a, defValue);
      defValue = this._primary.findSerializationInclusionForContent(a, defValue);
      return defValue;
   }

   public JsonInclude.Value findPropertyInclusion(Annotated a) {
      JsonInclude.Value v2 = this._secondary.findPropertyInclusion(a);
      JsonInclude.Value v1 = this._primary.findPropertyInclusion(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
      JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
      return r == null ? this._secondary.findSerializationTyping(a) : r;
   }

   public Object findSerializationConverter(Annotated a) {
      Object r = this._primary.findSerializationConverter(a);
      return r == null ? this._secondary.findSerializationConverter(a) : r;
   }

   public Object findSerializationContentConverter(AnnotatedMember a) {
      Object r = this._primary.findSerializationContentConverter(a);
      return r == null ? this._secondary.findSerializationContentConverter(a) : r;
   }

   public Class<?>[] findViews(Annotated a) {
      Class<?>[] result = this._primary.findViews(a);
      if (result == null) {
         result = this._secondary.findViews(a);
      }

      return result;
   }

   public Boolean isTypeId(AnnotatedMember member) {
      Boolean b = this._primary.isTypeId(member);
      return b == null ? this._secondary.isTypeId(member) : b;
   }

   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
      return r == null ? this._secondary.findObjectIdInfo(ann) : r;
   }

   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
      objectIdInfo = this._secondary.findObjectReferenceInfo(ann, objectIdInfo);
      objectIdInfo = this._primary.findObjectReferenceInfo(ann, objectIdInfo);
      return objectIdInfo;
   }

   public JsonFormat.Value findFormat(Annotated ann) {
      JsonFormat.Value v1 = this._primary.findFormat(ann);
      JsonFormat.Value v2 = this._secondary.findFormat(ann);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   public PropertyName findWrapperName(Annotated ann) {
      PropertyName name = this._primary.findWrapperName(ann);
      if (name == null) {
         name = this._secondary.findWrapperName(ann);
      } else if (name == PropertyName.USE_DEFAULT) {
         PropertyName name2 = this._secondary.findWrapperName(ann);
         if (name2 != null) {
            name = name2;
         }
      }

      return name;
   }

   public String findPropertyDefaultValue(Annotated ann) {
      String str = this._primary.findPropertyDefaultValue(ann);
      return str != null && !str.isEmpty() ? str : this._secondary.findPropertyDefaultValue(ann);
   }

   public String findPropertyDescription(Annotated ann) {
      String r = this._primary.findPropertyDescription(ann);
      return r == null ? this._secondary.findPropertyDescription(ann) : r;
   }

   public Integer findPropertyIndex(Annotated ann) {
      Integer r = this._primary.findPropertyIndex(ann);
      return r == null ? this._secondary.findPropertyIndex(ann) : r;
   }

   public String findImplicitPropertyName(AnnotatedMember ann) {
      String r = this._primary.findImplicitPropertyName(ann);
      return r == null ? this._secondary.findImplicitPropertyName(ann) : r;
   }

   public List<PropertyName> findPropertyAliases(Annotated ann) {
      List<PropertyName> r = this._primary.findPropertyAliases(ann);
      return r == null ? this._secondary.findPropertyAliases(ann) : r;
   }

   public JsonProperty.Access findPropertyAccess(Annotated ann) {
      JsonProperty.Access acc = this._primary.findPropertyAccess(ann);
      if (acc != null && acc != JsonProperty.Access.AUTO) {
         return acc;
      } else {
         acc = this._secondary.findPropertyAccess(ann);
         return acc != null ? acc : JsonProperty.Access.AUTO;
      }
   }

   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
      AnnotatedMethod res = this._primary.resolveSetterConflict(config, setter1, setter2);
      if (res == null) {
         res = this._secondary.resolveSetterConflict(config, setter1, setter2);
      }

      return res;
   }

   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      JavaType t = this._secondary.refineSerializationType(config, a, baseType);
      return this._primary.refineSerializationType(config, a, t);
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationType(Annotated a) {
      Class<?> r = this._primary.findSerializationType(a);
      return r == null ? this._secondary.findSerializationType(a) : r;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findSerializationKeyType(am, baseType);
      return r == null ? this._secondary.findSerializationKeyType(am, baseType) : r;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findSerializationContentType(am, baseType);
      return r == null ? this._secondary.findSerializationContentType(am, baseType) : r;
   }

   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      String[] r = this._primary.findSerializationPropertyOrder(ac);
      return r == null ? this._secondary.findSerializationPropertyOrder(ac) : r;
   }

   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      Boolean r = this._primary.findSerializationSortAlphabetically(ann);
      return r == null ? this._secondary.findSerializationSortAlphabetically(ann) : r;
   }

   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
      this._primary.findAndAddVirtualProperties(config, ac, properties);
      this._secondary.findAndAddVirtualProperties(config, ac, properties);
   }

   public PropertyName findNameForSerialization(Annotated a) {
      PropertyName n = this._primary.findNameForSerialization(a);
      if (n == null) {
         n = this._secondary.findNameForSerialization(a);
      } else if (n == PropertyName.USE_DEFAULT) {
         PropertyName n2 = this._secondary.findNameForSerialization(a);
         if (n2 != null) {
            n = n2;
         }
      }

      return n;
   }

   public Boolean hasAsValue(Annotated a) {
      Boolean b = this._primary.hasAsValue(a);
      if (b == null) {
         b = this._secondary.hasAsValue(a);
      }

      return b;
   }

   public Boolean hasAnyGetter(Annotated a) {
      Boolean b = this._primary.hasAnyGetter(a);
      if (b == null) {
         b = this._secondary.hasAnyGetter(a);
      }

      return b;
   }

   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      names = this._secondary.findEnumValues(enumType, enumValues, names);
      names = this._primary.findEnumValues(enumType, enumValues, names);
      return names;
   }

   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
      Enum<?> en = this._primary.findDefaultEnumValue(enumCls);
      return en == null ? this._secondary.findDefaultEnumValue(enumCls) : en;
   }

   /** @deprecated */
   @Deprecated
   public String findEnumValue(Enum<?> value) {
      String r = this._primary.findEnumValue(value);
      return r == null ? this._secondary.findEnumValue(value) : r;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
      return this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am);
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
      return this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am);
   }

   public Object findDeserializer(Annotated a) {
      Object r = this._primary.findDeserializer(a);
      return this._isExplicitClassOrOb(r, JsonDeserializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findDeserializer(a), JsonDeserializer.None.class);
   }

   public Object findKeyDeserializer(Annotated a) {
      Object r = this._primary.findKeyDeserializer(a);
      return this._isExplicitClassOrOb(r, KeyDeserializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findKeyDeserializer(a), KeyDeserializer.None.class);
   }

   public Object findContentDeserializer(Annotated am) {
      Object r = this._primary.findContentDeserializer(am);
      return this._isExplicitClassOrOb(r, JsonDeserializer.None.class) ? r : this._explicitClassOrOb(this._secondary.findContentDeserializer(am), JsonDeserializer.None.class);
   }

   public Object findDeserializationConverter(Annotated a) {
      Object ob = this._primary.findDeserializationConverter(a);
      return ob == null ? this._secondary.findDeserializationConverter(a) : ob;
   }

   public Object findDeserializationContentConverter(AnnotatedMember a) {
      Object ob = this._primary.findDeserializationContentConverter(a);
      return ob == null ? this._secondary.findDeserializationContentConverter(a) : ob;
   }

   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      JavaType t = this._secondary.refineDeserializationType(config, a, baseType);
      return this._primary.refineDeserializationType(config, a, t);
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findDeserializationType(am, baseType);
      return r != null ? r : this._secondary.findDeserializationType(am, baseType);
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
      Class<?> result = this._primary.findDeserializationKeyType(am, baseKeyType);
      return result == null ? this._secondary.findDeserializationKeyType(am, baseKeyType) : result;
   }

   /** @deprecated */
   @Deprecated
   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
      Class<?> result = this._primary.findDeserializationContentType(am, baseContentType);
      return result == null ? this._secondary.findDeserializationContentType(am, baseContentType) : result;
   }

   public Object findValueInstantiator(AnnotatedClass ac) {
      Object result = this._primary.findValueInstantiator(ac);
      return result == null ? this._secondary.findValueInstantiator(ac) : result;
   }

   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      Class<?> result = this._primary.findPOJOBuilder(ac);
      return result == null ? this._secondary.findPOJOBuilder(ac) : result;
   }

   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
      return result == null ? this._secondary.findPOJOBuilderConfig(ac) : result;
   }

   public PropertyName findNameForDeserialization(Annotated a) {
      PropertyName n = this._primary.findNameForDeserialization(a);
      if (n == null) {
         n = this._secondary.findNameForDeserialization(a);
      } else if (n == PropertyName.USE_DEFAULT) {
         PropertyName n2 = this._secondary.findNameForDeserialization(a);
         if (n2 != null) {
            n = n2;
         }
      }

      return n;
   }

   public Boolean hasAnySetter(Annotated a) {
      Boolean b = this._primary.hasAnySetter(a);
      if (b == null) {
         b = this._secondary.hasAnySetter(a);
      }

      return b;
   }

   public JsonSetter.Value findSetterInfo(Annotated a) {
      JsonSetter.Value v2 = this._secondary.findSetterInfo(a);
      JsonSetter.Value v1 = this._primary.findSetterInfo(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   public Boolean findMergeInfo(Annotated a) {
      Boolean b = this._primary.findMergeInfo(a);
      if (b == null) {
         b = this._secondary.findMergeInfo(a);
      }

      return b;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasCreatorAnnotation(Annotated a) {
      return this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a);
   }

   /** @deprecated */
   @Deprecated
   public JsonCreator.Mode findCreatorBinding(Annotated a) {
      JsonCreator.Mode mode = this._primary.findCreatorBinding(a);
      return mode != null ? mode : this._secondary.findCreatorBinding(a);
   }

   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
      JsonCreator.Mode mode = this._primary.findCreatorAnnotation(config, a);
      return mode == null ? this._secondary.findCreatorAnnotation(config, a) : mode;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
      return this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am);
   }

   protected boolean _isExplicitClassOrOb(Object maybeCls, Class<?> implicit) {
      if (maybeCls != null && maybeCls != implicit) {
         if (maybeCls instanceof Class) {
            return !ClassUtil.isBogusClass((Class)maybeCls);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected Object _explicitClassOrOb(Object maybeCls, Class<?> implicit) {
      if (maybeCls != null && maybeCls != implicit) {
         return maybeCls instanceof Class && ClassUtil.isBogusClass((Class)maybeCls) ? null : maybeCls;
      } else {
         return null;
      }
   }
}
