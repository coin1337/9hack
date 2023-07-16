package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import software.bernie.shadowed.fasterxml.jackson.databind.AbstractTypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ArrayType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ReferenceType;

public abstract class DeserializerFactory {
   protected static final Deserializers[] NO_DESERIALIZERS = new Deserializers[0];

   public abstract DeserializerFactory withAdditionalDeserializers(Deserializers var1);

   public abstract DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers var1);

   public abstract DeserializerFactory withDeserializerModifier(BeanDeserializerModifier var1);

   public abstract DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver var1);

   public abstract DeserializerFactory withValueInstantiators(ValueInstantiators var1);

   public abstract JavaType mapAbstractType(DeserializationConfig var1, JavaType var2) throws JsonMappingException;

   public abstract ValueInstantiator findValueInstantiator(DeserializationContext var1, BeanDescription var2) throws JsonMappingException;

   public abstract JsonDeserializer<Object> createBeanDeserializer(DeserializationContext var1, JavaType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext var1, JavaType var2, BeanDescription var3, Class<?> var4) throws JsonMappingException;

   public abstract JsonDeserializer<?> createEnumDeserializer(DeserializationContext var1, JavaType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createReferenceDeserializer(DeserializationContext var1, ReferenceType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createTreeDeserializer(DeserializationConfig var1, JavaType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createArrayDeserializer(DeserializationContext var1, ArrayType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createCollectionDeserializer(DeserializationContext var1, CollectionType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext var1, CollectionLikeType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createMapDeserializer(DeserializationContext var1, MapType var2, BeanDescription var3) throws JsonMappingException;

   public abstract JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext var1, MapLikeType var2, BeanDescription var3) throws JsonMappingException;

   public abstract KeyDeserializer createKeyDeserializer(DeserializationContext var1, JavaType var2) throws JsonMappingException;

   public abstract TypeDeserializer findTypeDeserializer(DeserializationConfig var1, JavaType var2) throws JsonMappingException;
}
