package software.bernie.shadowed.fasterxml.jackson.databind.cfg;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ContextAttributes {
   public static ContextAttributes getEmpty() {
      return ContextAttributes.Impl.getEmpty();
   }

   public abstract ContextAttributes withSharedAttribute(Object var1, Object var2);

   public abstract ContextAttributes withSharedAttributes(Map<?, ?> var1);

   public abstract ContextAttributes withoutSharedAttribute(Object var1);

   public abstract Object getAttribute(Object var1);

   public abstract ContextAttributes withPerCallAttribute(Object var1, Object var2);

   public static class Impl extends ContextAttributes implements Serializable {
      private static final long serialVersionUID = 1L;
      protected static final ContextAttributes.Impl EMPTY = new ContextAttributes.Impl(Collections.emptyMap());
      protected static final Object NULL_SURROGATE = new Object();
      protected final Map<?, ?> _shared;
      protected transient Map<Object, Object> _nonShared;

      protected Impl(Map<?, ?> shared) {
         this._shared = shared;
         this._nonShared = null;
      }

      protected Impl(Map<?, ?> shared, Map<Object, Object> nonShared) {
         this._shared = shared;
         this._nonShared = nonShared;
      }

      public static ContextAttributes getEmpty() {
         return EMPTY;
      }

      public ContextAttributes withSharedAttribute(Object key, Object value) {
         Object m;
         if (this == EMPTY) {
            m = new HashMap(8);
         } else {
            m = this._copy(this._shared);
         }

         ((Map)m).put(key, value);
         return new ContextAttributes.Impl((Map)m);
      }

      public ContextAttributes withSharedAttributes(Map<?, ?> shared) {
         return new ContextAttributes.Impl(shared);
      }

      public ContextAttributes withoutSharedAttribute(Object key) {
         if (this._shared.isEmpty()) {
            return this;
         } else if (this._shared.containsKey(key)) {
            if (this._shared.size() == 1) {
               return EMPTY;
            } else {
               Map<Object, Object> m = this._copy(this._shared);
               m.remove(key);
               return new ContextAttributes.Impl(m);
            }
         } else {
            return this;
         }
      }

      public Object getAttribute(Object key) {
         if (this._nonShared != null) {
            Object ob = this._nonShared.get(key);
            if (ob != null) {
               if (ob == NULL_SURROGATE) {
                  return null;
               }

               return ob;
            }
         }

         return this._shared.get(key);
      }

      public ContextAttributes withPerCallAttribute(Object key, Object value) {
         if (value == null) {
            if (!this._shared.containsKey(key)) {
               if (this._nonShared != null && this._nonShared.containsKey(key)) {
                  this._nonShared.remove(key);
                  return this;
               }

               return this;
            }

            value = NULL_SURROGATE;
         }

         if (this._nonShared == null) {
            return this.nonSharedInstance(key, value);
         } else {
            this._nonShared.put(key, value);
            return this;
         }
      }

      protected ContextAttributes nonSharedInstance(Object key, Object value) {
         Map<Object, Object> m = new HashMap();
         if (value == null) {
            value = NULL_SURROGATE;
         }

         m.put(key, value);
         return new ContextAttributes.Impl(this._shared, m);
      }

      private Map<Object, Object> _copy(Map<?, ?> src) {
         return new HashMap(src);
      }
   }
}
