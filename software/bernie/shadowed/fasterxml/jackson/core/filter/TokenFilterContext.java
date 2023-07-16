package software.bernie.shadowed.fasterxml.jackson.core.filter;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;

public class TokenFilterContext extends JsonStreamContext {
   protected final TokenFilterContext _parent;
   protected TokenFilterContext _child;
   protected String _currentName;
   protected TokenFilter _filter;
   protected boolean _startHandled;
   protected boolean _needToHandleName;

   protected TokenFilterContext(int type, TokenFilterContext parent, TokenFilter filter, boolean startHandled) {
      this._type = type;
      this._parent = parent;
      this._filter = filter;
      this._index = -1;
      this._startHandled = startHandled;
      this._needToHandleName = false;
   }

   protected TokenFilterContext reset(int type, TokenFilter filter, boolean startWritten) {
      this._type = type;
      this._filter = filter;
      this._index = -1;
      this._currentName = null;
      this._startHandled = startWritten;
      this._needToHandleName = false;
      return this;
   }

   public static TokenFilterContext createRootContext(TokenFilter filter) {
      return new TokenFilterContext(0, (TokenFilterContext)null, filter, true);
   }

   public TokenFilterContext createChildArrayContext(TokenFilter filter, boolean writeStart) {
      TokenFilterContext ctxt = this._child;
      if (ctxt == null) {
         this._child = ctxt = new TokenFilterContext(1, this, filter, writeStart);
         return ctxt;
      } else {
         return ctxt.reset(1, filter, writeStart);
      }
   }

   public TokenFilterContext createChildObjectContext(TokenFilter filter, boolean writeStart) {
      TokenFilterContext ctxt = this._child;
      if (ctxt == null) {
         this._child = ctxt = new TokenFilterContext(2, this, filter, writeStart);
         return ctxt;
      } else {
         return ctxt.reset(2, filter, writeStart);
      }
   }

   public TokenFilter setFieldName(String name) throws JsonProcessingException {
      this._currentName = name;
      this._needToHandleName = true;
      return this._filter;
   }

   public TokenFilter checkValue(TokenFilter filter) {
      if (this._type == 2) {
         return filter;
      } else {
         int ix = ++this._index;
         return this._type == 1 ? filter.includeElement(ix) : filter.includeRootValue(ix);
      }
   }

   public void writePath(JsonGenerator gen) throws IOException {
      if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
         if (this._parent != null) {
            this._parent._writePath(gen);
         }

         if (this._startHandled) {
            if (this._needToHandleName) {
               gen.writeFieldName(this._currentName);
            }
         } else {
            this._startHandled = true;
            if (this._type == 2) {
               gen.writeStartObject();
               gen.writeFieldName(this._currentName);
            } else if (this._type == 1) {
               gen.writeStartArray();
            }
         }

      }
   }

   public void writeImmediatePath(JsonGenerator gen) throws IOException {
      if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
         if (this._startHandled) {
            if (this._needToHandleName) {
               gen.writeFieldName(this._currentName);
            }
         } else {
            this._startHandled = true;
            if (this._type == 2) {
               gen.writeStartObject();
               if (this._needToHandleName) {
                  gen.writeFieldName(this._currentName);
               }
            } else if (this._type == 1) {
               gen.writeStartArray();
            }
         }

      }
   }

   private void _writePath(JsonGenerator gen) throws IOException {
      if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
         if (this._parent != null) {
            this._parent._writePath(gen);
         }

         if (this._startHandled) {
            if (this._needToHandleName) {
               this._needToHandleName = false;
               gen.writeFieldName(this._currentName);
            }
         } else {
            this._startHandled = true;
            if (this._type == 2) {
               gen.writeStartObject();
               if (this._needToHandleName) {
                  this._needToHandleName = false;
                  gen.writeFieldName(this._currentName);
               }
            } else if (this._type == 1) {
               gen.writeStartArray();
            }
         }

      }
   }

   public TokenFilterContext closeArray(JsonGenerator gen) throws IOException {
      if (this._startHandled) {
         gen.writeEndArray();
      }

      if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
         this._filter.filterFinishArray();
      }

      return this._parent;
   }

   public TokenFilterContext closeObject(JsonGenerator gen) throws IOException {
      if (this._startHandled) {
         gen.writeEndObject();
      }

      if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
         this._filter.filterFinishObject();
      }

      return this._parent;
   }

   public void skipParentChecks() {
      this._filter = null;

      for(TokenFilterContext ctxt = this._parent; ctxt != null; ctxt = ctxt._parent) {
         this._parent._filter = null;
      }

   }

   public Object getCurrentValue() {
      return null;
   }

   public void setCurrentValue(Object v) {
   }

   public final TokenFilterContext getParent() {
      return this._parent;
   }

   public final String getCurrentName() {
      return this._currentName;
   }

   public boolean hasCurrentName() {
      return this._currentName != null;
   }

   public TokenFilter getFilter() {
      return this._filter;
   }

   public boolean isStartHandled() {
      return this._startHandled;
   }

   public JsonToken nextTokenToRead() {
      if (!this._startHandled) {
         this._startHandled = true;
         return this._type == 2 ? JsonToken.START_OBJECT : JsonToken.START_ARRAY;
      } else if (this._needToHandleName && this._type == 2) {
         this._needToHandleName = false;
         return JsonToken.FIELD_NAME;
      } else {
         return null;
      }
   }

   public TokenFilterContext findChildOf(TokenFilterContext parent) {
      if (this._parent == parent) {
         return this;
      } else {
         TokenFilterContext p;
         for(TokenFilterContext curr = this._parent; curr != null; curr = p) {
            p = curr._parent;
            if (p == parent) {
               return curr;
            }
         }

         return null;
      }
   }

   protected void appendDesc(StringBuilder sb) {
      if (this._parent != null) {
         this._parent.appendDesc(sb);
      }

      if (this._type == 2) {
         sb.append('{');
         if (this._currentName != null) {
            sb.append('"');
            sb.append(this._currentName);
            sb.append('"');
         } else {
            sb.append('?');
         }

         sb.append('}');
      } else if (this._type == 1) {
         sb.append('[');
         sb.append(this.getCurrentIndex());
         sb.append(']');
      } else {
         sb.append("/");
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder(64);
      this.appendDesc(sb);
      return sb.toString();
   }
}
