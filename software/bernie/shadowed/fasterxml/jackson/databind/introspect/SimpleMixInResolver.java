package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;

public class SimpleMixInResolver implements ClassIntrospector.MixInResolver, Serializable {
   private static final long serialVersionUID = 1L;
   protected final ClassIntrospector.MixInResolver _overrides;
   protected Map<ClassKey, Class<?>> _localMixIns;

   public SimpleMixInResolver(ClassIntrospector.MixInResolver overrides) {
      this._overrides = overrides;
   }

   protected SimpleMixInResolver(ClassIntrospector.MixInResolver overrides, Map<ClassKey, Class<?>> mixins) {
      this._overrides = overrides;
      this._localMixIns = mixins;
   }

   public SimpleMixInResolver withOverrides(ClassIntrospector.MixInResolver overrides) {
      return new SimpleMixInResolver(overrides, this._localMixIns);
   }

   public SimpleMixInResolver withoutLocalDefinitions() {
      return new SimpleMixInResolver(this._overrides, (Map)null);
   }

   public void setLocalDefinitions(Map<Class<?>, Class<?>> sourceMixins) {
      if (sourceMixins != null && !sourceMixins.isEmpty()) {
         Map<ClassKey, Class<?>> mixIns = new HashMap(sourceMixins.size());
         Iterator i$ = sourceMixins.entrySet().iterator();

         while(i$.hasNext()) {
            Entry<Class<?>, Class<?>> en = (Entry)i$.next();
            mixIns.put(new ClassKey((Class)en.getKey()), en.getValue());
         }

         this._localMixIns = mixIns;
      } else {
         this._localMixIns = null;
      }

   }

   public void addLocalDefinition(Class<?> target, Class<?> mixinSource) {
      if (this._localMixIns == null) {
         this._localMixIns = new HashMap();
      }

      this._localMixIns.put(new ClassKey(target), mixinSource);
   }

   public SimpleMixInResolver copy() {
      ClassIntrospector.MixInResolver overrides = this._overrides == null ? null : this._overrides.copy();
      Map<ClassKey, Class<?>> mixIns = this._localMixIns == null ? null : new HashMap(this._localMixIns);
      return new SimpleMixInResolver(overrides, mixIns);
   }

   public Class<?> findMixInClassFor(Class<?> cls) {
      Class<?> mixin = this._overrides == null ? null : this._overrides.findMixInClassFor(cls);
      if (mixin == null && this._localMixIns != null) {
         mixin = (Class)this._localMixIns.get(new ClassKey(cls));
      }

      return mixin;
   }

   public int localSize() {
      return this._localMixIns == null ? 0 : this._localMixIns.size();
   }
}
