package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;

public class UnresolvedForwardReference extends JsonMappingException {
   private static final long serialVersionUID = 1L;
   private ReadableObjectId _roid;
   private List<UnresolvedId> _unresolvedIds;

   public UnresolvedForwardReference(JsonParser p, String msg, JsonLocation loc, ReadableObjectId roid) {
      super((Closeable)p, (String)msg, (JsonLocation)loc);
      this._roid = roid;
   }

   public UnresolvedForwardReference(JsonParser p, String msg) {
      super((Closeable)p, (String)msg);
      this._unresolvedIds = new ArrayList();
   }

   /** @deprecated */
   @Deprecated
   public UnresolvedForwardReference(String msg, JsonLocation loc, ReadableObjectId roid) {
      super(msg, loc);
      this._roid = roid;
   }

   /** @deprecated */
   @Deprecated
   public UnresolvedForwardReference(String msg) {
      super(msg);
      this._unresolvedIds = new ArrayList();
   }

   public ReadableObjectId getRoid() {
      return this._roid;
   }

   public Object getUnresolvedId() {
      return this._roid.getKey().key;
   }

   public void addUnresolvedId(Object id, Class<?> type, JsonLocation where) {
      this._unresolvedIds.add(new UnresolvedId(id, type, where));
   }

   public List<UnresolvedId> getUnresolvedIds() {
      return this._unresolvedIds;
   }

   public String getMessage() {
      String msg = super.getMessage();
      if (this._unresolvedIds == null) {
         return msg;
      } else {
         StringBuilder sb = new StringBuilder(msg);
         Iterator iterator = this._unresolvedIds.iterator();

         while(iterator.hasNext()) {
            UnresolvedId unresolvedId = (UnresolvedId)iterator.next();
            sb.append(unresolvedId.toString());
            if (iterator.hasNext()) {
               sb.append(", ");
            }
         }

         sb.append('.');
         return sb.toString();
      }
   }
}
