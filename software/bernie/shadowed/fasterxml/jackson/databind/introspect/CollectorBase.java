package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

class CollectorBase {
   protected static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
   protected static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
   protected final AnnotationIntrospector _intr;

   protected CollectorBase(AnnotationIntrospector intr) {
      this._intr = intr;
   }

   protected final AnnotationCollector collectAnnotations(Annotation[] anns) {
      AnnotationCollector c = AnnotationCollector.emptyCollector();
      int i = 0;

      for(int end = anns.length; i < end; ++i) {
         Annotation ann = anns[i];
         c = c.addOrOverride(ann);
         if (this._intr.isAnnotationBundle(ann)) {
            c = this.collectFromBundle(c, ann);
         }
      }

      return c;
   }

   protected final AnnotationCollector collectAnnotations(AnnotationCollector c, Annotation[] anns) {
      int i = 0;

      for(int end = anns.length; i < end; ++i) {
         Annotation ann = anns[i];
         c = c.addOrOverride(ann);
         if (this._intr.isAnnotationBundle(ann)) {
            c = this.collectFromBundle(c, ann);
         }
      }

      return c;
   }

   protected final AnnotationCollector collectFromBundle(AnnotationCollector c, Annotation bundle) {
      Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
      int i = 0;

      for(int end = anns.length; i < end; ++i) {
         Annotation ann = anns[i];
         if (!_ignorableAnnotation(ann)) {
            if (this._intr.isAnnotationBundle(ann)) {
               if (!c.isPresent(ann)) {
                  c = c.addOrOverride(ann);
                  c = this.collectFromBundle(c, ann);
               }
            } else {
               c = c.addOrOverride(ann);
            }
         }
      }

      return c;
   }

   protected final AnnotationCollector collectDefaultAnnotations(AnnotationCollector c, Annotation[] anns) {
      int i = 0;

      for(int end = anns.length; i < end; ++i) {
         Annotation ann = anns[i];
         if (!c.isPresent(ann)) {
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
               c = this.collectDefaultFromBundle(c, ann);
            }
         }
      }

      return c;
   }

   protected final AnnotationCollector collectDefaultFromBundle(AnnotationCollector c, Annotation bundle) {
      Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
      int i = 0;

      for(int end = anns.length; i < end; ++i) {
         Annotation ann = anns[i];
         if (!_ignorableAnnotation(ann) && !c.isPresent(ann)) {
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
               c = this.collectFromBundle(c, ann);
            }
         }
      }

      return c;
   }

   protected static final boolean _ignorableAnnotation(Annotation a) {
      return a instanceof Target || a instanceof Retention;
   }

   static AnnotationMap _emptyAnnotationMap() {
      return new AnnotationMap();
   }

   static AnnotationMap[] _emptyAnnotationMaps(int count) {
      if (count == 0) {
         return NO_ANNOTATION_MAPS;
      } else {
         AnnotationMap[] maps = new AnnotationMap[count];

         for(int i = 0; i < count; ++i) {
            maps[i] = _emptyAnnotationMap();
         }

         return maps;
      }
   }
}
