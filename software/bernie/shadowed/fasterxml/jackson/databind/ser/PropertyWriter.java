package software.bernie.shadowed.fasterxml.jackson.databind.ser;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;

public abstract class PropertyWriter extends ConcreteBeanPropertyBase implements Serializable {
   private static final long serialVersionUID = 1L;

   protected PropertyWriter(PropertyMetadata md) {
      super(md);
   }

   protected PropertyWriter(BeanPropertyDefinition propDef) {
      super(propDef.getMetadata());
   }

   protected PropertyWriter(PropertyWriter base) {
      super((ConcreteBeanPropertyBase)base);
   }

   public abstract String getName();

   public abstract PropertyName getFullName();

   public <A extends Annotation> A findAnnotation(Class<A> acls) {
      A ann = this.getAnnotation(acls);
      if (ann == null) {
         ann = this.getContextAnnotation(acls);
      }

      return ann;
   }

   public abstract <A extends Annotation> A getAnnotation(Class<A> var1);

   public abstract <A extends Annotation> A getContextAnnotation(Class<A> var1);

   public abstract void serializeAsField(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract void serializeAsOmittedField(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract void serializeAsElement(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract void serializeAsPlaceholder(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract void depositSchemaProperty(JsonObjectFormatVisitor var1, SerializerProvider var2) throws JsonMappingException;

   /** @deprecated */
   @Deprecated
   public abstract void depositSchemaProperty(ObjectNode var1, SerializerProvider var2) throws JsonMappingException;
}
