package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class AnnotatedMethodCollector extends CollectorBase {
   private final ClassIntrospector.MixInResolver _mixInResolver;

   AnnotatedMethodCollector(AnnotationIntrospector intr, ClassIntrospector.MixInResolver mixins) {
      super(intr);
      this._mixInResolver = intr == null ? null : mixins;
   }

   public static AnnotatedMethodMap collectMethods(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, List<JavaType> superTypes, Class<?> primaryMixIn) {
      return (new AnnotatedMethodCollector(intr, mixins)).collect(types, tc, type, superTypes, primaryMixIn);
   }

   AnnotatedMethodMap collect(TypeFactory typeFactory, TypeResolutionContext tc, JavaType mainType, List<JavaType> superTypes, Class<?> primaryMixIn) {
      Map<MemberKey, AnnotatedMethodCollector.MethodBuilder> methods = new LinkedHashMap();
      this._addMemberMethods(tc, mainType.getRawClass(), methods, primaryMixIn);
      Iterator i$ = superTypes.iterator();

      while(i$.hasNext()) {
         JavaType type = (JavaType)i$.next();
         Class<?> mixin = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(type.getRawClass());
         this._addMemberMethods(new TypeResolutionContext.Basic(typeFactory, type.getBindings()), type.getRawClass(), methods, mixin);
      }

      if (methods.isEmpty()) {
         return new AnnotatedMethodMap();
      } else {
         Map<MemberKey, AnnotatedMethod> actual = new LinkedHashMap(methods.size());
         Iterator i$ = methods.entrySet().iterator();

         while(i$.hasNext()) {
            Entry<MemberKey, AnnotatedMethodCollector.MethodBuilder> entry = (Entry)i$.next();
            AnnotatedMethod am = ((AnnotatedMethodCollector.MethodBuilder)entry.getValue()).build();
            if (am != null) {
               actual.put(entry.getKey(), am);
            }
         }

         return new AnnotatedMethodMap(actual);
      }
   }

   private void _addMemberMethods(TypeResolutionContext tc, Class<?> cls, Map<MemberKey, AnnotatedMethodCollector.MethodBuilder> methods, Class<?> mixInCls) {
      if (mixInCls != null) {
         this._addMethodMixIns(tc, cls, methods, mixInCls);
      }

      if (cls != null) {
         Method[] arr$ = ClassUtil.getClassMethods(cls);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Method m = arr$[i$];
            if (this._isIncludableMemberMethod(m)) {
               MemberKey key = new MemberKey(m);
               AnnotatedMethodCollector.MethodBuilder b = (AnnotatedMethodCollector.MethodBuilder)methods.get(key);
               if (b == null) {
                  AnnotationCollector c = this._intr == null ? AnnotationCollector.emptyCollector() : this.collectAnnotations(m.getDeclaredAnnotations());
                  methods.put(key, new AnnotatedMethodCollector.MethodBuilder(tc, m, c));
               } else {
                  if (this._intr != null) {
                     b.annotations = this.collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
                  }

                  Method old = b.method;
                  if (old == null) {
                     b.method = m;
                  } else if (Modifier.isAbstract(old.getModifiers()) && !Modifier.isAbstract(m.getModifiers())) {
                     b.method = m;
                  }
               }
            }
         }

      }
   }

   protected void _addMethodMixIns(TypeResolutionContext tc, Class<?> targetClass, Map<MemberKey, AnnotatedMethodCollector.MethodBuilder> methods, Class<?> mixInCls) {
      if (this._intr != null) {
         Iterator i$ = ClassUtil.findRawSuperTypes(mixInCls, targetClass, true).iterator();

         while(i$.hasNext()) {
            Class<?> mixin = (Class)i$.next();
            Method[] arr$ = ClassUtil.getDeclaredMethods(mixin);
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               Method m = arr$[i$];
               if (this._isIncludableMemberMethod(m)) {
                  MemberKey key = new MemberKey(m);
                  AnnotatedMethodCollector.MethodBuilder b = (AnnotatedMethodCollector.MethodBuilder)methods.get(key);
                  Annotation[] anns = m.getDeclaredAnnotations();
                  if (b == null) {
                     methods.put(key, new AnnotatedMethodCollector.MethodBuilder(tc, (Method)null, this.collectAnnotations(anns)));
                  } else {
                     b.annotations = this.collectDefaultAnnotations(b.annotations, anns);
                  }
               }
            }
         }

      }
   }

   private boolean _isIncludableMemberMethod(Method m) {
      if (!Modifier.isStatic(m.getModifiers()) && !m.isSynthetic() && !m.isBridge()) {
         int pcount = m.getParameterTypes().length;
         return pcount <= 2;
      } else {
         return false;
      }
   }

   private static final class MethodBuilder {
      public final TypeResolutionContext typeContext;
      public Method method;
      public AnnotationCollector annotations;

      public MethodBuilder(TypeResolutionContext tc, Method m, AnnotationCollector ann) {
         this.typeContext = tc;
         this.method = m;
         this.annotations = ann;
      }

      public AnnotatedMethod build() {
         return this.method == null ? null : new AnnotatedMethod(this.typeContext, this.method, this.annotations.asAnnotationMap(), (AnnotationMap[])null);
      }
   }
}
