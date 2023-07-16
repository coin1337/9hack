package software.bernie.shadowed.fasterxml.jackson.core.json;

import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;

public class JsonWriteContext extends JsonStreamContext {
   public static final int STATUS_OK_AS_IS = 0;
   public static final int STATUS_OK_AFTER_COMMA = 1;
   public static final int STATUS_OK_AFTER_COLON = 2;
   public static final int STATUS_OK_AFTER_SPACE = 3;
   public static final int STATUS_EXPECT_VALUE = 4;
   public static final int STATUS_EXPECT_NAME = 5;
   protected final JsonWriteContext _parent;
   protected DupDetector _dups;
   protected JsonWriteContext _child;
   protected String _currentName;
   protected Object _currentValue;
   protected boolean _gotName;

   protected JsonWriteContext(int type, JsonWriteContext parent, DupDetector dups) {
      this._type = type;
      this._parent = parent;
      this._dups = dups;
      this._index = -1;
   }

   protected JsonWriteContext reset(int type) {
      this._type = type;
      this._index = -1;
      this._currentName = null;
      this._gotName = false;
      this._currentValue = null;
      if (this._dups != null) {
         this._dups.reset();
      }

      return this;
   }

   public JsonWriteContext withDupDetector(DupDetector dups) {
      this._dups = dups;
      return this;
   }

   public Object getCurrentValue() {
      return this._currentValue;
   }

   public void setCurrentValue(Object v) {
      this._currentValue = v;
   }

   /** @deprecated */
   @Deprecated
   public static JsonWriteContext createRootContext() {
      return createRootContext((DupDetector)null);
   }

   public static JsonWriteContext createRootContext(DupDetector dd) {
      return new JsonWriteContext(0, (JsonWriteContext)null, dd);
   }

   public JsonWriteContext createChildArrayContext() {
      JsonWriteContext ctxt = this._child;
      if (ctxt == null) {
         this._child = ctxt = new JsonWriteContext(1, this, this._dups == null ? null : this._dups.child());
         return ctxt;
      } else {
         return ctxt.reset(1);
      }
   }

   public JsonWriteContext createChildObjectContext() {
      JsonWriteContext ctxt = this._child;
      if (ctxt == null) {
         this._child = ctxt = new JsonWriteContext(2, this, this._dups == null ? null : this._dups.child());
         return ctxt;
      } else {
         return ctxt.reset(2);
      }
   }

   public final JsonWriteContext getParent() {
      return this._parent;
   }

   public final String getCurrentName() {
      return this._currentName;
   }

   public boolean hasCurrentName() {
      return this._currentName != null;
   }

   public JsonWriteContext clearAndGetParent() {
      this._currentValue = null;
      return this._parent;
   }

   public DupDetector getDupDetector() {
      return this._dups;
   }

   public int writeFieldName(String name) throws JsonProcessingException {
      if (this._type == 2 && !this._gotName) {
         this._gotName = true;
         this._currentName = name;
         if (this._dups != null) {
            this._checkDup(this._dups, name);
         }

         return this._index < 0 ? 0 : 1;
      } else {
         return 4;
      }
   }

   private final void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
      if (dd.isDup(name)) {
         Object src = dd.getSource();
         throw new JsonGenerationException("Duplicate field '" + name + "'", src instanceof JsonGenerator ? (JsonGenerator)src : null);
      }
   }

   public int writeValue() {
      if (this._type == 2) {
         if (!this._gotName) {
            return 5;
         } else {
            this._gotName = false;
            ++this._index;
            return 2;
         }
      } else if (this._type == 1) {
         int ix = this._index++;
         return ix < 0 ? 0 : 1;
      } else {
         ++this._index;
         return this._index == 0 ? 0 : 3;
      }
   }
}
