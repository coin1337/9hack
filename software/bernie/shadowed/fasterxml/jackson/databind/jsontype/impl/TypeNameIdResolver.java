package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DatabindContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.NamedType;

public class TypeNameIdResolver extends TypeIdResolverBase {
   protected final MapperConfig<?> _config;
   protected final Map<String, String> _typeToId;
   protected final Map<String, JavaType> _idToType;

   protected TypeNameIdResolver(MapperConfig<?> config, JavaType baseType, Map<String, String> typeToId, Map<String, JavaType> idToType) {
      super(baseType, config.getTypeFactory());
      this._config = config;
      this._typeToId = typeToId;
      this._idToType = idToType;
   }

   public static TypeNameIdResolver construct(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
      if (forSer == forDeser) {
         throw new IllegalArgumentException();
      } else {
         Map<String, String> typeToId = null;
         Map<String, JavaType> idToType = null;
         if (forSer) {
            typeToId = new HashMap();
         }

         if (forDeser) {
            idToType = new HashMap();
            typeToId = new TreeMap();
         }

         if (subtypes != null) {
            Iterator i$ = subtypes.iterator();

            while(true) {
               Class cls;
               String id;
               JavaType prev;
               do {
                  do {
                     if (!i$.hasNext()) {
                        return new TypeNameIdResolver(config, baseType, (Map)typeToId, idToType);
                     }

                     NamedType t = (NamedType)i$.next();
                     cls = t.getType();
                     id = t.hasName() ? t.getName() : _defaultTypeId(cls);
                     if (forSer) {
                        ((Map)typeToId).put(cls.getName(), id);
                     }
                  } while(!forDeser);

                  prev = (JavaType)idToType.get(id);
               } while(prev != null && cls.isAssignableFrom(prev.getRawClass()));

               idToType.put(id, config.constructType(cls));
            }
         } else {
            return new TypeNameIdResolver(config, baseType, (Map)typeToId, idToType);
         }
      }
   }

   public JsonTypeInfo.Id getMechanism() {
      return JsonTypeInfo.Id.NAME;
   }

   public String idFromValue(Object value) {
      return this.idFromClass(value.getClass());
   }

   protected String idFromClass(Class<?> clazz) {
      if (clazz == null) {
         return null;
      } else {
         Class<?> cls = this._typeFactory.constructType((Type)clazz).getRawClass();
         String key = cls.getName();
         synchronized(this._typeToId) {
            String name = (String)this._typeToId.get(key);
            if (name == null) {
               if (this._config.isAnnotationProcessingEnabled()) {
                  BeanDescription beanDesc = this._config.introspectClassAnnotations(cls);
                  name = this._config.getAnnotationIntrospector().findTypeName(beanDesc.getClassInfo());
               }

               if (name == null) {
                  name = _defaultTypeId(cls);
               }

               this._typeToId.put(key, name);
            }

            return name;
         }
      }
   }

   public String idFromValueAndType(Object value, Class<?> type) {
      return value == null ? this.idFromClass(type) : this.idFromValue(value);
   }

   public JavaType typeFromId(DatabindContext context, String id) {
      return this._typeFromId(id);
   }

   protected JavaType _typeFromId(String id) {
      return (JavaType)this._idToType.get(id);
   }

   public String getDescForKnownTypeIds() {
      return (new TreeSet(this._idToType.keySet())).toString();
   }

   public String toString() {
      return String.format("[%s; id-to-type=%s]", this.getClass().getName(), this._idToType);
   }

   protected static String _defaultTypeId(Class<?> cls) {
      String n = cls.getName();
      int ix = n.lastIndexOf(46);
      return ix < 0 ? n : n.substring(ix + 1);
   }
}
