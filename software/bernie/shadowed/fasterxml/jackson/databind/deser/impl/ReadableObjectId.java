package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdResolver;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.UnresolvedForwardReference;

public class ReadableObjectId {
   protected Object _item;
   protected final ObjectIdGenerator.IdKey _key;
   protected LinkedList<ReadableObjectId.Referring> _referringProperties;
   protected ObjectIdResolver _resolver;

   public ReadableObjectId(ObjectIdGenerator.IdKey key) {
      this._key = key;
   }

   public void setResolver(ObjectIdResolver resolver) {
      this._resolver = resolver;
   }

   public ObjectIdGenerator.IdKey getKey() {
      return this._key;
   }

   public void appendReferring(ReadableObjectId.Referring currentReferring) {
      if (this._referringProperties == null) {
         this._referringProperties = new LinkedList();
      }

      this._referringProperties.add(currentReferring);
   }

   public void bindItem(Object ob) throws IOException {
      this._resolver.bindItem(this._key, ob);
      this._item = ob;
      Object id = this._key.key;
      if (this._referringProperties != null) {
         Iterator<ReadableObjectId.Referring> it = this._referringProperties.iterator();
         this._referringProperties = null;

         while(it.hasNext()) {
            ((ReadableObjectId.Referring)it.next()).handleResolvedForwardReference(id, ob);
         }
      }

   }

   public Object resolve() {
      return this._item = this._resolver.resolveId(this._key);
   }

   public boolean hasReferringProperties() {
      return this._referringProperties != null && !this._referringProperties.isEmpty();
   }

   public Iterator<ReadableObjectId.Referring> referringProperties() {
      return this._referringProperties == null ? Collections.emptyList().iterator() : this._referringProperties.iterator();
   }

   public boolean tryToResolveUnresolved(DeserializationContext ctxt) {
      return false;
   }

   public ObjectIdResolver getResolver() {
      return this._resolver;
   }

   public String toString() {
      return String.valueOf(this._key);
   }

   public abstract static class Referring {
      private final UnresolvedForwardReference _reference;
      private final Class<?> _beanType;

      public Referring(UnresolvedForwardReference ref, Class<?> beanType) {
         this._reference = ref;
         this._beanType = beanType;
      }

      public Referring(UnresolvedForwardReference ref, JavaType beanType) {
         this._reference = ref;
         this._beanType = beanType.getRawClass();
      }

      public JsonLocation getLocation() {
         return this._reference.getLocation();
      }

      public Class<?> getBeanType() {
         return this._beanType;
      }

      public abstract void handleResolvedForwardReference(Object var1, Object var2) throws IOException;

      public boolean hasId(Object id) {
         return id.equals(this._reference.getUnresolvedId());
      }
   }
}
