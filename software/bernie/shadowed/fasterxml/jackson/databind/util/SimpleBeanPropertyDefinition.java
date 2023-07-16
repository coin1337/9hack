package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.util.Collections;
import java.util.Iterator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedField;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;

public class SimpleBeanPropertyDefinition extends BeanPropertyDefinition {
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final AnnotatedMember _member;
   protected final PropertyMetadata _metadata;
   protected final PropertyName _fullName;
   protected final JsonInclude.Value _inclusion;

   protected SimpleBeanPropertyDefinition(AnnotationIntrospector intr, AnnotatedMember member, PropertyName fullName, PropertyMetadata metadata, JsonInclude.Value inclusion) {
      this._annotationIntrospector = intr;
      this._member = member;
      this._fullName = fullName;
      this._metadata = metadata == null ? PropertyMetadata.STD_OPTIONAL : metadata;
      this._inclusion = inclusion;
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member) {
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, PropertyName.construct(member.getName()), (PropertyMetadata)null, EMPTY_INCLUDE);
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name) {
      return construct(config, member, name, (PropertyMetadata)null, (JsonInclude.Value)EMPTY_INCLUDE);
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Include inclusion) {
      JsonInclude.Value inclValue = inclusion != null && inclusion != JsonInclude.Include.USE_DEFAULTS ? JsonInclude.Value.construct(inclusion, (JsonInclude.Include)null) : EMPTY_INCLUDE;
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclValue);
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Value inclusion) {
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclusion);
   }

   public BeanPropertyDefinition withSimpleName(String newName) {
      return this._fullName.hasSimpleName(newName) && !this._fullName.hasNamespace() ? this : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, new PropertyName(newName), this._metadata, this._inclusion);
   }

   public BeanPropertyDefinition withName(PropertyName newName) {
      return this._fullName.equals(newName) ? this : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, newName, this._metadata, this._inclusion);
   }

   public BeanPropertyDefinition withMetadata(PropertyMetadata metadata) {
      return metadata.equals(this._metadata) ? this : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, metadata, this._inclusion);
   }

   public BeanPropertyDefinition withInclusion(JsonInclude.Value inclusion) {
      return this._inclusion == inclusion ? this : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, this._metadata, inclusion);
   }

   public String getName() {
      return this._fullName.getSimpleName();
   }

   public PropertyName getFullName() {
      return this._fullName;
   }

   public boolean hasName(PropertyName name) {
      return this._fullName.equals(name);
   }

   public String getInternalName() {
      return this.getName();
   }

   public PropertyName getWrapperName() {
      return this._annotationIntrospector != null && this._member != null ? this._annotationIntrospector.findWrapperName(this._member) : null;
   }

   public boolean isExplicitlyIncluded() {
      return false;
   }

   public boolean isExplicitlyNamed() {
      return false;
   }

   public PropertyMetadata getMetadata() {
      return this._metadata;
   }

   public JavaType getPrimaryType() {
      return this._member == null ? TypeFactory.unknownType() : this._member.getType();
   }

   public Class<?> getRawPrimaryType() {
      return this._member == null ? Object.class : this._member.getRawType();
   }

   public JsonInclude.Value findInclusion() {
      return this._inclusion;
   }

   public boolean hasGetter() {
      return this.getGetter() != null;
   }

   public boolean hasSetter() {
      return this.getSetter() != null;
   }

   public boolean hasField() {
      return this._member instanceof AnnotatedField;
   }

   public boolean hasConstructorParameter() {
      return this._member instanceof AnnotatedParameter;
   }

   public AnnotatedMethod getGetter() {
      return this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 0 ? (AnnotatedMethod)this._member : null;
   }

   public AnnotatedMethod getSetter() {
      return this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 1 ? (AnnotatedMethod)this._member : null;
   }

   public AnnotatedField getField() {
      return this._member instanceof AnnotatedField ? (AnnotatedField)this._member : null;
   }

   public AnnotatedParameter getConstructorParameter() {
      return this._member instanceof AnnotatedParameter ? (AnnotatedParameter)this._member : null;
   }

   public Iterator<AnnotatedParameter> getConstructorParameters() {
      AnnotatedParameter param = this.getConstructorParameter();
      return param == null ? ClassUtil.emptyIterator() : Collections.singleton(param).iterator();
   }

   public AnnotatedMember getPrimaryMember() {
      return this._member;
   }
}
