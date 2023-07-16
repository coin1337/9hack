package software.bernie.shadowed.fasterxml.jackson.databind.util;

import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.json.JsonReadContext;

public class TokenBufferReadContext extends JsonStreamContext {
   protected final JsonStreamContext _parent;
   protected final JsonLocation _startLocation;
   protected String _currentName;
   protected Object _currentValue;

   protected TokenBufferReadContext(JsonStreamContext base, Object srcRef) {
      super(base);
      this._parent = base.getParent();
      this._currentName = base.getCurrentName();
      this._currentValue = base.getCurrentValue();
      if (base instanceof JsonReadContext) {
         JsonReadContext rc = (JsonReadContext)base;
         this._startLocation = rc.getStartLocation(srcRef);
      } else {
         this._startLocation = JsonLocation.NA;
      }

   }

   protected TokenBufferReadContext(JsonStreamContext base, JsonLocation startLoc) {
      super(base);
      this._parent = base.getParent();
      this._currentName = base.getCurrentName();
      this._currentValue = base.getCurrentValue();
      this._startLocation = startLoc;
   }

   protected TokenBufferReadContext() {
      super(0, -1);
      this._parent = null;
      this._startLocation = JsonLocation.NA;
   }

   protected TokenBufferReadContext(TokenBufferReadContext parent, int type, int index) {
      super(type, index);
      this._parent = parent;
      this._startLocation = parent._startLocation;
   }

   public Object getCurrentValue() {
      return this._currentValue;
   }

   public void setCurrentValue(Object v) {
      this._currentValue = v;
   }

   public static TokenBufferReadContext createRootContext(JsonStreamContext origContext) {
      return origContext == null ? new TokenBufferReadContext() : new TokenBufferReadContext(origContext, (JsonLocation)null);
   }

   public TokenBufferReadContext createChildArrayContext() {
      return new TokenBufferReadContext(this, 1, -1);
   }

   public TokenBufferReadContext createChildObjectContext() {
      return new TokenBufferReadContext(this, 2, -1);
   }

   public TokenBufferReadContext parentOrCopy() {
      if (this._parent instanceof TokenBufferReadContext) {
         return (TokenBufferReadContext)this._parent;
      } else {
         return this._parent == null ? new TokenBufferReadContext() : new TokenBufferReadContext(this._parent, this._startLocation);
      }
   }

   public String getCurrentName() {
      return this._currentName;
   }

   public boolean hasCurrentName() {
      return this._currentName != null;
   }

   public JsonStreamContext getParent() {
      return this._parent;
   }

   public void setCurrentName(String name) throws JsonProcessingException {
      this._currentName = name;
   }
}
