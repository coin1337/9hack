package software.bernie.shadowed.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

public class SimpleObjectIdResolver implements ObjectIdResolver {
   protected Map<ObjectIdGenerator.IdKey, Object> _items;

   public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
      if (this._items == null) {
         this._items = new HashMap();
      } else if (this._items.containsKey(id)) {
         throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
      }

      this._items.put(id, ob);
   }

   public Object resolveId(ObjectIdGenerator.IdKey id) {
      return this._items == null ? null : this._items.get(id);
   }

   public boolean canUseFor(ObjectIdResolver resolverType) {
      return resolverType.getClass() == this.getClass();
   }

   public ObjectIdResolver newForDeserialization(Object context) {
      return new SimpleObjectIdResolver();
   }
}
