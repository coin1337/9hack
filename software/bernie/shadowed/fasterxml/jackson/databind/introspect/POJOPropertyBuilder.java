package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.annotation.Nulls;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ConfigOverride;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class POJOPropertyBuilder extends BeanPropertyDefinition implements Comparable<POJOPropertyBuilder> {
   private static final AnnotationIntrospector.ReferenceProperty NOT_REFEFERENCE_PROP = AnnotationIntrospector.ReferenceProperty.managed("");
   protected final boolean _forSerialization;
   protected final MapperConfig<?> _config;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final PropertyName _name;
   protected final PropertyName _internalName;
   protected POJOPropertyBuilder.Linked<AnnotatedField> _fields;
   protected POJOPropertyBuilder.Linked<AnnotatedParameter> _ctorParameters;
   protected POJOPropertyBuilder.Linked<AnnotatedMethod> _getters;
   protected POJOPropertyBuilder.Linked<AnnotatedMethod> _setters;
   protected transient PropertyMetadata _metadata;
   protected transient AnnotationIntrospector.ReferenceProperty _referenceInfo;

   public POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName) {
      this(config, ai, forSerialization, internalName, internalName);
   }

   protected POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName, PropertyName name) {
      this._config = config;
      this._annotationIntrospector = ai;
      this._internalName = internalName;
      this._name = name;
      this._forSerialization = forSerialization;
   }

   protected POJOPropertyBuilder(POJOPropertyBuilder src, PropertyName newName) {
      this._config = src._config;
      this._annotationIntrospector = src._annotationIntrospector;
      this._internalName = src._internalName;
      this._name = newName;
      this._fields = src._fields;
      this._ctorParameters = src._ctorParameters;
      this._getters = src._getters;
      this._setters = src._setters;
      this._forSerialization = src._forSerialization;
   }

   public POJOPropertyBuilder withName(PropertyName newName) {
      return new POJOPropertyBuilder(this, newName);
   }

   public POJOPropertyBuilder withSimpleName(String newSimpleName) {
      PropertyName newName = this._name.withSimpleName(newSimpleName);
      return newName == this._name ? this : new POJOPropertyBuilder(this, newName);
   }

   public int compareTo(POJOPropertyBuilder other) {
      if (this._ctorParameters != null) {
         if (other._ctorParameters == null) {
            return -1;
         }
      } else if (other._ctorParameters != null) {
         return 1;
      }

      return this.getName().compareTo(other.getName());
   }

   public String getName() {
      return this._name == null ? null : this._name.getSimpleName();
   }

   public PropertyName getFullName() {
      return this._name;
   }

   public boolean hasName(PropertyName name) {
      return this._name.equals(name);
   }

   public String getInternalName() {
      return this._internalName.getSimpleName();
   }

   public PropertyName getWrapperName() {
      AnnotatedMember member = this.getPrimaryMember();
      return member != null && this._annotationIntrospector != null ? this._annotationIntrospector.findWrapperName(member) : null;
   }

   public boolean isExplicitlyIncluded() {
      return this._anyExplicits(this._fields) || this._anyExplicits(this._getters) || this._anyExplicits(this._setters) || this._anyExplicitNames(this._ctorParameters);
   }

   public boolean isExplicitlyNamed() {
      return this._anyExplicitNames(this._fields) || this._anyExplicitNames(this._getters) || this._anyExplicitNames(this._setters) || this._anyExplicitNames(this._ctorParameters);
   }

   public PropertyMetadata getMetadata() {
      if (this._metadata == null) {
         Boolean b = this._findRequired();
         String desc = this._findDescription();
         Integer idx = this._findIndex();
         String def = this._findDefaultValue();
         if (b == null && idx == null && def == null) {
            this._metadata = desc == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc);
         } else {
            this._metadata = PropertyMetadata.construct(b, desc, idx, def);
         }

         if (!this._forSerialization) {
            this._metadata = this._getSetterInfo(this._metadata);
         }
      }

      return this._metadata;
   }

   protected PropertyMetadata _getSetterInfo(PropertyMetadata metadata) {
      boolean needMerge = true;
      Nulls valueNulls = null;
      Nulls contentNulls = null;
      AnnotatedMember prim = this.getPrimaryMember();
      AnnotatedMember acc = this.getAccessor();
      JsonSetter.Value setterInfo;
      if (prim != null) {
         if (this._annotationIntrospector != null) {
            if (acc != null) {
               Boolean b = this._annotationIntrospector.findMergeInfo(prim);
               if (b != null) {
                  needMerge = false;
                  if (b) {
                     metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForPropertyOverride(acc));
                  }
               }
            }

            setterInfo = this._annotationIntrospector.findSetterInfo(prim);
            if (setterInfo != null) {
               valueNulls = setterInfo.nonDefaultValueNulls();
               contentNulls = setterInfo.nonDefaultContentNulls();
            }
         }

         if (needMerge || valueNulls == null || contentNulls == null) {
            Class<?> rawType = this.getRawPrimaryType();
            ConfigOverride co = this._config.getConfigOverride(rawType);
            JsonSetter.Value setterInfo = co.getSetterInfo();
            if (setterInfo != null) {
               if (valueNulls == null) {
                  valueNulls = setterInfo.nonDefaultValueNulls();
               }

               if (contentNulls == null) {
                  contentNulls = setterInfo.nonDefaultContentNulls();
               }
            }

            if (needMerge && acc != null) {
               Boolean b = co.getMergeable();
               if (b != null) {
                  needMerge = false;
                  if (b) {
                     metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForTypeOverride(acc));
                  }
               }
            }
         }
      }

      if (needMerge || valueNulls == null || contentNulls == null) {
         setterInfo = this._config.getDefaultSetterInfo();
         if (valueNulls == null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
         }

         if (contentNulls == null) {
            contentNulls = setterInfo.nonDefaultContentNulls();
         }

         if (needMerge) {
            Boolean b = this._config.getDefaultMergeable();
            if (Boolean.TRUE.equals(b) && acc != null) {
               metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForDefaults(acc));
            }
         }
      }

      if (valueNulls != null || contentNulls != null) {
         metadata = metadata.withNulls(valueNulls, contentNulls);
      }

      return metadata;
   }

   public JavaType getPrimaryType() {
      AnnotatedMethod m;
      if (this._forSerialization) {
         m = this.getGetter();
         if (m == null) {
            AnnotatedMember m = this.getField();
            return m == null ? TypeFactory.unknownType() : m.getType();
         } else {
            return m.getType();
         }
      } else {
         AnnotatedMember m = this.getConstructorParameter();
         if (m == null) {
            m = this.getSetter();
            if (m != null) {
               return ((AnnotatedMethod)m).getParameterType(0);
            }

            m = this.getField();
         }

         if (m == null) {
            m = this.getGetter();
            if (m == null) {
               return TypeFactory.unknownType();
            }
         }

         return ((AnnotatedMember)m).getType();
      }
   }

   public Class<?> getRawPrimaryType() {
      return this.getPrimaryType().getRawClass();
   }

   public boolean hasGetter() {
      return this._getters != null;
   }

   public boolean hasSetter() {
      return this._setters != null;
   }

   public boolean hasField() {
      return this._fields != null;
   }

   public boolean hasConstructorParameter() {
      return this._ctorParameters != null;
   }

   public boolean couldDeserialize() {
      return this._ctorParameters != null || this._setters != null || this._fields != null;
   }

   public boolean couldSerialize() {
      return this._getters != null || this._fields != null;
   }

   public AnnotatedMethod getGetter() {
      POJOPropertyBuilder.Linked<AnnotatedMethod> curr = this._getters;
      if (curr == null) {
         return null;
      } else {
         POJOPropertyBuilder.Linked<AnnotatedMethod> next = curr.next;
         if (next == null) {
            return (AnnotatedMethod)curr.value;
         } else {
            for(; next != null; next = next.next) {
               Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
               Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
               if (currClass != nextClass) {
                  if (currClass.isAssignableFrom(nextClass)) {
                     curr = next;
                     continue;
                  }

                  if (nextClass.isAssignableFrom(currClass)) {
                     continue;
                  }
               }

               int priNext = this._getterPriority((AnnotatedMethod)next.value);
               int priCurr = this._getterPriority((AnnotatedMethod)curr.value);
               if (priNext == priCurr) {
                  throw new IllegalArgumentException("Conflicting getter definitions for property \"" + this.getName() + "\": " + ((AnnotatedMethod)curr.value).getFullName() + " vs " + ((AnnotatedMethod)next.value).getFullName());
               }

               if (priNext < priCurr) {
                  curr = next;
               }
            }

            this._getters = curr.withoutNext();
            return (AnnotatedMethod)curr.value;
         }
      }
   }

   public AnnotatedMethod getSetter() {
      POJOPropertyBuilder.Linked<AnnotatedMethod> curr = this._setters;
      if (curr == null) {
         return null;
      } else {
         POJOPropertyBuilder.Linked<AnnotatedMethod> next = curr.next;
         if (next == null) {
            return (AnnotatedMethod)curr.value;
         } else {
            while(true) {
               if (next == null) {
                  this._setters = curr.withoutNext();
                  return (AnnotatedMethod)curr.value;
               }

               label42: {
                  Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
                  Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
                  if (currClass != nextClass) {
                     if (currClass.isAssignableFrom(nextClass)) {
                        curr = next;
                        break label42;
                     }

                     if (nextClass.isAssignableFrom(currClass)) {
                        break label42;
                     }
                  }

                  AnnotatedMethod nextM = (AnnotatedMethod)next.value;
                  AnnotatedMethod currM = (AnnotatedMethod)curr.value;
                  int priNext = this._setterPriority(nextM);
                  int priCurr = this._setterPriority(currM);
                  if (priNext != priCurr) {
                     if (priNext < priCurr) {
                        curr = next;
                     }
                  } else {
                     if (this._annotationIntrospector == null) {
                        break;
                     }

                     AnnotatedMethod pref = this._annotationIntrospector.resolveSetterConflict(this._config, currM, nextM);
                     if (pref != currM) {
                        if (pref != nextM) {
                           break;
                        }

                        curr = next;
                     }
                  }
               }

               next = next.next;
            }

            throw new IllegalArgumentException(String.format("Conflicting setter definitions for property \"%s\": %s vs %s", this.getName(), ((AnnotatedMethod)curr.value).getFullName(), ((AnnotatedMethod)next.value).getFullName()));
         }
      }
   }

   public AnnotatedField getField() {
      if (this._fields == null) {
         return null;
      } else {
         AnnotatedField field = (AnnotatedField)this._fields.value;
         POJOPropertyBuilder.Linked next = this._fields.next;

         AnnotatedField nextField;
         while(true) {
            if (next == null) {
               return field;
            }

            nextField = (AnnotatedField)next.value;
            Class<?> fieldClass = field.getDeclaringClass();
            Class<?> nextClass = nextField.getDeclaringClass();
            if (fieldClass == nextClass) {
               break;
            }

            if (fieldClass.isAssignableFrom(nextClass)) {
               field = nextField;
            } else if (!nextClass.isAssignableFrom(fieldClass)) {
               break;
            }

            next = next.next;
         }

         throw new IllegalArgumentException("Multiple fields representing property \"" + this.getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
      }
   }

   public AnnotatedParameter getConstructorParameter() {
      if (this._ctorParameters == null) {
         return null;
      } else {
         POJOPropertyBuilder.Linked curr = this._ctorParameters;

         while(!(((AnnotatedParameter)curr.value).getOwner() instanceof AnnotatedConstructor)) {
            curr = curr.next;
            if (curr == null) {
               return (AnnotatedParameter)this._ctorParameters.value;
            }
         }

         return (AnnotatedParameter)curr.value;
      }
   }

   public Iterator<AnnotatedParameter> getConstructorParameters() {
      return (Iterator)(this._ctorParameters == null ? ClassUtil.emptyIterator() : new POJOPropertyBuilder.MemberIterator(this._ctorParameters));
   }

   public AnnotatedMember getPrimaryMember() {
      if (this._forSerialization) {
         return this.getAccessor();
      } else {
         AnnotatedMember m = this.getMutator();
         if (m == null) {
            m = this.getAccessor();
         }

         return m;
      }
   }

   protected int _getterPriority(AnnotatedMethod m) {
      String name = m.getName();
      if (name.startsWith("get") && name.length() > 3) {
         return 1;
      } else {
         return name.startsWith("is") && name.length() > 2 ? 2 : 3;
      }
   }

   protected int _setterPriority(AnnotatedMethod m) {
      String name = m.getName();
      return name.startsWith("set") && name.length() > 3 ? 1 : 2;
   }

   public Class<?>[] findViews() {
      return (Class[])this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<Class<?>[]>() {
         public Class<?>[] withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findViews(member);
         }
      });
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType() {
      AnnotationIntrospector.ReferenceProperty result = this._referenceInfo;
      if (result != null) {
         return result == NOT_REFEFERENCE_PROP ? null : result;
      } else {
         result = (AnnotationIntrospector.ReferenceProperty)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<AnnotationIntrospector.ReferenceProperty>() {
            public AnnotationIntrospector.ReferenceProperty withMember(AnnotatedMember member) {
               return POJOPropertyBuilder.this._annotationIntrospector.findReferenceType(member);
            }
         });
         this._referenceInfo = result == null ? NOT_REFEFERENCE_PROP : result;
         return result;
      }
   }

   public boolean isTypeId() {
      Boolean b = (Boolean)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<Boolean>() {
         public Boolean withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.isTypeId(member);
         }
      });
      return b != null && b;
   }

   protected Boolean _findRequired() {
      return (Boolean)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<Boolean>() {
         public Boolean withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.hasRequiredMarker(member);
         }
      });
   }

   protected String _findDescription() {
      return (String)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<String>() {
         public String withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findPropertyDescription(member);
         }
      });
   }

   protected Integer _findIndex() {
      return (Integer)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<Integer>() {
         public Integer withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findPropertyIndex(member);
         }
      });
   }

   protected String _findDefaultValue() {
      return (String)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<String>() {
         public String withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findPropertyDefaultValue(member);
         }
      });
   }

   public ObjectIdInfo findObjectIdInfo() {
      return (ObjectIdInfo)this.fromMemberAnnotations(new POJOPropertyBuilder.WithMember<ObjectIdInfo>() {
         public ObjectIdInfo withMember(AnnotatedMember member) {
            ObjectIdInfo info = POJOPropertyBuilder.this._annotationIntrospector.findObjectIdInfo(member);
            if (info != null) {
               info = POJOPropertyBuilder.this._annotationIntrospector.findObjectReferenceInfo(member, info);
            }

            return info;
         }
      });
   }

   public JsonInclude.Value findInclusion() {
      AnnotatedMember a = this.getAccessor();
      JsonInclude.Value v = this._annotationIntrospector == null ? null : this._annotationIntrospector.findPropertyInclusion(a);
      return v == null ? JsonInclude.Value.empty() : v;
   }

   public JsonProperty.Access findAccess() {
      return (JsonProperty.Access)this.fromMemberAnnotationsExcept(new POJOPropertyBuilder.WithMember<JsonProperty.Access>() {
         public JsonProperty.Access withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findPropertyAccess(member);
         }
      }, JsonProperty.Access.AUTO);
   }

   public void addField(AnnotatedField a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._fields = new POJOPropertyBuilder.Linked(a, this._fields, name, explName, visible, ignored);
   }

   public void addCtor(AnnotatedParameter a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._ctorParameters = new POJOPropertyBuilder.Linked(a, this._ctorParameters, name, explName, visible, ignored);
   }

   public void addGetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._getters = new POJOPropertyBuilder.Linked(a, this._getters, name, explName, visible, ignored);
   }

   public void addSetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._setters = new POJOPropertyBuilder.Linked(a, this._setters, name, explName, visible, ignored);
   }

   public void addAll(POJOPropertyBuilder src) {
      this._fields = merge(this._fields, src._fields);
      this._ctorParameters = merge(this._ctorParameters, src._ctorParameters);
      this._getters = merge(this._getters, src._getters);
      this._setters = merge(this._setters, src._setters);
   }

   private static <T> POJOPropertyBuilder.Linked<T> merge(POJOPropertyBuilder.Linked<T> chain1, POJOPropertyBuilder.Linked<T> chain2) {
      if (chain1 == null) {
         return chain2;
      } else {
         return chain2 == null ? chain1 : chain1.append(chain2);
      }
   }

   public void removeIgnored() {
      this._fields = this._removeIgnored(this._fields);
      this._getters = this._removeIgnored(this._getters);
      this._setters = this._removeIgnored(this._setters);
      this._ctorParameters = this._removeIgnored(this._ctorParameters);
   }

   public JsonProperty.Access removeNonVisible(boolean inferMutators) {
      JsonProperty.Access acc = this.findAccess();
      if (acc == null) {
         acc = JsonProperty.Access.AUTO;
      }

      switch(acc) {
      case READ_ONLY:
         this._setters = null;
         this._ctorParameters = null;
         if (!this._forSerialization) {
            this._fields = null;
         }
      case READ_WRITE:
         break;
      case WRITE_ONLY:
         this._getters = null;
         if (this._forSerialization) {
            this._fields = null;
         }
         break;
      case AUTO:
      default:
         this._getters = this._removeNonVisible(this._getters);
         this._ctorParameters = this._removeNonVisible(this._ctorParameters);
         if (!inferMutators || this._getters == null) {
            this._fields = this._removeNonVisible(this._fields);
            this._setters = this._removeNonVisible(this._setters);
         }
      }

      return acc;
   }

   public void removeConstructors() {
      this._ctorParameters = null;
   }

   public void trimByVisibility() {
      this._fields = this._trimByVisibility(this._fields);
      this._getters = this._trimByVisibility(this._getters);
      this._setters = this._trimByVisibility(this._setters);
      this._ctorParameters = this._trimByVisibility(this._ctorParameters);
   }

   public void mergeAnnotations(boolean forSerialization) {
      AnnotationMap ann;
      if (forSerialization) {
         if (this._getters != null) {
            ann = this._mergeAnnotations(0, this._getters, this._fields, this._ctorParameters, this._setters);
            this._getters = this._applyAnnotations(this._getters, ann);
         } else if (this._fields != null) {
            ann = this._mergeAnnotations(0, this._fields, this._ctorParameters, this._setters);
            this._fields = this._applyAnnotations(this._fields, ann);
         }
      } else if (this._ctorParameters != null) {
         ann = this._mergeAnnotations(0, this._ctorParameters, this._setters, this._fields, this._getters);
         this._ctorParameters = this._applyAnnotations(this._ctorParameters, ann);
      } else if (this._setters != null) {
         ann = this._mergeAnnotations(0, this._setters, this._fields, this._getters);
         this._setters = this._applyAnnotations(this._setters, ann);
      } else if (this._fields != null) {
         ann = this._mergeAnnotations(0, this._fields, this._getters);
         this._fields = this._applyAnnotations(this._fields, ann);
      }

   }

   private AnnotationMap _mergeAnnotations(int index, POJOPropertyBuilder.Linked<? extends AnnotatedMember>... nodes) {
      AnnotationMap ann = this._getAllAnnotations(nodes[index]);

      do {
         ++index;
         if (index >= nodes.length) {
            return ann;
         }
      } while(nodes[index] == null);

      return AnnotationMap.merge(ann, this._mergeAnnotations(index, nodes));
   }

   private <T extends AnnotatedMember> AnnotationMap _getAllAnnotations(POJOPropertyBuilder.Linked<T> node) {
      AnnotationMap ann = ((AnnotatedMember)node.value).getAllAnnotations();
      if (node.next != null) {
         ann = AnnotationMap.merge(ann, this._getAllAnnotations(node.next));
      }

      return ann;
   }

   private <T extends AnnotatedMember> POJOPropertyBuilder.Linked<T> _applyAnnotations(POJOPropertyBuilder.Linked<T> node, AnnotationMap ann) {
      T value = (AnnotatedMember)((AnnotatedMember)node.value).withAnnotations(ann);
      if (node.next != null) {
         node = node.withNext(this._applyAnnotations(node.next, ann));
      }

      return node.withValue(value);
   }

   private <T> POJOPropertyBuilder.Linked<T> _removeIgnored(POJOPropertyBuilder.Linked<T> node) {
      return node == null ? node : node.withoutIgnored();
   }

   private <T> POJOPropertyBuilder.Linked<T> _removeNonVisible(POJOPropertyBuilder.Linked<T> node) {
      return node == null ? node : node.withoutNonVisible();
   }

   private <T> POJOPropertyBuilder.Linked<T> _trimByVisibility(POJOPropertyBuilder.Linked<T> node) {
      return node == null ? node : node.trimByVisibility();
   }

   private <T> boolean _anyExplicits(POJOPropertyBuilder.Linked<T> n) {
      while(n != null) {
         if (n.name != null && n.name.hasSimpleName()) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   private <T> boolean _anyExplicitNames(POJOPropertyBuilder.Linked<T> n) {
      while(n != null) {
         if (n.name != null && n.isNameExplicit) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public boolean anyVisible() {
      return this._anyVisible(this._fields) || this._anyVisible(this._getters) || this._anyVisible(this._setters) || this._anyVisible(this._ctorParameters);
   }

   private <T> boolean _anyVisible(POJOPropertyBuilder.Linked<T> n) {
      while(n != null) {
         if (n.isVisible) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public boolean anyIgnorals() {
      return this._anyIgnorals(this._fields) || this._anyIgnorals(this._getters) || this._anyIgnorals(this._setters) || this._anyIgnorals(this._ctorParameters);
   }

   private <T> boolean _anyIgnorals(POJOPropertyBuilder.Linked<T> n) {
      while(n != null) {
         if (n.isMarkedIgnored) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public Set<PropertyName> findExplicitNames() {
      Set<PropertyName> renamed = null;
      renamed = this._findExplicitNames(this._fields, renamed);
      renamed = this._findExplicitNames(this._getters, renamed);
      renamed = this._findExplicitNames(this._setters, renamed);
      renamed = this._findExplicitNames(this._ctorParameters, renamed);
      return renamed == null ? Collections.emptySet() : renamed;
   }

   public Collection<POJOPropertyBuilder> explode(Collection<PropertyName> newNames) {
      HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap();
      this._explode(newNames, props, this._fields);
      this._explode(newNames, props, this._getters);
      this._explode(newNames, props, this._setters);
      this._explode(newNames, props, this._ctorParameters);
      return props.values();
   }

   private void _explode(Collection<PropertyName> newNames, Map<PropertyName, POJOPropertyBuilder> props, POJOPropertyBuilder.Linked<?> accessors) {
      POJOPropertyBuilder.Linked<?> firstAcc = accessors;

      for(POJOPropertyBuilder.Linked node = accessors; node != null; node = node.next) {
         PropertyName name = node.name;
         if (node.isNameExplicit && name != null) {
            POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(name);
            if (prop == null) {
               prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, this._internalName, name);
               props.put(name, prop);
            }

            if (firstAcc == this._fields) {
               prop._fields = node.withNext(prop._fields);
            } else if (firstAcc == this._getters) {
               prop._getters = node.withNext(prop._getters);
            } else if (firstAcc == this._setters) {
               prop._setters = node.withNext(prop._setters);
            } else {
               if (firstAcc != this._ctorParameters) {
                  throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
               }

               prop._ctorParameters = node.withNext(prop._ctorParameters);
            }
         } else if (node.isVisible) {
            throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name '" + this._name + "'): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
         }
      }

   }

   private Set<PropertyName> _findExplicitNames(POJOPropertyBuilder.Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed) {
      for(; node != null; node = node.next) {
         if (node.isNameExplicit && node.name != null) {
            if (renamed == null) {
               renamed = new HashSet();
            }

            ((Set)renamed).add(node.name);
         }
      }

      return (Set)renamed;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[Property '").append(this._name).append("'; ctors: ").append(this._ctorParameters).append(", field(s): ").append(this._fields).append(", getter(s): ").append(this._getters).append(", setter(s): ").append(this._setters);
      sb.append("]");
      return sb.toString();
   }

   protected <T> T fromMemberAnnotations(POJOPropertyBuilder.WithMember<T> func) {
      T result = null;
      if (this._annotationIntrospector != null) {
         if (this._forSerialization) {
            if (this._getters != null) {
               result = func.withMember((AnnotatedMember)this._getters.value);
            }
         } else {
            if (this._ctorParameters != null) {
               result = func.withMember((AnnotatedMember)this._ctorParameters.value);
            }

            if (result == null && this._setters != null) {
               result = func.withMember((AnnotatedMember)this._setters.value);
            }
         }

         if (result == null && this._fields != null) {
            result = func.withMember((AnnotatedMember)this._fields.value);
         }
      }

      return result;
   }

   protected <T> T fromMemberAnnotationsExcept(POJOPropertyBuilder.WithMember<T> func, T defaultValue) {
      if (this._annotationIntrospector == null) {
         return null;
      } else {
         Object result;
         if (this._forSerialization) {
            if (this._getters != null) {
               result = func.withMember((AnnotatedMember)this._getters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._fields != null) {
               result = func.withMember((AnnotatedMember)this._fields.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._ctorParameters != null) {
               result = func.withMember((AnnotatedMember)this._ctorParameters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._setters != null) {
               result = func.withMember((AnnotatedMember)this._setters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            return null;
         } else {
            if (this._ctorParameters != null) {
               result = func.withMember((AnnotatedMember)this._ctorParameters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._setters != null) {
               result = func.withMember((AnnotatedMember)this._setters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._fields != null) {
               result = func.withMember((AnnotatedMember)this._fields.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            if (this._getters != null) {
               result = func.withMember((AnnotatedMember)this._getters.value);
               if (result != null && result != defaultValue) {
                  return result;
               }
            }

            return null;
         }
      }
   }

   protected static final class Linked<T> {
      public final T value;
      public final POJOPropertyBuilder.Linked<T> next;
      public final PropertyName name;
      public final boolean isNameExplicit;
      public final boolean isVisible;
      public final boolean isMarkedIgnored;

      public Linked(T v, POJOPropertyBuilder.Linked<T> n, PropertyName name, boolean explName, boolean visible, boolean ignored) {
         this.value = v;
         this.next = n;
         this.name = name != null && !name.isEmpty() ? name : null;
         if (explName) {
            if (this.name == null) {
               throw new IllegalArgumentException("Cannot pass true for 'explName' if name is null/empty");
            }

            if (!name.hasSimpleName()) {
               explName = false;
            }
         }

         this.isNameExplicit = explName;
         this.isVisible = visible;
         this.isMarkedIgnored = ignored;
      }

      public POJOPropertyBuilder.Linked<T> withoutNext() {
         return this.next == null ? this : new POJOPropertyBuilder.Linked(this.value, (POJOPropertyBuilder.Linked)null, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public POJOPropertyBuilder.Linked<T> withValue(T newValue) {
         return newValue == this.value ? this : new POJOPropertyBuilder.Linked(newValue, this.next, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public POJOPropertyBuilder.Linked<T> withNext(POJOPropertyBuilder.Linked<T> newNext) {
         return newNext == this.next ? this : new POJOPropertyBuilder.Linked(this.value, newNext, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public POJOPropertyBuilder.Linked<T> withoutIgnored() {
         if (this.isMarkedIgnored) {
            return this.next == null ? null : this.next.withoutIgnored();
         } else {
            if (this.next != null) {
               POJOPropertyBuilder.Linked<T> newNext = this.next.withoutIgnored();
               if (newNext != this.next) {
                  return this.withNext(newNext);
               }
            }

            return this;
         }
      }

      public POJOPropertyBuilder.Linked<T> withoutNonVisible() {
         POJOPropertyBuilder.Linked<T> newNext = this.next == null ? null : this.next.withoutNonVisible();
         return this.isVisible ? this.withNext(newNext) : newNext;
      }

      protected POJOPropertyBuilder.Linked<T> append(POJOPropertyBuilder.Linked<T> appendable) {
         return this.next == null ? this.withNext(appendable) : this.withNext(this.next.append(appendable));
      }

      public POJOPropertyBuilder.Linked<T> trimByVisibility() {
         if (this.next == null) {
            return this;
         } else {
            POJOPropertyBuilder.Linked<T> newNext = this.next.trimByVisibility();
            if (this.name != null) {
               return newNext.name == null ? this.withNext((POJOPropertyBuilder.Linked)null) : this.withNext(newNext);
            } else if (newNext.name != null) {
               return newNext;
            } else if (this.isVisible == newNext.isVisible) {
               return this.withNext(newNext);
            } else {
               return this.isVisible ? this.withNext((POJOPropertyBuilder.Linked)null) : newNext;
            }
         }
      }

      public String toString() {
         String msg = String.format("%s[visible=%b,ignore=%b,explicitName=%b]", this.value.toString(), this.isVisible, this.isMarkedIgnored, this.isNameExplicit);
         if (this.next != null) {
            msg = msg + ", " + this.next.toString();
         }

         return msg;
      }
   }

   protected static class MemberIterator<T extends AnnotatedMember> implements Iterator<T> {
      private POJOPropertyBuilder.Linked<T> next;

      public MemberIterator(POJOPropertyBuilder.Linked<T> first) {
         this.next = first;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public T next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            T result = (AnnotatedMember)this.next.value;
            this.next = this.next.next;
            return result;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private interface WithMember<T> {
      T withMember(AnnotatedMember var1);
   }
}
