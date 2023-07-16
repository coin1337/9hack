package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.util.HashSet;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;

public class DupDetector {
   protected final Object _source;
   protected String _firstName;
   protected String _secondName;
   protected HashSet<String> _seen;

   private DupDetector(Object src) {
      this._source = src;
   }

   public static DupDetector rootDetector(JsonParser p) {
      return new DupDetector(p);
   }

   public static DupDetector rootDetector(JsonGenerator g) {
      return new DupDetector(g);
   }

   public DupDetector child() {
      return new DupDetector(this._source);
   }

   public void reset() {
      this._firstName = null;
      this._secondName = null;
      this._seen = null;
   }

   public JsonLocation findLocation() {
      return this._source instanceof JsonParser ? ((JsonParser)this._source).getCurrentLocation() : null;
   }

   public Object getSource() {
      return this._source;
   }

   public boolean isDup(String name) throws JsonParseException {
      if (this._firstName == null) {
         this._firstName = name;
         return false;
      } else if (name.equals(this._firstName)) {
         return true;
      } else if (this._secondName == null) {
         this._secondName = name;
         return false;
      } else if (name.equals(this._secondName)) {
         return true;
      } else {
         if (this._seen == null) {
            this._seen = new HashSet(16);
            this._seen.add(this._firstName);
            this._seen.add(this._secondName);
         }

         return !this._seen.add(name);
      }
   }
}
