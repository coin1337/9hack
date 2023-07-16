package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonPointer;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RawValue;

public class ArrayNode extends ContainerNode<ArrayNode> {
   private final List<JsonNode> _children;

   public ArrayNode(JsonNodeFactory nf) {
      super(nf);
      this._children = new ArrayList();
   }

   public ArrayNode(JsonNodeFactory nf, int capacity) {
      super(nf);
      this._children = new ArrayList(capacity);
   }

   public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
      super(nf);
      this._children = children;
   }

   protected JsonNode _at(JsonPointer ptr) {
      return this.get(ptr.getMatchingIndex());
   }

   public ArrayNode deepCopy() {
      ArrayNode ret = new ArrayNode(this._nodeFactory);
      Iterator i$ = this._children.iterator();

      while(i$.hasNext()) {
         JsonNode element = (JsonNode)i$.next();
         ret._children.add(element.deepCopy());
      }

      return ret;
   }

   public boolean isEmpty(SerializerProvider serializers) {
      return this._children.isEmpty();
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.ARRAY;
   }

   public JsonToken asToken() {
      return JsonToken.START_ARRAY;
   }

   public int size() {
      return this._children.size();
   }

   public Iterator<JsonNode> elements() {
      return this._children.iterator();
   }

   public JsonNode get(int index) {
      return index >= 0 && index < this._children.size() ? (JsonNode)this._children.get(index) : null;
   }

   public JsonNode get(String fieldName) {
      return null;
   }

   public JsonNode path(String fieldName) {
      return MissingNode.getInstance();
   }

   public JsonNode path(int index) {
      return (JsonNode)(index >= 0 && index < this._children.size() ? (JsonNode)this._children.get(index) : MissingNode.getInstance());
   }

   public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
      if (!(o instanceof ArrayNode)) {
         return false;
      } else {
         ArrayNode other = (ArrayNode)o;
         int len = this._children.size();
         if (other.size() != len) {
            return false;
         } else {
            List<JsonNode> l1 = this._children;
            List<JsonNode> l2 = other._children;

            for(int i = 0; i < len; ++i) {
               if (!((JsonNode)l1.get(i)).equals(comparator, (JsonNode)l2.get(i))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public void serialize(JsonGenerator f, SerializerProvider provider) throws IOException {
      List<JsonNode> c = this._children;
      int size = c.size();
      f.writeStartArray(size);

      for(int i = 0; i < size; ++i) {
         JsonNode n = (JsonNode)c.get(i);
         ((BaseJsonNode)n).serialize(f, provider);
      }

      f.writeEndArray();
   }

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));
      Iterator i$ = this._children.iterator();

      while(i$.hasNext()) {
         JsonNode n = (JsonNode)i$.next();
         ((BaseJsonNode)n).serialize(g, provider);
      }

      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   public JsonNode findValue(String fieldName) {
      Iterator i$ = this._children.iterator();

      JsonNode value;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         JsonNode node = (JsonNode)i$.next();
         value = node.findValue(fieldName);
      } while(value == null);

      return value;
   }

   public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
      JsonNode node;
      for(Iterator i$ = this._children.iterator(); i$.hasNext(); foundSoFar = node.findValues(fieldName, foundSoFar)) {
         node = (JsonNode)i$.next();
      }

      return foundSoFar;
   }

   public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
      JsonNode node;
      for(Iterator i$ = this._children.iterator(); i$.hasNext(); foundSoFar = node.findValuesAsText(fieldName, foundSoFar)) {
         node = (JsonNode)i$.next();
      }

      return foundSoFar;
   }

   public ObjectNode findParent(String fieldName) {
      Iterator i$ = this._children.iterator();

      JsonNode parent;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         JsonNode node = (JsonNode)i$.next();
         parent = node.findParent(fieldName);
      } while(parent == null);

      return (ObjectNode)parent;
   }

   public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
      JsonNode node;
      for(Iterator i$ = this._children.iterator(); i$.hasNext(); foundSoFar = node.findParents(fieldName, foundSoFar)) {
         node = (JsonNode)i$.next();
      }

      return foundSoFar;
   }

   public JsonNode set(int index, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      if (index >= 0 && index < this._children.size()) {
         return (JsonNode)this._children.set(index, value);
      } else {
         throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
      }
   }

   public ArrayNode add(JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      this._add((JsonNode)value);
      return this;
   }

   public ArrayNode addAll(ArrayNode other) {
      this._children.addAll(other._children);
      return this;
   }

   public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
      this._children.addAll(nodes);
      return this;
   }

   public ArrayNode insert(int index, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      this._insert(index, (JsonNode)value);
      return this;
   }

   public JsonNode remove(int index) {
      return index >= 0 && index < this._children.size() ? (JsonNode)this._children.remove(index) : null;
   }

   public ArrayNode removeAll() {
      this._children.clear();
      return this;
   }

   public ArrayNode addArray() {
      ArrayNode n = this.arrayNode();
      this._add(n);
      return n;
   }

   public ObjectNode addObject() {
      ObjectNode n = this.objectNode();
      this._add(n);
      return n;
   }

   public ArrayNode addPOJO(Object value) {
      if (value == null) {
         this.addNull();
      } else {
         this._add(this.pojoNode(value));
      }

      return this;
   }

   public ArrayNode addRawValue(RawValue raw) {
      if (raw == null) {
         this.addNull();
      } else {
         this._add(this.rawValueNode(raw));
      }

      return this;
   }

   public ArrayNode addNull() {
      this._add(this.nullNode());
      return this;
   }

   public ArrayNode add(int v) {
      this._add(this.numberNode(v));
      return this;
   }

   public ArrayNode add(Integer value) {
      return value == null ? this.addNull() : this._add(this.numberNode(value));
   }

   public ArrayNode add(long v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Long value) {
      return value == null ? this.addNull() : this._add(this.numberNode(value));
   }

   public ArrayNode add(float v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Float value) {
      return value == null ? this.addNull() : this._add(this.numberNode(value));
   }

   public ArrayNode add(double v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Double value) {
      return value == null ? this.addNull() : this._add(this.numberNode(value));
   }

   public ArrayNode add(BigDecimal v) {
      return v == null ? this.addNull() : this._add(this.numberNode(v));
   }

   public ArrayNode add(BigInteger v) {
      return v == null ? this.addNull() : this._add(this.numberNode(v));
   }

   public ArrayNode add(String v) {
      return v == null ? this.addNull() : this._add(this.textNode(v));
   }

   public ArrayNode add(boolean v) {
      return this._add(this.booleanNode(v));
   }

   public ArrayNode add(Boolean value) {
      return value == null ? this.addNull() : this._add(this.booleanNode(value));
   }

   public ArrayNode add(byte[] v) {
      return v == null ? this.addNull() : this._add(this.binaryNode(v));
   }

   public ArrayNode insertArray(int index) {
      ArrayNode n = this.arrayNode();
      this._insert(index, n);
      return n;
   }

   public ObjectNode insertObject(int index) {
      ObjectNode n = this.objectNode();
      this._insert(index, n);
      return n;
   }

   public ArrayNode insertPOJO(int index, Object value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.pojoNode(value));
   }

   public ArrayNode insertNull(int index) {
      this._insert(index, this.nullNode());
      return this;
   }

   public ArrayNode insert(int index, int v) {
      this._insert(index, this.numberNode(v));
      return this;
   }

   public ArrayNode insert(int index, Integer value) {
      if (value == null) {
         this.insertNull(index);
      } else {
         this._insert(index, this.numberNode(value));
      }

      return this;
   }

   public ArrayNode insert(int index, long v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Long value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.numberNode(value));
   }

   public ArrayNode insert(int index, float v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Float value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.numberNode(value));
   }

   public ArrayNode insert(int index, double v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Double value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.numberNode(value));
   }

   public ArrayNode insert(int index, BigDecimal v) {
      return v == null ? this.insertNull(index) : this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, BigInteger v) {
      return v == null ? this.insertNull(index) : this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, String v) {
      return v == null ? this.insertNull(index) : this._insert(index, this.textNode(v));
   }

   public ArrayNode insert(int index, boolean v) {
      return this._insert(index, this.booleanNode(v));
   }

   public ArrayNode insert(int index, Boolean value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.booleanNode(value));
   }

   public ArrayNode insert(int index, byte[] v) {
      return v == null ? this.insertNull(index) : this._insert(index, this.binaryNode(v));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof ArrayNode ? this._children.equals(((ArrayNode)o)._children) : false;
      }
   }

   protected boolean _childrenEqual(ArrayNode other) {
      return this._children.equals(other._children);
   }

   public int hashCode() {
      return this._children.hashCode();
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(16 + (this.size() << 4));
      sb.append('[');
      int i = 0;

      for(int len = this._children.size(); i < len; ++i) {
         if (i > 0) {
            sb.append(',');
         }

         sb.append(((JsonNode)this._children.get(i)).toString());
      }

      sb.append(']');
      return sb.toString();
   }

   protected ArrayNode _add(JsonNode node) {
      this._children.add(node);
      return this;
   }

   protected ArrayNode _insert(int index, JsonNode node) {
      if (index < 0) {
         this._children.add(0, node);
      } else if (index >= this._children.size()) {
         this._children.add(node);
      } else {
         this._children.add(index, node);
      }

      return this;
   }
}
