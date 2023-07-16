package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.PrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.io.SerializedString;

public class DefaultPrettyPrinter implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable {
   private static final long serialVersionUID = 1L;
   public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
   protected DefaultPrettyPrinter.Indenter _arrayIndenter;
   protected DefaultPrettyPrinter.Indenter _objectIndenter;
   protected final SerializableString _rootSeparator;
   protected boolean _spacesInObjectEntries;
   protected transient int _nesting;
   protected Separators _separators;
   protected String _objectFieldValueSeparatorWithSpaces;

   public DefaultPrettyPrinter() {
      this((SerializableString)DEFAULT_ROOT_VALUE_SEPARATOR);
   }

   public DefaultPrettyPrinter(String rootSeparator) {
      this((SerializableString)(rootSeparator == null ? null : new SerializedString(rootSeparator)));
   }

   public DefaultPrettyPrinter(SerializableString rootSeparator) {
      this._arrayIndenter = DefaultPrettyPrinter.FixedSpaceIndenter.instance;
      this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
      this._spacesInObjectEntries = true;
      this._rootSeparator = rootSeparator;
      this.withSeparators(DEFAULT_SEPARATORS);
   }

   public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
      this(base, base._rootSeparator);
   }

   public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
      this._arrayIndenter = DefaultPrettyPrinter.FixedSpaceIndenter.instance;
      this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
      this._spacesInObjectEntries = true;
      this._arrayIndenter = base._arrayIndenter;
      this._objectIndenter = base._objectIndenter;
      this._spacesInObjectEntries = base._spacesInObjectEntries;
      this._nesting = base._nesting;
      this._separators = base._separators;
      this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
      this._rootSeparator = rootSeparator;
   }

   public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
      return this._rootSeparator != rootSeparator && (rootSeparator == null || !rootSeparator.equals(this._rootSeparator)) ? new DefaultPrettyPrinter(this, rootSeparator) : this;
   }

   public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
      return this.withRootSeparator((SerializableString)(rootSeparator == null ? null : new SerializedString(rootSeparator)));
   }

   public void indentArraysWith(DefaultPrettyPrinter.Indenter i) {
      this._arrayIndenter = (DefaultPrettyPrinter.Indenter)(i == null ? DefaultPrettyPrinter.NopIndenter.instance : i);
   }

   public void indentObjectsWith(DefaultPrettyPrinter.Indenter i) {
      this._objectIndenter = (DefaultPrettyPrinter.Indenter)(i == null ? DefaultPrettyPrinter.NopIndenter.instance : i);
   }

   public DefaultPrettyPrinter withArrayIndenter(DefaultPrettyPrinter.Indenter i) {
      if (i == null) {
         i = DefaultPrettyPrinter.NopIndenter.instance;
      }

      if (this._arrayIndenter == i) {
         return this;
      } else {
         DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
         pp._arrayIndenter = (DefaultPrettyPrinter.Indenter)i;
         return pp;
      }
   }

   public DefaultPrettyPrinter withObjectIndenter(DefaultPrettyPrinter.Indenter i) {
      if (i == null) {
         i = DefaultPrettyPrinter.NopIndenter.instance;
      }

      if (this._objectIndenter == i) {
         return this;
      } else {
         DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
         pp._objectIndenter = (DefaultPrettyPrinter.Indenter)i;
         return pp;
      }
   }

   public DefaultPrettyPrinter withSpacesInObjectEntries() {
      return this._withSpaces(true);
   }

   public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
      return this._withSpaces(false);
   }

   protected DefaultPrettyPrinter _withSpaces(boolean state) {
      if (this._spacesInObjectEntries == state) {
         return this;
      } else {
         DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
         pp._spacesInObjectEntries = state;
         return pp;
      }
   }

   public DefaultPrettyPrinter withSeparators(Separators separators) {
      this._separators = separators;
      this._objectFieldValueSeparatorWithSpaces = " " + separators.getObjectFieldValueSeparator() + " ";
      return this;
   }

   public DefaultPrettyPrinter createInstance() {
      return new DefaultPrettyPrinter(this);
   }

   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
      if (this._rootSeparator != null) {
         g.writeRaw(this._rootSeparator);
      }

   }

   public void writeStartObject(JsonGenerator g) throws IOException {
      g.writeRaw('{');
      if (!this._objectIndenter.isInline()) {
         ++this._nesting;
      }

   }

   public void beforeObjectEntries(JsonGenerator g) throws IOException {
      this._objectIndenter.writeIndentation(g, this._nesting);
   }

   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
      if (this._spacesInObjectEntries) {
         g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
      } else {
         g.writeRaw(this._separators.getObjectFieldValueSeparator());
      }

   }

   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._separators.getObjectEntrySeparator());
      this._objectIndenter.writeIndentation(g, this._nesting);
   }

   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
      if (!this._objectIndenter.isInline()) {
         --this._nesting;
      }

      if (nrOfEntries > 0) {
         this._objectIndenter.writeIndentation(g, this._nesting);
      } else {
         g.writeRaw(' ');
      }

      g.writeRaw('}');
   }

   public void writeStartArray(JsonGenerator g) throws IOException {
      if (!this._arrayIndenter.isInline()) {
         ++this._nesting;
      }

      g.writeRaw('[');
   }

   public void beforeArrayValues(JsonGenerator g) throws IOException {
      this._arrayIndenter.writeIndentation(g, this._nesting);
   }

   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._separators.getArrayValueSeparator());
      this._arrayIndenter.writeIndentation(g, this._nesting);
   }

   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
      if (!this._arrayIndenter.isInline()) {
         --this._nesting;
      }

      if (nrOfValues > 0) {
         this._arrayIndenter.writeIndentation(g, this._nesting);
      } else {
         g.writeRaw(' ');
      }

      g.writeRaw(']');
   }

   public static class FixedSpaceIndenter extends DefaultPrettyPrinter.NopIndenter {
      public static final DefaultPrettyPrinter.FixedSpaceIndenter instance = new DefaultPrettyPrinter.FixedSpaceIndenter();

      public void writeIndentation(JsonGenerator g, int level) throws IOException {
         g.writeRaw(' ');
      }

      public boolean isInline() {
         return true;
      }
   }

   public static class NopIndenter implements DefaultPrettyPrinter.Indenter, Serializable {
      public static final DefaultPrettyPrinter.NopIndenter instance = new DefaultPrettyPrinter.NopIndenter();

      public void writeIndentation(JsonGenerator g, int level) throws IOException {
      }

      public boolean isInline() {
         return true;
      }
   }

   public interface Indenter {
      void writeIndentation(JsonGenerator var1, int var2) throws IOException;

      boolean isInline();
   }
}
