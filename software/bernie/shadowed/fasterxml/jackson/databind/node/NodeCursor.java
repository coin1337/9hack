package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.util.Iterator;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;

abstract class NodeCursor extends JsonStreamContext {
   protected final NodeCursor _parent;
   protected String _currentName;
   protected Object _currentValue;

   public NodeCursor(int contextType, NodeCursor p) {
      this._type = contextType;
      this._index = -1;
      this._parent = p;
   }

   public final NodeCursor getParent() {
      return this._parent;
   }

   public final String getCurrentName() {
      return this._currentName;
   }

   public void overrideCurrentName(String name) {
      this._currentName = name;
   }

   public Object getCurrentValue() {
      return this._currentValue;
   }

   public void setCurrentValue(Object v) {
      this._currentValue = v;
   }

   public abstract JsonToken nextToken();

   public abstract JsonToken nextValue();

   public abstract JsonToken endToken();

   public abstract JsonNode currentNode();

   public abstract boolean currentHasChildren();

   public final NodeCursor iterateChildren() {
      JsonNode n = this.currentNode();
      if (n == null) {
         throw new IllegalStateException("No current node");
      } else if (n.isArray()) {
         return new NodeCursor.ArrayCursor(n, this);
      } else if (n.isObject()) {
         return new NodeCursor.ObjectCursor(n, this);
      } else {
         throw new IllegalStateException("Current node of type " + n.getClass().getName());
      }
   }

   protected static final class ObjectCursor extends NodeCursor {
      protected Iterator<Entry<String, JsonNode>> _contents;
      protected Entry<String, JsonNode> _current;
      protected boolean _needEntry;

      public ObjectCursor(JsonNode n, NodeCursor p) {
         super(2, p);
         this._contents = ((ObjectNode)n).fields();
         this._needEntry = true;
      }

      public JsonToken nextToken() {
         if (this._needEntry) {
            if (!this._contents.hasNext()) {
               this._currentName = null;
               this._current = null;
               return null;
            } else {
               this._needEntry = false;
               this._current = (Entry)this._contents.next();
               this._currentName = this._current == null ? null : (String)this._current.getKey();
               return JsonToken.FIELD_NAME;
            }
         } else {
            this._needEntry = true;
            return ((JsonNode)this._current.getValue()).asToken();
         }
      }

      public JsonToken nextValue() {
         JsonToken t = this.nextToken();
         if (t == JsonToken.FIELD_NAME) {
            t = this.nextToken();
         }

         return t;
      }

      public JsonToken endToken() {
         return JsonToken.END_OBJECT;
      }

      public JsonNode currentNode() {
         return this._current == null ? null : (JsonNode)this._current.getValue();
      }

      public boolean currentHasChildren() {
         return ((ContainerNode)this.currentNode()).size() > 0;
      }
   }

   protected static final class ArrayCursor extends NodeCursor {
      protected Iterator<JsonNode> _contents;
      protected JsonNode _currentNode;

      public ArrayCursor(JsonNode n, NodeCursor p) {
         super(1, p);
         this._contents = n.elements();
      }

      public JsonToken nextToken() {
         if (!this._contents.hasNext()) {
            this._currentNode = null;
            return null;
         } else {
            this._currentNode = (JsonNode)this._contents.next();
            return this._currentNode.asToken();
         }
      }

      public JsonToken nextValue() {
         return this.nextToken();
      }

      public JsonToken endToken() {
         return JsonToken.END_ARRAY;
      }

      public JsonNode currentNode() {
         return this._currentNode;
      }

      public boolean currentHasChildren() {
         return ((ContainerNode)this.currentNode()).size() > 0;
      }
   }

   protected static final class RootCursor extends NodeCursor {
      protected JsonNode _node;
      protected boolean _done = false;

      public RootCursor(JsonNode n, NodeCursor p) {
         super(0, p);
         this._node = n;
      }

      public void overrideCurrentName(String name) {
      }

      public JsonToken nextToken() {
         if (!this._done) {
            this._done = true;
            return this._node.asToken();
         } else {
            this._node = null;
            return null;
         }
      }

      public JsonToken nextValue() {
         return this.nextToken();
      }

      public JsonToken endToken() {
         return null;
      }

      public JsonNode currentNode() {
         return this._node;
      }

      public boolean currentHasChildren() {
         return false;
      }
   }
}
