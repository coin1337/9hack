package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public abstract class AnnotatedMember extends Annotated implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final transient TypeResolutionContext _typeContext;
   protected final transient AnnotationMap _annotations;

   protected AnnotatedMember(TypeResolutionContext ctxt, AnnotationMap annotations) {
      this._typeContext = ctxt;
      this._annotations = annotations;
   }

   protected AnnotatedMember(AnnotatedMember base) {
      this._typeContext = base._typeContext;
      this._annotations = base._annotations;
   }

   public abstract Annotated withAnnotations(AnnotationMap var1);

   public abstract Class<?> getDeclaringClass();

   public abstract Member getMember();

   public String getFullName() {
      return this.getDeclaringClass().getName() + "#" + this.getName();
   }

   /** @deprecated */
   @Deprecated
   public TypeResolutionContext getTypeContext() {
      return this._typeContext;
   }

   public final <A extends Annotation> A getAnnotation(Class<A> acls) {
      return this._annotations == null ? null : this._annotations.get(acls);
   }

   public final boolean hasAnnotation(Class<?> acls) {
      return this._annotations == null ? false : this._annotations.has(acls);
   }

   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
      return this._annotations == null ? false : this._annotations.hasOneOf(annoClasses);
   }

   public AnnotationMap getAllAnnotations() {
      return this._annotations;
   }

   public final void fixAccess(boolean force) {
      Member m = this.getMember();
      if (m != null) {
         ClassUtil.checkAndFixAccess(m, force);
      }

   }

   public abstract void setValue(Object var1, Object var2) throws UnsupportedOperationException, IllegalArgumentException;

   public abstract Object getValue(Object var1) throws UnsupportedOperationException, IllegalArgumentException;
}
