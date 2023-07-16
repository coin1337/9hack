package software.bernie.shadowed.fasterxml.jackson.databind.ext;

import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.nio.file.Path;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

public class Java7SupportImpl extends Java7Support {
   private final Class<?> _bogus;

   public Java7SupportImpl() {
      Class<?> cls = Transient.class;
      cls = ConstructorProperties.class;
      this._bogus = cls;
   }

   public Class<?> getClassJavaNioFilePath() {
      return Path.class;
   }

   public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
      return rawType == Path.class ? new NioPathDeserializer() : null;
   }

   public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
      return Path.class.isAssignableFrom(rawType) ? new NioPathSerializer() : null;
   }

   public Boolean findTransient(Annotated a) {
      Transient t = (Transient)a.getAnnotation(Transient.class);
      return t != null ? t.value() : null;
   }

   public Boolean hasCreatorAnnotation(Annotated a) {
      ConstructorProperties props = (ConstructorProperties)a.getAnnotation(ConstructorProperties.class);
      return props != null ? Boolean.TRUE : null;
   }

   public PropertyName findConstructorName(AnnotatedParameter p) {
      AnnotatedWithParams ctor = p.getOwner();
      if (ctor != null) {
         ConstructorProperties props = (ConstructorProperties)ctor.getAnnotation(ConstructorProperties.class);
         if (props != null) {
            String[] names = props.value();
            int ix = p.getIndex();
            if (ix < names.length) {
               return PropertyName.construct(names[ix]);
            }
         }
      }

      return null;
   }
}
