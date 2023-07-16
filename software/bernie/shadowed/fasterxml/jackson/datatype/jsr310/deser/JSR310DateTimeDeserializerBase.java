package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;

public abstract class JSR310DateTimeDeserializerBase<T> extends JSR310DeserializerBase<T> implements ContextualDeserializer {
   protected final DateTimeFormatter _formatter;

   protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f) {
      super(supportedType);
      this._formatter = f;
   }

   protected abstract JsonDeserializer<T> withDateFormat(DateTimeFormatter var1);

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonFormat.Value format = this.findFormatOverrides(ctxt, property, this.handledType());
      if (format != null && format.hasPattern()) {
         String pattern = format.getPattern();
         Locale locale = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
         DateTimeFormatter df;
         if (locale == null) {
            df = DateTimeFormatter.ofPattern(pattern);
         } else {
            df = DateTimeFormatter.ofPattern(pattern, locale);
         }

         if (format.hasTimeZone()) {
            df = df.withZone(format.getTimeZone().toZoneId());
         }

         return this.withDateFormat(df);
      } else {
         return this;
      }
   }
}
