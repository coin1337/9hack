package software.bernie.shadowed.fasterxml.jackson.databind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonAnnotation;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerialize {
   Class<? extends JsonSerializer> using() default JsonSerializer.None.class;

   Class<? extends JsonSerializer> contentUsing() default JsonSerializer.None.class;

   Class<? extends JsonSerializer> keyUsing() default JsonSerializer.None.class;

   Class<? extends JsonSerializer> nullsUsing() default JsonSerializer.None.class;

   Class<?> as() default Void.class;

   Class<?> keyAs() default Void.class;

   Class<?> contentAs() default Void.class;

   JsonSerialize.Typing typing() default JsonSerialize.Typing.DEFAULT_TYPING;

   Class<? extends Converter> converter() default Converter.None.class;

   Class<? extends Converter> contentConverter() default Converter.None.class;

   /** @deprecated */
   @Deprecated
   JsonSerialize.Inclusion include() default JsonSerialize.Inclusion.DEFAULT_INCLUSION;

   public static enum Typing {
      DYNAMIC,
      STATIC,
      DEFAULT_TYPING;
   }

   /** @deprecated */
   @Deprecated
   public static enum Inclusion {
      ALWAYS,
      NON_NULL,
      NON_DEFAULT,
      NON_EMPTY,
      DEFAULT_INCLUSION;
   }
}
