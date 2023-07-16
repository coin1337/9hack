package software.bernie.shadowed.fasterxml.jackson.databind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonAnnotation;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAppend {
   JsonAppend.Attr[] attrs() default {};

   JsonAppend.Prop[] props() default {};

   boolean prepend() default false;

   public @interface Prop {
      Class<? extends VirtualBeanPropertyWriter> value();

      String name() default "";

      String namespace() default "";

      JsonInclude.Include include() default JsonInclude.Include.NON_NULL;

      boolean required() default false;

      Class<?> type() default Object.class;
   }

   public @interface Attr {
      String value();

      String propName() default "";

      String propNamespace() default "";

      JsonInclude.Include include() default JsonInclude.Include.NON_NULL;

      boolean required() default false;
   }
}
