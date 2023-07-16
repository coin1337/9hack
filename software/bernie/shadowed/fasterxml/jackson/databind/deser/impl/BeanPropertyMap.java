package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class BeanPropertyMap implements Iterable<SettableBeanProperty>, Serializable {
   private static final long serialVersionUID = 2L;
   protected final boolean _caseInsensitive;
   private int _hashMask;
   private int _size;
   private int _spillCount;
   private Object[] _hashArea;
   private SettableBeanProperty[] _propsInOrder;
   private final Map<String, List<PropertyName>> _aliasDefs;
   private final Map<String, String> _aliasMapping;

   public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs) {
      this._caseInsensitive = caseInsensitive;
      this._propsInOrder = (SettableBeanProperty[])props.toArray(new SettableBeanProperty[props.size()]);
      this._aliasDefs = aliasDefs;
      this._aliasMapping = this._buildAliasMapping(aliasDefs);
      this.init(props);
   }

   /** @deprecated */
   @Deprecated
   public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props) {
      this(caseInsensitive, props, Collections.emptyMap());
   }

   protected BeanPropertyMap(BeanPropertyMap base, boolean caseInsensitive) {
      this._caseInsensitive = caseInsensitive;
      this._aliasDefs = base._aliasDefs;
      this._aliasMapping = base._aliasMapping;
      this._propsInOrder = (SettableBeanProperty[])Arrays.copyOf(base._propsInOrder, base._propsInOrder.length);
      this.init(Arrays.asList(this._propsInOrder));
   }

   public BeanPropertyMap withCaseInsensitivity(boolean state) {
      return this._caseInsensitive == state ? this : new BeanPropertyMap(this, state);
   }

   protected void init(Collection<SettableBeanProperty> props) {
      this._size = props.size();
      int hashSize = findSize(this._size);
      this._hashMask = hashSize - 1;
      int alloc = (hashSize + (hashSize >> 1)) * 2;
      Object[] hashed = new Object[alloc];
      int spillCount = 0;
      Iterator i$ = props.iterator();

      while(i$.hasNext()) {
         SettableBeanProperty prop = (SettableBeanProperty)i$.next();
         if (prop != null) {
            String key = this.getPropertyName(prop);
            int slot = this._hashCode(key);
            int ix = slot << 1;
            if (hashed[ix] != null) {
               ix = hashSize + (slot >> 1) << 1;
               if (hashed[ix] != null) {
                  ix = (hashSize + (hashSize >> 1) << 1) + spillCount;
                  spillCount += 2;
                  if (ix >= hashed.length) {
                     hashed = Arrays.copyOf(hashed, hashed.length + 4);
                  }
               }
            }

            hashed[ix] = key;
            hashed[ix + 1] = prop;
         }
      }

      this._hashArea = hashed;
      this._spillCount = spillCount;
   }

   private static final int findSize(int size) {
      if (size <= 5) {
         return 8;
      } else if (size <= 12) {
         return 16;
      } else {
         int needed = size + (size >> 2);

         int result;
         for(result = 32; result < needed; result += result) {
         }

         return result;
      }
   }

   public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive, Map<String, List<PropertyName>> aliasMapping) {
      return new BeanPropertyMap(caseInsensitive, props, aliasMapping);
   }

   /** @deprecated */
   @Deprecated
   public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive) {
      return construct(props, caseInsensitive, Collections.emptyMap());
   }

   public BeanPropertyMap withProperty(SettableBeanProperty newProp) {
      String key = this.getPropertyName(newProp);
      int slot = 1;

      int hashSize;
      for(hashSize = this._hashArea.length; slot < hashSize; slot += 2) {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[slot];
         if (prop != null && prop.getName().equals(key)) {
            this._hashArea[slot] = newProp;
            this._propsInOrder[this._findFromOrdered(prop)] = newProp;
            return this;
         }
      }

      slot = this._hashCode(key);
      hashSize = this._hashMask + 1;
      int ix = slot << 1;
      if (this._hashArea[ix] != null) {
         ix = hashSize + (slot >> 1) << 1;
         if (this._hashArea[ix] != null) {
            ix = (hashSize + (hashSize >> 1) << 1) + this._spillCount;
            this._spillCount += 2;
            if (ix >= this._hashArea.length) {
               this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + 4);
            }
         }
      }

      this._hashArea[ix] = key;
      this._hashArea[ix + 1] = newProp;
      int last = this._propsInOrder.length;
      this._propsInOrder = (SettableBeanProperty[])Arrays.copyOf(this._propsInOrder, last + 1);
      this._propsInOrder[last] = newProp;
      return this;
   }

   public BeanPropertyMap assignIndexes() {
      int index = 0;
      int i = 1;

      for(int end = this._hashArea.length; i < end; i += 2) {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
         if (prop != null) {
            prop.assignIndex(index++);
         }
      }

      return this;
   }

   public BeanPropertyMap renameAll(NameTransformer transformer) {
      if (transformer != null && transformer != NameTransformer.NOP) {
         int len = this._propsInOrder.length;
         ArrayList<SettableBeanProperty> newProps = new ArrayList(len);

         for(int i = 0; i < len; ++i) {
            SettableBeanProperty prop = this._propsInOrder[i];
            if (prop == null) {
               newProps.add(prop);
            } else {
               newProps.add(this._rename(prop, transformer));
            }
         }

         return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs);
      } else {
         return this;
      }
   }

   public BeanPropertyMap withoutProperties(Collection<String> toExclude) {
      if (toExclude.isEmpty()) {
         return this;
      } else {
         int len = this._propsInOrder.length;
         ArrayList<SettableBeanProperty> newProps = new ArrayList(len);

         for(int i = 0; i < len; ++i) {
            SettableBeanProperty prop = this._propsInOrder[i];
            if (prop != null && !toExclude.contains(prop.getName())) {
               newProps.add(prop);
            }
         }

         return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs);
      }
   }

   public void replace(SettableBeanProperty newProp) {
      String key = this.getPropertyName(newProp);
      int ix = this._findIndexInHash(key);
      if (ix < 0) {
         throw new NoSuchElementException("No entry '" + key + "' found, can't replace");
      } else {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[ix];
         this._hashArea[ix] = newProp;
         this._propsInOrder[this._findFromOrdered(prop)] = newProp;
      }
   }

   public void remove(SettableBeanProperty propToRm) {
      ArrayList<SettableBeanProperty> props = new ArrayList(this._size);
      String key = this.getPropertyName(propToRm);
      boolean found = false;
      int i = 1;

      for(int end = this._hashArea.length; i < end; i += 2) {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
         if (prop != null) {
            if (!found) {
               found = key.equals(this._hashArea[i - 1]);
               if (found) {
                  this._propsInOrder[this._findFromOrdered(prop)] = null;
                  continue;
               }
            }

            props.add(prop);
         }
      }

      if (!found) {
         throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
      } else {
         this.init(props);
      }
   }

   public int size() {
      return this._size;
   }

   public boolean isCaseInsensitive() {
      return this._caseInsensitive;
   }

   public boolean hasAliases() {
      return !this._aliasDefs.isEmpty();
   }

   public Iterator<SettableBeanProperty> iterator() {
      return this._properties().iterator();
   }

   private List<SettableBeanProperty> _properties() {
      ArrayList<SettableBeanProperty> p = new ArrayList(this._size);
      int i = 1;

      for(int end = this._hashArea.length; i < end; i += 2) {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
         if (prop != null) {
            p.add(prop);
         }
      }

      return p;
   }

   public SettableBeanProperty[] getPropertiesInInsertionOrder() {
      return this._propsInOrder;
   }

   protected final String getPropertyName(SettableBeanProperty prop) {
      return this._caseInsensitive ? prop.getName().toLowerCase() : prop.getName();
   }

   public SettableBeanProperty find(int index) {
      int i = 1;

      for(int end = this._hashArea.length; i < end; i += 2) {
         SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
         if (prop != null && index == prop.getPropertyIndex()) {
            return prop;
         }
      }

      return null;
   }

   public SettableBeanProperty find(String key) {
      if (key == null) {
         throw new IllegalArgumentException("Cannot pass null property name");
      } else {
         if (this._caseInsensitive) {
            key = key.toLowerCase();
         }

         int slot = key.hashCode() & this._hashMask;
         int ix = slot << 1;
         Object match = this._hashArea[ix];
         return match != key && !key.equals(match) ? this._find2(key, slot, match) : (SettableBeanProperty)this._hashArea[ix + 1];
      }
   }

   private final SettableBeanProperty _find2(String key, int slot, Object match) {
      if (match == null) {
         return this._findWithAlias((String)this._aliasMapping.get(key));
      } else {
         int hashSize = this._hashMask + 1;
         int ix = hashSize + (slot >> 1) << 1;
         match = this._hashArea[ix];
         if (key.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
         } else {
            if (match != null) {
               int i = hashSize + (hashSize >> 1) << 1;

               for(int end = i + this._spillCount; i < end; i += 2) {
                  match = this._hashArea[i];
                  if (match == key || key.equals(match)) {
                     return (SettableBeanProperty)this._hashArea[i + 1];
                  }
               }
            }

            return this._findWithAlias((String)this._aliasMapping.get(key));
         }
      }
   }

   private SettableBeanProperty _findWithAlias(String keyFromAlias) {
      if (keyFromAlias == null) {
         return null;
      } else {
         int slot = this._hashCode(keyFromAlias);
         int ix = slot << 1;
         Object match = this._hashArea[ix];
         if (keyFromAlias.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
         } else {
            return match == null ? null : this._find2ViaAlias(keyFromAlias, slot, match);
         }
      }
   }

   private SettableBeanProperty _find2ViaAlias(String key, int slot, Object match) {
      int hashSize = this._hashMask + 1;
      int ix = hashSize + (slot >> 1) << 1;
      match = this._hashArea[ix];
      if (key.equals(match)) {
         return (SettableBeanProperty)this._hashArea[ix + 1];
      } else {
         if (match != null) {
            int i = hashSize + (hashSize >> 1) << 1;

            for(int end = i + this._spillCount; i < end; i += 2) {
               match = this._hashArea[i];
               if (match == key || key.equals(match)) {
                  return (SettableBeanProperty)this._hashArea[i + 1];
               }
            }
         }

         return null;
      }
   }

   public boolean findDeserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, String key) throws IOException {
      SettableBeanProperty prop = this.find(key);
      if (prop == null) {
         return false;
      } else {
         try {
            prop.deserializeAndSet(p, ctxt, bean);
         } catch (Exception var7) {
            this.wrapAndThrow(var7, bean, key, ctxt);
         }

         return true;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Properties=[");
      int count = 0;
      Iterator it = this.iterator();

      while(it.hasNext()) {
         SettableBeanProperty prop = (SettableBeanProperty)it.next();
         if (count++ > 0) {
            sb.append(", ");
         }

         sb.append(prop.getName());
         sb.append('(');
         sb.append(prop.getType());
         sb.append(')');
      }

      sb.append(']');
      if (!this._aliasDefs.isEmpty()) {
         sb.append("(aliases: ");
         sb.append(this._aliasDefs);
         sb.append(")");
      }

      return sb.toString();
   }

   protected SettableBeanProperty _rename(SettableBeanProperty prop, NameTransformer xf) {
      if (prop == null) {
         return prop;
      } else {
         String newName = xf.transform(prop.getName());
         prop = prop.withSimpleName(newName);
         JsonDeserializer<?> deser = prop.getValueDeserializer();
         if (deser != null) {
            JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(xf);
            if (newDeser != deser) {
               prop = prop.withValueDeserializer(newDeser);
            }
         }

         return prop;
      }
   }

   protected void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
      if (t instanceof IOException) {
         if (!wrap || !(t instanceof JsonProcessingException)) {
            throw (IOException)t;
         }
      } else if (!wrap) {
         ClassUtil.throwIfRTE(t);
      }

      throw JsonMappingException.wrapWithPath(t, bean, fieldName);
   }

   private final int _findIndexInHash(String key) {
      int slot = this._hashCode(key);
      int ix = slot << 1;
      if (key.equals(this._hashArea[ix])) {
         return ix + 1;
      } else {
         int hashSize = this._hashMask + 1;
         ix = hashSize + (slot >> 1) << 1;
         if (key.equals(this._hashArea[ix])) {
            return ix + 1;
         } else {
            int i = hashSize + (hashSize >> 1) << 1;

            for(int end = i + this._spillCount; i < end; i += 2) {
               if (key.equals(this._hashArea[i])) {
                  return i + 1;
               }
            }

            return -1;
         }
      }
   }

   private final int _findFromOrdered(SettableBeanProperty prop) {
      int i = 0;

      for(int end = this._propsInOrder.length; i < end; ++i) {
         if (this._propsInOrder[i] == prop) {
            return i;
         }
      }

      throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
   }

   private final int _hashCode(String key) {
      return key.hashCode() & this._hashMask;
   }

   private Map<String, String> _buildAliasMapping(Map<String, List<PropertyName>> defs) {
      if (defs != null && !defs.isEmpty()) {
         Map<String, String> aliases = new HashMap();
         Iterator i$ = defs.entrySet().iterator();

         while(i$.hasNext()) {
            Entry<String, List<PropertyName>> entry = (Entry)i$.next();
            String key = (String)entry.getKey();
            if (this._caseInsensitive) {
               key = key.toLowerCase();
            }

            String mapped;
            for(Iterator i$ = ((List)entry.getValue()).iterator(); i$.hasNext(); aliases.put(mapped, key)) {
               PropertyName pn = (PropertyName)i$.next();
               mapped = pn.getSimpleName();
               if (this._caseInsensitive) {
                  mapped = mapped.toLowerCase();
               }
            }
         }

         return aliases;
      } else {
         return Collections.emptyMap();
      }
   }
}
