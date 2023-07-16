package software.bernie.shadowed.fasterxml.jackson.databind;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.TimeZone;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public abstract class DatabindContext {
   private static final int MAX_ERROR_STR_LEN = 500;

   public abstract MapperConfig<?> getConfig();

   public abstract AnnotationIntrospector getAnnotationIntrospector();

   public abstract boolean isEnabled(MapperFeature var1);

   public abstract boolean canOverrideAccessModifiers();

   public abstract Class<?> getActiveView();

   public abstract Locale getLocale();

   public abstract TimeZone getTimeZone();

   public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> var1);

   public abstract Object getAttribute(Object var1);

   public abstract DatabindContext setAttribute(Object var1, Object var2);

   public JavaType constructType(Type type) {
      return type == null ? null : this.getTypeFactory().constructType(type);
   }

   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
      return baseType.getRawClass() == subclass ? baseType : this.getConfig().constructSpecializedType(baseType, subclass);
   }

   public JavaType resolveSubType(JavaType baseType, String subClass) throws JsonMappingException {
      if (subClass.indexOf(60) > 0) {
         return this.getTypeFactory().constructFromCanonical(subClass);
      } else {
         Class cls;
         try {
            cls = this.getTypeFactory().findClass(subClass);
         } catch (ClassNotFoundException var5) {
            return null;
         } catch (Exception var6) {
            throw this.invalidTypeIdException(baseType, subClass, String.format("problem: (%s) %s", var6.getClass().getName(), var6.getMessage()));
         }

         if (baseType.isTypeOrSuperTypeOf(cls)) {
            return this.getTypeFactory().constructSpecializedType(baseType, cls);
         } else {
            throw this.invalidTypeIdException(baseType, subClass, "Not a subtype");
         }
      }
   }

   protected abstract JsonMappingException invalidTypeIdException(JavaType var1, String var2, String var3);

   public abstract TypeFactory getTypeFactory();

   public ObjectIdGenerator<?> objectIdGeneratorInstance(Annotated annotated, ObjectIdInfo objectIdInfo) throws JsonMappingException {
      Class<?> implClass = objectIdInfo.getGeneratorType();
      MapperConfig<?> config = this.getConfig();
      HandlerInstantiator hi = config.getHandlerInstantiator();
      ObjectIdGenerator<?> gen = hi == null ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
      if (gen == null) {
         gen = (ObjectIdGenerator)ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
      }

      return gen.forScope(objectIdInfo.getScope());
   }

   public ObjectIdResolver objectIdResolverInstance(Annotated annotated, ObjectIdInfo objectIdInfo) {
      Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
      MapperConfig<?> config = this.getConfig();
      HandlerInstantiator hi = config.getHandlerInstantiator();
      ObjectIdResolver resolver = hi == null ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
      if (resolver == null) {
         resolver = (ObjectIdResolver)ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
      }

      return resolver;
   }

   public Converter<Object, Object> converterInstance(Annotated annotated, Object converterDef) throws JsonMappingException {
      if (converterDef == null) {
         return null;
      } else if (converterDef instanceof Converter) {
         return (Converter)converterDef;
      } else if (!(converterDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
      } else {
         Class<?> converterClass = (Class)converterDef;
         if (converterClass != Converter.None.class && !ClassUtil.isBogusClass(converterClass)) {
            if (!Converter.class.isAssignableFrom(converterClass)) {
               throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
            } else {
               MapperConfig<?> config = this.getConfig();
               HandlerInstantiator hi = config.getHandlerInstantiator();
               Converter<?, ?> conv = hi == null ? null : hi.converterInstance(config, annotated, converterClass);
               if (conv == null) {
                  conv = (Converter)ClassUtil.createInstance(converterClass, config.canOverrideAccessModifiers());
               }

               return conv;
            }
         } else {
            return null;
         }
      }
   }

   public abstract <T> T reportBadDefinition(JavaType var1, String var2) throws JsonMappingException;

   public <T> T reportBadDefinition(Class<?> type, String msg) throws JsonMappingException {
      return this.reportBadDefinition(this.constructType(type), msg);
   }

   protected final String _format(String msg, Object... msgArgs) {
      return msgArgs.length > 0 ? String.format(msg, msgArgs) : msg;
   }

   protected final String _truncate(String desc) {
      if (desc == null) {
         return "";
      } else {
         return desc.length() <= 500 ? desc : desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
      }
   }

   protected String _quotedString(String desc) {
      return desc == null ? "[N/A]" : String.format("\"%s\"", this._truncate(desc));
   }

   protected String _colonConcat(String msgBase, String extra) {
      return extra == null ? msgBase : msgBase + ": " + extra;
   }

   protected String _calcName(Class<?> cls) {
      return cls.isArray() ? this._calcName(cls.getComponentType()) + "[]" : cls.getName();
   }

   protected String _desc(String desc) {
      return desc == null ? "[N/A]" : this._truncate(desc);
   }
}
