package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeBindings;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public final class AnnotatedClass extends Annotated implements TypeResolutionContext {
   private static final AnnotatedClass.Creators NO_CREATORS = new AnnotatedClass.Creators((AnnotatedConstructor)null, Collections.emptyList(), Collections.emptyList());
   protected final JavaType _type;
   protected final Class<?> _class;
   protected final TypeBindings _bindings;
   protected final List<JavaType> _superTypes;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final TypeFactory _typeFactory;
   protected final ClassIntrospector.MixInResolver _mixInResolver;
   protected final Class<?> _primaryMixIn;
   protected final Annotations _classAnnotations;
   protected AnnotatedClass.Creators _creators;
   protected AnnotatedMethodMap _memberMethods;
   protected List<AnnotatedField> _fields;
   protected transient Boolean _nonStaticInnerClass;

   AnnotatedClass(JavaType type, Class<?> rawType, List<JavaType> superTypes, Class<?> primaryMixIn, Annotations classAnnotations, TypeBindings bindings, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf) {
      this._type = type;
      this._class = rawType;
      this._superTypes = superTypes;
      this._primaryMixIn = primaryMixIn;
      this._classAnnotations = classAnnotations;
      this._bindings = bindings;
      this._annotationIntrospector = aintr;
      this._mixInResolver = mir;
      this._typeFactory = tf;
   }

   AnnotatedClass(Class<?> rawType) {
      this._type = null;
      this._class = rawType;
      this._superTypes = Collections.emptyList();
      this._primaryMixIn = null;
      this._classAnnotations = AnnotationCollector.emptyAnnotations();
      this._bindings = TypeBindings.emptyBindings();
      this._annotationIntrospector = null;
      this._mixInResolver = null;
      this._typeFactory = null;
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config) {
      return construct(type, config, config);
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config, ClassIntrospector.MixInResolver mir) {
      return AnnotatedClassResolver.resolve(config, type, mir);
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config) {
      return constructWithoutSuperTypes(raw, config, config);
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config, ClassIntrospector.MixInResolver mir) {
      return AnnotatedClassResolver.resolveWithoutSuperTypes(config, raw, mir);
   }

   public JavaType resolveType(Type type) {
      return this._typeFactory.constructType(type, this._bindings);
   }

   public Class<?> getAnnotated() {
      return this._class;
   }

   public int getModifiers() {
      return this._class.getModifiers();
   }

   public String getName() {
      return this._class.getName();
   }

   public <A extends Annotation> A getAnnotation(Class<A> acls) {
      return this._classAnnotations.get(acls);
   }

   public boolean hasAnnotation(Class<?> acls) {
      return this._classAnnotations.has(acls);
   }

   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
      return this._classAnnotations.hasOneOf(annoClasses);
   }

   public Class<?> getRawType() {
      return this._class;
   }

   public JavaType getType() {
      return this._type;
   }

   public Annotations getAnnotations() {
      return this._classAnnotations;
   }

   public boolean hasAnnotations() {
      return this._classAnnotations.size() > 0;
   }

   public AnnotatedConstructor getDefaultConstructor() {
      return this._creators().defaultConstructor;
   }

   public List<AnnotatedConstructor> getConstructors() {
      return this._creators().constructors;
   }

   public List<AnnotatedMethod> getFactoryMethods() {
      return this._creators().creatorMethods;
   }

   /** @deprecated */
   @Deprecated
   public List<AnnotatedMethod> getStaticMethods() {
      return this.getFactoryMethods();
   }

   public Iterable<AnnotatedMethod> memberMethods() {
      return this._methods();
   }

   public int getMemberMethodCount() {
      return this._methods().size();
   }

   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
      return this._methods().find(name, paramTypes);
   }

   public int getFieldCount() {
      return this._fields().size();
   }

   public Iterable<AnnotatedField> fields() {
      return this._fields();
   }

   public boolean isNonStaticInnerClass() {
      Boolean B = this._nonStaticInnerClass;
      if (B == null) {
         this._nonStaticInnerClass = B = ClassUtil.isNonStaticInnerClass(this._class);
      }

      return B;
   }

   private final List<AnnotatedField> _fields() {
      List<AnnotatedField> f = this._fields;
      if (f == null) {
         if (this._type == null) {
            f = Collections.emptyList();
         } else {
            f = AnnotatedFieldCollector.collectFields(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type);
         }

         this._fields = f;
      }

      return f;
   }

   private final AnnotatedMethodMap _methods() {
      AnnotatedMethodMap m = this._memberMethods;
      if (m == null) {
         if (this._type == null) {
            m = new AnnotatedMethodMap();
         } else {
            m = AnnotatedMethodCollector.collectMethods(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._superTypes, this._primaryMixIn);
         }

         this._memberMethods = m;
      }

      return m;
   }

   private final AnnotatedClass.Creators _creators() {
      AnnotatedClass.Creators c = this._creators;
      if (c == null) {
         if (this._type == null) {
            c = NO_CREATORS;
         } else {
            c = AnnotatedCreatorCollector.collectCreators(this._annotationIntrospector, this, this._type, this._primaryMixIn);
         }

         this._creators = c;
      }

      return c;
   }

   public String toString() {
      return "[AnnotedClass " + this._class.getName() + "]";
   }

   public int hashCode() {
      return this._class.getName().hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         return ((AnnotatedClass)o)._class == this._class;
      }
   }

   public static final class Creators {
      public final AnnotatedConstructor defaultConstructor;
      public final List<AnnotatedConstructor> constructors;
      public final List<AnnotatedMethod> creatorMethods;

      public Creators(AnnotatedConstructor defCtor, List<AnnotatedConstructor> ctors, List<AnnotatedMethod> ctorMethods) {
         this.defaultConstructor = defCtor;
         this.constructors = ctors;
         this.creatorMethods = ctorMethods;
      }
   }
}
