package software.bernie.shadowed.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonCreator {
   JsonCreator.Mode mode() default JsonCreator.Mode.DEFAULT;

   public static enum Mode {
      DEFAULT,
      DELEGATING,
      PROPERTIES,
      DISABLED;
   }
}
