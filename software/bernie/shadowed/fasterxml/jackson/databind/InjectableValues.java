package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public abstract class InjectableValues {
   public abstract Object findInjectableValue(Object var1, DeserializationContext var2, BeanProperty var3, Object var4) throws JsonMappingException;

   public static class Std extends InjectableValues implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final Map<String, Object> _values;

      public Std() {
         this(new HashMap());
      }

      public Std(Map<String, Object> values) {
         this._values = values;
      }

      public InjectableValues.Std addValue(String key, Object value) {
         this._values.put(key, value);
         return this;
      }

      public InjectableValues.Std addValue(Class<?> classKey, Object value) {
         this._values.put(classKey.getName(), value);
         return this;
      }

      public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
         if (!(valueId instanceof String)) {
            ctxt.reportBadDefinition(ClassUtil.classOf(valueId), String.format("Unrecognized inject value id type (%s), expecting String", ClassUtil.classNameOf(valueId)));
         }

         String key = (String)valueId;
         Object ob = this._values.get(key);
         if (ob == null && !this._values.containsKey(key)) {
            throw new IllegalArgumentException("No injectable id with value '" + key + "' found (for property '" + forProperty.getName() + "')");
         } else {
            return ob;
         }
      }
   }
}
