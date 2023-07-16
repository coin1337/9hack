package software.bernie.shadowed.fasterxml.jackson.databind.module;

import java.io.Serializable;
import java.util.HashMap;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiators;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;

public class SimpleValueInstantiators extends ValueInstantiators.Base implements Serializable {
   private static final long serialVersionUID = -8929386427526115130L;
   protected HashMap<ClassKey, ValueInstantiator> _classMappings = new HashMap();

   public SimpleValueInstantiators addValueInstantiator(Class<?> forType, ValueInstantiator inst) {
      this._classMappings.put(new ClassKey(forType), inst);
      return this;
   }

   public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
      ValueInstantiator inst = (ValueInstantiator)this._classMappings.get(new ClassKey(beanDesc.getBeanClass()));
      return inst == null ? defaultInstantiator : inst;
   }
}
