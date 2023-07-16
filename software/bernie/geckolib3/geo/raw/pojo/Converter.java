package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ObjectMapper;
import software.bernie.shadowed.fasterxml.jackson.databind.ObjectReader;
import software.bernie.shadowed.fasterxml.jackson.databind.ObjectWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.module.SimpleModule;

public class Converter {
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   private static final DateTimeFormatter TIME_FORMATTER;
   private static ObjectReader reader;
   private static ObjectWriter writer;

   public static OffsetDateTime parseDateTimeString(String str) {
      return ZonedDateTime.from(DATE_TIME_FORMATTER.parse(str)).toOffsetDateTime();
   }

   public static OffsetTime parseTimeString(String str) {
      return ZonedDateTime.from(TIME_FORMATTER.parse(str)).toOffsetDateTime().toOffsetTime();
   }

   public static RawGeoModel fromJsonString(String json) throws IOException {
      return (RawGeoModel)getObjectReader().readValue(json);
   }

   public static String toJsonString(RawGeoModel obj) throws JsonProcessingException {
      return getObjectWriter().writeValueAsString(obj);
   }

   private static void instantiateMapper() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.findAndRegisterModules();
      mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      SimpleModule module = new SimpleModule();
      module.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
         public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String value = jsonParser.getText();
            return Converter.parseDateTimeString(value);
         }
      });
      mapper.registerModule(module);
      reader = mapper.readerFor(RawGeoModel.class);
      writer = mapper.writerFor(RawGeoModel.class);
   }

   private static ObjectReader getObjectReader() {
      if (reader == null) {
         instantiateMapper();
      }

      return reader;
   }

   private static ObjectWriter getObjectWriter() {
      if (writer == null) {
         instantiateMapper();
      }

      return writer;
   }

   static {
      DATE_TIME_FORMATTER = (new DateTimeFormatterBuilder()).appendOptional(DateTimeFormatter.ISO_DATE_TIME).appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME).appendOptional(DateTimeFormatter.ISO_INSTANT).appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SX")).appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")).appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toFormatter().withZone(ZoneOffset.UTC);
      TIME_FORMATTER = (new DateTimeFormatterBuilder()).appendOptional(DateTimeFormatter.ISO_TIME).appendOptional(DateTimeFormatter.ISO_OFFSET_TIME).parseDefaulting(ChronoField.YEAR, 2020L).parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L).parseDefaulting(ChronoField.DAY_OF_MONTH, 1L).toFormatter().withZone(ZoneOffset.UTC);
   }
}
