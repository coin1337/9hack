package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerators;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator {
   private static final long serialVersionUID = 1L;
   protected final BeanPropertyWriter _property;

   public PropertyBasedObjectIdGenerator(ObjectIdInfo oid, BeanPropertyWriter prop) {
      this(oid.getScope(), prop);
   }

   protected PropertyBasedObjectIdGenerator(Class<?> scope, BeanPropertyWriter prop) {
      super(scope);
      this._property = prop;
   }

   public boolean canUseFor(ObjectIdGenerator<?> gen) {
      if (gen.getClass() == this.getClass()) {
         PropertyBasedObjectIdGenerator other = (PropertyBasedObjectIdGenerator)gen;
         if (other.getScope() == this._scope) {
            return other._property == this._property;
         }
      }

      return false;
   }

   public Object generateId(Object forPojo) {
      try {
         return this._property.get(forPojo);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException("Problem accessing property '" + this._property.getName() + "': " + var4.getMessage(), var4);
      }
   }

   public ObjectIdGenerator<Object> forScope(Class<?> scope) {
      return scope == this._scope ? this : new PropertyBasedObjectIdGenerator(scope, this._property);
   }

   public ObjectIdGenerator<Object> newForSerialization(Object context) {
      return this;
   }

   public ObjectIdGenerator.IdKey key(Object key) {
      return key == null ? null : new ObjectIdGenerator.IdKey(this.getClass(), this._scope, key);
   }
}
