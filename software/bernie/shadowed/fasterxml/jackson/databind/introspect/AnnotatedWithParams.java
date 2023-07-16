package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public abstract class AnnotatedWithParams extends AnnotatedMember {
   private static final long serialVersionUID = 1L;
   protected final AnnotationMap[] _paramAnnotations;

   protected AnnotatedWithParams(TypeResolutionContext ctxt, AnnotationMap annotations, AnnotationMap[] paramAnnotations) {
      super(ctxt, annotations);
      this._paramAnnotations = paramAnnotations;
   }

   protected AnnotatedWithParams(AnnotatedWithParams base, AnnotationMap[] paramAnnotations) {
      super(base);
      this._paramAnnotations = paramAnnotations;
   }

   public final void addOrOverrideParam(int paramIndex, Annotation a) {
      AnnotationMap old = this._paramAnnotations[paramIndex];
      if (old == null) {
         old = new AnnotationMap();
         this._paramAnnotations[paramIndex] = old;
      }

      old.add(a);
   }

   protected AnnotatedParameter replaceParameterAnnotations(int index, AnnotationMap ann) {
      this._paramAnnotations[index] = ann;
      return this.getParameter(index);
   }

   public final AnnotationMap getParameterAnnotations(int index) {
      return this._paramAnnotations != null && index >= 0 && index < this._paramAnnotations.length ? this._paramAnnotations[index] : null;
   }

   public final AnnotatedParameter getParameter(int index) {
      return new AnnotatedParameter(this, this.getParameterType(index), this._typeContext, this.getParameterAnnotations(index), index);
   }

   public abstract int getParameterCount();

   public abstract Class<?> getRawParameterType(int var1);

   public abstract JavaType getParameterType(int var1);

   /** @deprecated */
   @Deprecated
   public abstract Type getGenericParameterType(int var1);

   public final int getAnnotationCount() {
      return this._annotations.size();
   }

   public abstract Object call() throws Exception;

   public abstract Object call(Object[] var1) throws Exception;

   public abstract Object call1(Object var1) throws Exception;
}
