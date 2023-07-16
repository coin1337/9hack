package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

final class AnnotatedCreatorCollector extends CollectorBase {
   private final TypeResolutionContext _typeContext;
   private AnnotatedConstructor _defaultConstructor;

   AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc) {
      super(intr);
      this._typeContext = tc;
   }

   public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn) {
      return (new AnnotatedCreatorCollector(intr, tc)).collect(type, primaryMixIn);
   }

   AnnotatedClass.Creators collect(JavaType type, Class<?> primaryMixIn) {
      List<AnnotatedConstructor> constructors = this._findPotentialConstructors(type, primaryMixIn);
      List<AnnotatedMethod> factories = this._findPotentialFactories(type, primaryMixIn);
      if (this._intr != null) {
         if (this._defaultConstructor != null && this._intr.hasIgnoreMarker(this._defaultConstructor)) {
            this._defaultConstructor = null;
         }

         int i = constructors.size();

         while(true) {
            --i;
            if (i < 0) {
               i = factories.size();

               while(true) {
                  --i;
                  if (i < 0) {
                     return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
                  }

                  if (this._intr.hasIgnoreMarker((AnnotatedMember)factories.get(i))) {
                     factories.remove(i);
                  }
               }
            }

            if (this._intr.hasIgnoreMarker((AnnotatedMember)constructors.get(i))) {
               constructors.remove(i);
            }
         }
      } else {
         return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
      }
   }

   private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn) {
      ClassUtil.Ctor defaultCtor = null;
      List<ClassUtil.Ctor> ctors = null;
      int i;
      if (!type.isEnumType()) {
         ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());
         ClassUtil.Ctor[] arr$ = declaredCtors;
         i = declaredCtors.length;

         for(int i$ = 0; i$ < i; ++i$) {
            ClassUtil.Ctor ctor = arr$[i$];
            if (isIncludableConstructor(ctor.getConstructor())) {
               if (ctor.getParamCount() == 0) {
                  defaultCtor = ctor;
               } else {
                  if (ctors == null) {
                     ctors = new ArrayList();
                  }

                  ctors.add(ctor);
               }
            }
         }
      }

      Object result;
      int ctorCount;
      if (ctors == null) {
         result = Collections.emptyList();
         if (defaultCtor == null) {
            return (List)result;
         }

         ctorCount = 0;
      } else {
         ctorCount = ctors.size();
         result = new ArrayList(ctorCount);

         for(i = 0; i < ctorCount; ++i) {
            ((List)result).add((Object)null);
         }
      }

      if (primaryMixIn != null) {
         MemberKey[] ctorKeys = null;
         ClassUtil.Ctor[] arr$ = ClassUtil.getConstructors(primaryMixIn);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ClassUtil.Ctor mixinCtor = arr$[i$];
            if (mixinCtor.getParamCount() == 0) {
               if (defaultCtor != null) {
                  this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, mixinCtor);
                  defaultCtor = null;
               }
            } else if (ctors != null) {
               if (ctorKeys == null) {
                  ctorKeys = new MemberKey[ctorCount];

                  for(int i = 0; i < ctorCount; ++i) {
                     ctorKeys[i] = new MemberKey(((ClassUtil.Ctor)ctors.get(i)).getConstructor());
                  }
               }

               MemberKey key = new MemberKey(mixinCtor.getConstructor());

               for(int i = 0; i < ctorCount; ++i) {
                  if (key.equals(ctorKeys[i])) {
                     ((List)result).set(i, this.constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), mixinCtor));
                     break;
                  }
               }
            }
         }
      }

      if (defaultCtor != null) {
         this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, (ClassUtil.Ctor)null);
      }

      for(i = 0; i < ctorCount; ++i) {
         AnnotatedConstructor ctor = (AnnotatedConstructor)((List)result).get(i);
         if (ctor == null) {
            ((List)result).set(i, this.constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), (ClassUtil.Ctor)null));
         }
      }

      return (List)result;
   }

   private List<AnnotatedMethod> _findPotentialFactories(JavaType type, Class<?> primaryMixIn) {
      List<Method> candidates = null;
      Method[] arr$ = ClassUtil.getClassMethods(type.getRawClass());
      int len$ = arr$.length;

      int i;
      for(i = 0; i < len$; ++i) {
         Method m = arr$[i];
         if (Modifier.isStatic(m.getModifiers())) {
            if (candidates == null) {
               candidates = new ArrayList();
            }

            candidates.add(m);
         }
      }

      if (candidates == null) {
         return Collections.emptyList();
      } else {
         int factoryCount = candidates.size();
         List<AnnotatedMethod> result = new ArrayList(factoryCount);

         for(i = 0; i < factoryCount; ++i) {
            result.add((Object)null);
         }

         if (primaryMixIn != null) {
            MemberKey[] methodKeys = null;
            Method[] arr$ = ClassUtil.getDeclaredMethods(primaryMixIn);
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               Method mixinFactory = arr$[i$];
               if (Modifier.isStatic(mixinFactory.getModifiers())) {
                  if (methodKeys == null) {
                     methodKeys = new MemberKey[factoryCount];

                     for(int i = 0; i < factoryCount; ++i) {
                        methodKeys[i] = new MemberKey((Method)candidates.get(i));
                     }
                  }

                  MemberKey key = new MemberKey(mixinFactory);

                  for(int i = 0; i < factoryCount; ++i) {
                     if (key.equals(methodKeys[i])) {
                        result.set(i, this.constructFactoryCreator((Method)candidates.get(i), mixinFactory));
                        break;
                     }
                  }
               }
            }
         }

         for(i = 0; i < factoryCount; ++i) {
            AnnotatedMethod factory = (AnnotatedMethod)result.get(i);
            if (factory == null) {
               result.set(i, this.constructFactoryCreator((Method)candidates.get(i), (Method)null));
            }
         }

         return result;
      }
   }

   protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
      return this._intr == null ? new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), NO_ANNOTATION_MAPS) : new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), this.collectAnnotations(ctor.getConstructor().getParameterAnnotations(), mixin == null ? (Annotation[][])null : mixin.getConstructor().getParameterAnnotations()));
   }

   protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
      int paramCount = ctor.getParamCount();
      if (this._intr == null) {
         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
      } else if (paramCount == 0) {
         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
      } else {
         Annotation[][] paramAnns = ctor.getParameterAnnotations();
         AnnotationMap[] resolvedAnnotations;
         if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            Annotation[][] old;
            if (dc.isEnum() && paramCount == paramAnns.length + 2) {
               old = paramAnns;
               paramAnns = new Annotation[paramAnns.length + 2][];
               System.arraycopy(old, 0, paramAnns, 2, old.length);
               resolvedAnnotations = this.collectAnnotations(paramAnns, (Annotation[][])null);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
               old = paramAnns;
               paramAnns = new Annotation[paramAnns.length + 1][];
               System.arraycopy(old, 0, paramAnns, 1, old.length);
               paramAnns[0] = NO_ANNOTATIONS;
               resolvedAnnotations = this.collectAnnotations(paramAnns, (Annotation[][])null);
            }

            if (resolvedAnnotations == null) {
               throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", ctor.getDeclaringClass().getName(), paramCount, paramAnns.length));
            }
         } else {
            resolvedAnnotations = this.collectAnnotations(paramAnns, mixin == null ? (Annotation[][])null : mixin.getParameterAnnotations());
         }

         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), resolvedAnnotations);
      }
   }

   protected AnnotatedMethod constructFactoryCreator(Method m, Method mixin) {
      int paramCount = m.getParameterTypes().length;
      if (this._intr == null) {
         return new AnnotatedMethod(this._typeContext, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
      } else {
         return paramCount == 0 ? new AnnotatedMethod(this._typeContext, m, this.collectAnnotations((AnnotatedElement)m, (AnnotatedElement)mixin), NO_ANNOTATION_MAPS) : new AnnotatedMethod(this._typeContext, m, this.collectAnnotations((AnnotatedElement)m, (AnnotatedElement)mixin), this.collectAnnotations(m.getParameterAnnotations(), mixin == null ? (Annotation[][])null : mixin.getParameterAnnotations()));
      }
   }

   private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
      int count = mainAnns.length;
      AnnotationMap[] result = new AnnotationMap[count];

      for(int i = 0; i < count; ++i) {
         AnnotationCollector c = this.collectAnnotations((AnnotationCollector)AnnotationCollector.emptyCollector(), (Annotation[])mainAnns[i]);
         if (mixinAnns != null) {
            c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixinAnns[i]);
         }

         result[i] = c.asAnnotationMap();
      }

      return result;
   }

   private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin) {
      AnnotationCollector c = this.collectAnnotations(main.getConstructor().getDeclaredAnnotations());
      if (mixin != null) {
         c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixin.getConstructor().getDeclaredAnnotations());
      }

      return c.asAnnotationMap();
   }

   private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
      AnnotationCollector c = this.collectAnnotations(main.getDeclaredAnnotations());
      if (mixin != null) {
         c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixin.getDeclaredAnnotations());
      }

      return c.asAnnotationMap();
   }

   private static boolean isIncludableConstructor(Constructor<?> c) {
      return !c.isSynthetic();
   }
}
