package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.PrettyPrinter;

public class MinimalPrettyPrinter implements PrettyPrinter, Serializable {
   private static final long serialVersionUID = 1L;
   protected String _rootValueSeparator;
   protected Separators _separators;

   public MinimalPrettyPrinter() {
      this(DEFAULT_ROOT_VALUE_SEPARATOR.toString());
   }

   public MinimalPrettyPrinter(String rootValueSeparator) {
      this._rootValueSeparator = rootValueSeparator;
      this._separators = DEFAULT_SEPARATORS;
   }

   public void setRootValueSeparator(String sep) {
      this._rootValueSeparator = sep;
   }

   public MinimalPrettyPrinter setSeparators(Separators separators) {
      this._separators = separators;
      return this;
   }

   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
      if (this._rootValueSeparator != null) {
         g.writeRaw(this._rootValueSeparator);
      }

   }

   public void writeStartObject(JsonGenerator g) throws IOException {
      g.writeRaw('{');
   }

   public void beforeObjectEntries(JsonGenerator g) throws IOException {
   }

   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._separators.getObjectFieldValueSeparator());
   }

   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._separators.getObjectEntrySeparator());
   }

   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
      g.writeRaw('}');
   }

   public void writeStartArray(JsonGenerator g) throws IOException {
      g.writeRaw('[');
   }

   public void beforeArrayValues(JsonGenerator g) throws IOException {
   }

   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._separators.getArrayValueSeparator());
   }

   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
      g.writeRaw(']');
   }
}
