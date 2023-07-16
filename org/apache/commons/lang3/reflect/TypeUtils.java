package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

public class TypeUtils {
   public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

   public static boolean isAssignable(Type type, Type toType) {
      return isAssignable(type, (Type)toType, (Map)null);
   }

   private static boolean isAssignable(Type type, Type toType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (toType != null && !(toType instanceof Class)) {
         if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType)toType, typeVarAssigns);
         } else if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType)toType, typeVarAssigns);
         } else if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType)toType, typeVarAssigns);
         } else if (toType instanceof TypeVariable) {
            return isAssignable(type, (TypeVariable)toType, typeVarAssigns);
         } else {
            throw new IllegalStateException("found an unhandled type: " + toType);
         }
      } else {
         return isAssignable(type, (Class)toType);
      }
   }

   private static boolean isAssignable(Type type, Class<?> toClass) {
      if (type == null) {
         return toClass == null || !toClass.isPrimitive();
      } else if (toClass == null) {
         return false;
      } else if (toClass.equals(type)) {
         return true;
      } else if (type instanceof Class) {
         return ClassUtils.isAssignable((Class)type, toClass);
      } else if (type instanceof ParameterizedType) {
         return isAssignable(getRawType((ParameterizedType)type), (Class)toClass);
      } else if (type instanceof TypeVariable) {
         Type[] var2 = ((TypeVariable)type).getBounds();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type bound = var2[var4];
            if (isAssignable(bound, toClass)) {
               return true;
            }
         }

         return false;
      } else if (!(type instanceof GenericArrayType)) {
         if (type instanceof WildcardType) {
            return false;
         } else {
            throw new IllegalStateException("found an unhandled type: " + type);
         }
      } else {
         return toClass.equals(Object.class) || toClass.isArray() && isAssignable(((GenericArrayType)type).getGenericComponentType(), toClass.getComponentType());
      }
   }

   private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toParameterizedType == null) {
         return false;
      } else if (toParameterizedType.equals(type)) {
         return true;
      } else {
         Class<?> toClass = getRawType(toParameterizedType);
         Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments((Type)type, toClass, (Map)null);
         if (fromTypeVarAssigns == null) {
            return false;
         } else if (fromTypeVarAssigns.isEmpty()) {
            return true;
         } else {
            Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);
            Iterator var6 = toTypeVarAssigns.keySet().iterator();

            Type toTypeArg;
            Type fromTypeArg;
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           return true;
                        }

                        TypeVariable<?> var = (TypeVariable)var6.next();
                        toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
                        fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
                     } while(toTypeArg == null && fromTypeArg instanceof Class);
                  } while(fromTypeArg == null);
               } while(toTypeArg.equals(fromTypeArg));
            } while(toTypeArg instanceof WildcardType && isAssignable(fromTypeArg, toTypeArg, typeVarAssigns));

            return false;
         }
      }
   }

   private static Type unrollVariableAssignments(TypeVariable<?> typeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
      while(true) {
         Type result = (Type)typeVarAssigns.get(typeVariable);
         if (!(result instanceof TypeVariable) || result.equals(typeVariable)) {
            return result;
         }

         typeVariable = (TypeVariable)result;
      }
   }

   private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toGenericArrayType == null) {
         return false;
      } else if (toGenericArrayType.equals(type)) {
         return true;
      } else {
         Type toComponentType = toGenericArrayType.getGenericComponentType();
         if (!(type instanceof Class)) {
            if (type instanceof GenericArrayType) {
               return isAssignable(((GenericArrayType)type).getGenericComponentType(), toComponentType, typeVarAssigns);
            } else {
               int var5;
               int var6;
               Type bound;
               Type[] var8;
               if (type instanceof WildcardType) {
                  var8 = getImplicitUpperBounds((WildcardType)type);
                  var5 = var8.length;

                  for(var6 = 0; var6 < var5; ++var6) {
                     bound = var8[var6];
                     if (isAssignable(bound, (Type)toGenericArrayType)) {
                        return true;
                     }
                  }

                  return false;
               } else if (type instanceof TypeVariable) {
                  var8 = getImplicitBounds((TypeVariable)type);
                  var5 = var8.length;

                  for(var6 = 0; var6 < var5; ++var6) {
                     bound = var8[var6];
                     if (isAssignable(bound, (Type)toGenericArrayType)) {
                        return true;
                     }
                  }

                  return false;
               } else if (type instanceof ParameterizedType) {
                  return false;
               } else {
                  throw new IllegalStateException("found an unhandled type: " + type);
               }
            }
         } else {
            Class<?> cls = (Class)type;
            return cls.isArray() && isAssignable(cls.getComponentType(), (Type)toComponentType, typeVarAssigns);
         }
      }
   }

   private static boolean isAssignable(Type type, WildcardType toWildcardType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toWildcardType == null) {
         return false;
      } else if (toWildcardType.equals(type)) {
         return true;
      } else {
         Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
         Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
         if (!(type instanceof WildcardType)) {
            Type[] var16 = toUpperBounds;
            int var17 = toUpperBounds.length;

            int var18;
            Type toBound;
            for(var18 = 0; var18 < var17; ++var18) {
               toBound = var16[var18];
               if (!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns), typeVarAssigns)) {
                  return false;
               }
            }

            var16 = toLowerBounds;
            var17 = toLowerBounds.length;

            for(var18 = 0; var18 < var17; ++var18) {
               toBound = var16[var18];
               if (!isAssignable(substituteTypeVariables(toBound, typeVarAssigns), type, typeVarAssigns)) {
                  return false;
               }
            }

            return true;
         } else {
            WildcardType wildcardType = (WildcardType)type;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType);
            Type[] var8 = toUpperBounds;
            int var9 = toUpperBounds.length;

            int var10;
            Type toBound;
            Type[] var12;
            int var13;
            int var14;
            Type bound;
            for(var10 = 0; var10 < var9; ++var10) {
               toBound = var8[var10];
               toBound = substituteTypeVariables(toBound, typeVarAssigns);
               var12 = upperBounds;
               var13 = upperBounds.length;

               for(var14 = 0; var14 < var13; ++var14) {
                  bound = var12[var14];
                  if (!isAssignable(bound, toBound, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            var8 = toLowerBounds;
            var9 = toLowerBounds.length;

            for(var10 = 0; var10 < var9; ++var10) {
               toBound = var8[var10];
               toBound = substituteTypeVariables(toBound, typeVarAssigns);
               var12 = lowerBounds;
               var13 = lowerBounds.length;

               for(var14 = 0; var14 < var13; ++var14) {
                  bound = var12[var14];
                  if (!isAssignable(toBound, bound, typeVarAssigns)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type == null) {
         return true;
      } else if (toTypeVariable == null) {
         return false;
      } else if (toTypeVariable.equals(type)) {
         return true;
      } else {
         if (type instanceof TypeVariable) {
            Type[] bounds = getImplicitBounds((TypeVariable)type);
            Type[] var4 = bounds;
            int var5 = bounds.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Type bound = var4[var6];
               if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                  return true;
               }
            }
         }

         if (!(type instanceof Class) && !(type instanceof ParameterizedType) && !(type instanceof GenericArrayType) && !(type instanceof WildcardType)) {
            throw new IllegalStateException("found an unhandled type: " + type);
         } else {
            return false;
         }
      }
   }

   private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
      if (type instanceof TypeVariable && typeVarAssigns != null) {
         Type replacementType = (Type)typeVarAssigns.get(type);
         if (replacementType == null) {
            throw new IllegalArgumentException("missing assignment type for type variable " + type);
         } else {
            return replacementType;
         }
      } else {
         return type;
      }
   }

   public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
      return getTypeArguments((ParameterizedType)type, getRawType(type), (Map)null);
   }

   public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
      return getTypeArguments((Type)type, toClass, (Map)null);
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
      if (type instanceof Class) {
         return getTypeArguments((Class)type, toClass, subtypeVarAssigns);
      } else if (type instanceof ParameterizedType) {
         return getTypeArguments((ParameterizedType)type, toClass, subtypeVarAssigns);
      } else if (type instanceof GenericArrayType) {
         return getTypeArguments(((GenericArrayType)type).getGenericComponentType(), toClass.isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
      } else {
         Type[] var3;
         int var4;
         int var5;
         Type bound;
         if (type instanceof WildcardType) {
            var3 = getImplicitUpperBounds((WildcardType)type);
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               bound = var3[var5];
               if (isAssignable(bound, toClass)) {
                  return getTypeArguments(bound, toClass, subtypeVarAssigns);
               }
            }

            return null;
         } else if (type instanceof TypeVariable) {
            var3 = getImplicitBounds((TypeVariable)type);
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               bound = var3[var5];
               if (isAssignable(bound, toClass)) {
                  return getTypeArguments(bound, toClass, subtypeVarAssigns);
               }
            }

            return null;
         } else {
            throw new IllegalStateException("found an unhandled type: " + type);
         }
      }
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType parameterizedType, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
      Class<?> cls = getRawType(parameterizedType);
      if (!isAssignable(cls, (Class)toClass)) {
         return null;
      } else {
         Type ownerType = parameterizedType.getOwnerType();
         Object typeVarAssigns;
         if (ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedOwnerType = (ParameterizedType)ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType, getRawType(parameterizedOwnerType), subtypeVarAssigns);
         } else {
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap() : new HashMap(subtypeVarAssigns);
         }

         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         TypeVariable<?>[] typeParams = cls.getTypeParameters();

         for(int i = 0; i < typeParams.length; ++i) {
            Type typeArg = typeArgs[i];
            ((Map)typeVarAssigns).put(typeParams[i], ((Map)typeVarAssigns).getOrDefault(typeArg, typeArg));
         }

         return (Map)(toClass.equals(cls) ? typeVarAssigns : getTypeArguments((Type)getClosestParentType(cls, toClass), toClass, (Map)typeVarAssigns));
      }
   }

   private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
      if (!isAssignable(cls, (Class)toClass)) {
         return null;
      } else {
         if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
               return new HashMap();
            }

            cls = ClassUtils.primitiveToWrapper(cls);
         }

         HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap() : new HashMap(subtypeVarAssigns);
         return (Map)(toClass.equals(cls) ? typeVarAssigns : getTypeArguments((Type)getClosestParentType(cls, toClass), toClass, typeVarAssigns));
      }
   }

   public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls, ParameterizedType superType) {
      Validate.notNull(cls, "cls is null");
      Validate.notNull(superType, "superType is null");
      Class<?> superClass = getRawType(superType);
      if (!isAssignable(cls, (Class)superClass)) {
         return null;
      } else if (cls.equals(superClass)) {
         return getTypeArguments((ParameterizedType)superType, superClass, (Map)null);
      } else {
         Type midType = getClosestParentType(cls, superClass);
         if (midType instanceof Class) {
            return determineTypeArguments((Class)midType, superType);
         } else {
            ParameterizedType midParameterizedType = (ParameterizedType)midType;
            Class<?> midClass = getRawType(midParameterizedType);
            Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superType);
            mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
            return typeVarAssigns;
         }
      }
   }

   private static <T> void mapTypeVariablesToArguments(Class<T> cls, ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
      Type ownerType = parameterizedType.getOwnerType();
      if (ownerType instanceof ParameterizedType) {
         mapTypeVariablesToArguments(cls, (ParameterizedType)ownerType, typeVarAssigns);
      }

      Type[] typeArgs = parameterizedType.getActualTypeArguments();
      TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
      List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());

      for(int i = 0; i < typeArgs.length; ++i) {
         TypeVariable<?> typeVar = typeVars[i];
         Type typeArg = typeArgs[i];
         if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
            typeVarAssigns.put((TypeVariable)typeArg, typeVarAssigns.get(typeVar));
         }
      }

   }

   private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
      if (superClass.isInterface()) {
         Type[] interfaceTypes = cls.getGenericInterfaces();
         Type genericInterface = null;
         Type[] var4 = interfaceTypes;
         int var5 = interfaceTypes.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Type midType = var4[var6];
            Class<?> midClass = null;
            if (midType instanceof ParameterizedType) {
               midClass = getRawType((ParameterizedType)midType);
            } else {
               if (!(midType instanceof Class)) {
                  throw new IllegalStateException("Unexpected generic interface type found: " + midType);
               }

               midClass = (Class)midType;
            }

            if (isAssignable(midClass, (Class)superClass) && isAssignable(genericInterface, (Type)midClass)) {
               genericInterface = midType;
            }
         }

         if (genericInterface != null) {
            return genericInterface;
         }
      }

      return cls.getGenericSuperclass();
   }

   public static boolean isInstance(Object value, Type type) {
      if (type == null) {
         return false;
      } else {
         return value == null ? !(type instanceof Class) || !((Class)type).isPrimitive() : isAssignable(value.getClass(), (Type)type, (Map)null);
      }
   }

   public static Type[] normalizeUpperBounds(Type[] bounds) {
      Validate.notNull(bounds, "null value specified for bounds array");
      if (bounds.length < 2) {
         return bounds;
      } else {
         Set<Type> types = new HashSet(bounds.length);
         Type[] var2 = bounds;
         int var3 = bounds.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type type1 = var2[var4];
            boolean subtypeFound = false;
            Type[] var7 = bounds;
            int var8 = bounds.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Type type2 = var7[var9];
               if (type1 != type2 && isAssignable(type2, (Type)type1, (Map)null)) {
                  subtypeFound = true;
                  break;
               }
            }

            if (!subtypeFound) {
               types.add(type1);
            }
         }

         return (Type[])types.toArray(ArrayUtils.EMPTY_TYPE_ARRAY);
      }
   }

   public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
      Validate.notNull(typeVariable, "typeVariable is null");
      Type[] bounds = typeVariable.getBounds();
      return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
   }

   public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType is null");
      Type[] bounds = wildcardType.getUpperBounds();
      return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
   }

   public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
      Validate.notNull(wildcardType, "wildcardType is null");
      Type[] bounds = wildcardType.getLowerBounds();
      return bounds.length == 0 ? new Type[]{null} : bounds;
   }

   public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVarAssigns) {
      Validate.notNull(typeVarAssigns, "typeVarAssigns is null");
      Iterator var1 = typeVarAssigns.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<TypeVariable<?>, Type> entry = (Entry)var1.next();
         TypeVariable<?> typeVar = (TypeVariable)entry.getKey();
         Type type = (Type)entry.getValue();
         Type[] var5 = getImplicitBounds(typeVar);
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Type bound = var5[var7];
            if (!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns), typeVarAssigns)) {
               return false;
            }
         }
      }

      return true;
   }

   private static Class<?> getRawType(ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if (!(rawType instanceof Class)) {
         throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
      } else {
         return (Class)rawType;
      }
   }

   public static Class<?> getRawType(Type type, Type assigningType) {
      if (type instanceof Class) {
         return (Class)type;
      } else if (type instanceof ParameterizedType) {
         return getRawType((ParameterizedType)type);
      } else if (type instanceof TypeVariable) {
         if (assigningType == null) {
            return null;
         } else {
            Object genericDeclaration = ((TypeVariable)type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class)) {
               return null;
            } else {
               Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType, (Class)genericDeclaration);
               if (typeVarAssigns == null) {
                  return null;
               } else {
                  Type typeArgument = (Type)typeVarAssigns.get(type);
                  return typeArgument == null ? null : getRawType(typeArgument, assigningType);
               }
            }
         }
      } else if (type instanceof GenericArrayType) {
         Class<?> rawComponentType = getRawType(((GenericArrayType)type).getGenericComponentType(), assigningType);
         return Array.newInstance(rawComponentType, 0).getClass();
      } else if (type instanceof WildcardType) {
         return null;
      } else {
         throw new IllegalArgumentException("unknown type: " + type);
      }
   }

   public static boolean isArrayType(Type type) {
      return type instanceof GenericArrayType || type instanceof Class && ((Class)type).isArray();
   }

   public static Type getArrayComponentType(Type type) {
      if (type instanceof Class) {
         Class<?> cls = (Class)type;
         return cls.isArray() ? cls.getComponentType() : null;
      } else {
         return type instanceof GenericArrayType ? ((GenericArrayType)type).getGenericComponentType() : null;
      }
   }

   public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, Type type) {
      if (typeArguments == null) {
         typeArguments = Collections.emptyMap();
      }

      if (containsTypeVariables(type)) {
         if (type instanceof TypeVariable) {
            return unrollVariables(typeArguments, (Type)typeArguments.get(type));
         }

         if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)type;
            Object parameterizedTypeArguments;
            if (p.getOwnerType() == null) {
               parameterizedTypeArguments = typeArguments;
            } else {
               parameterizedTypeArguments = new HashMap(typeArguments);
               ((Map)parameterizedTypeArguments).putAll(getTypeArguments(p));
            }

            Type[] args = p.getActualTypeArguments();

            for(int i = 0; i < args.length; ++i) {
               Type unrolled = unrollVariables((Map)parameterizedTypeArguments, args[i]);
               if (unrolled != null) {
                  args[i] = unrolled;
               }
            }

            return parameterizeWithOwner(p.getOwnerType(), (Class)p.getRawType(), args);
         }

         if (type instanceof WildcardType) {
            WildcardType wild = (WildcardType)type;
            return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
         }
      }

      return type;
   }

   private static Type[] unrollBounds(Map<TypeVariable<?>, Type> typeArguments, Type[] bounds) {
      Type[] result = bounds;

      for(int i = 0; i < result.length; ++i) {
         Type unrolled = unrollVariables(typeArguments, result[i]);
         if (unrolled == null) {
            result = (Type[])ArrayUtils.remove((Object[])result, i--);
         } else {
            result[i] = unrolled;
         }
      }

      return result;
   }

   public static boolean containsTypeVariables(Type type) {
      if (type instanceof TypeVariable) {
         return true;
      } else if (type instanceof Class) {
         return ((Class)type).getTypeParameters().length > 0;
      } else if (type instanceof ParameterizedType) {
         Type[] var5 = ((ParameterizedType)type).getActualTypeArguments();
         int var2 = var5.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Type arg = var5[var3];
            if (containsTypeVariables(arg)) {
               return true;
            }
         }

         return false;
      } else if (!(type instanceof WildcardType)) {
         return false;
      } else {
         WildcardType wild = (WildcardType)type;
         return containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
      }
   }

   public static final ParameterizedType parameterize(Class<?> rawClass, Type... typeArguments) {
      return parameterizeWithOwner((Type)null, rawClass, (Type[])typeArguments);
   }

   public static final ParameterizedType parameterize(Class<?> rawClass, Map<TypeVariable<?>, Type> typeArgMappings) {
      Validate.notNull(rawClass, "raw class is null");
      Validate.notNull(typeArgMappings, "typeArgMappings is null");
      return parameterizeWithOwner((Type)null, rawClass, (Type[])extractTypeArgumentsFrom(typeArgMappings, rawClass.getTypeParameters()));
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Type... typeArguments) {
      Validate.notNull(rawClass, "raw class is null");
      Object useOwner;
      if (rawClass.getEnclosingClass() == null) {
         Validate.isTrue(owner == null, "no owner allowed for top-level %s", rawClass);
         useOwner = null;
      } else if (owner == null) {
         useOwner = rawClass.getEnclosingClass();
      } else {
         Validate.isTrue(isAssignable(owner, rawClass.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, rawClass);
         useOwner = owner;
      }

      Validate.noNullElements((Object[])typeArguments, "null type argument at index %s");
      Validate.isTrue(rawClass.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %d, got %d", rawClass.getTypeParameters().length, typeArguments.length);
      return new TypeUtils.ParameterizedTypeImpl(rawClass, (Type)useOwner, typeArguments);
   }

   public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Map<TypeVariable<?>, Type> typeArgMappings) {
      Validate.notNull(rawClass, "raw class is null");
      Validate.notNull(typeArgMappings, "typeArgMappings is null");
      return parameterizeWithOwner(owner, rawClass, extractTypeArgumentsFrom(typeArgMappings, rawClass.getTypeParameters()));
   }

   private static Type[] extractTypeArgumentsFrom(Map<TypeVariable<?>, Type> mappings, TypeVariable<?>[] variables) {
      Type[] result = new Type[variables.length];
      int index = 0;
      TypeVariable[] var4 = variables;
      int var5 = variables.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         TypeVariable<?> var = var4[var6];
         Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString((Type)var));
         result[index++] = (Type)mappings.get(var);
      }

      return result;
   }

   public static TypeUtils.WildcardTypeBuilder wildcardType() {
      return new TypeUtils.WildcardTypeBuilder();
   }

   public static GenericArrayType genericArrayType(Type componentType) {
      return new TypeUtils.GenericArrayTypeImpl((Type)Validate.notNull(componentType, "componentType is null"));
   }

   public static boolean equals(Type type1, Type type2) {
      if (Objects.equals(type1, type2)) {
         return true;
      } else if (type1 instanceof ParameterizedType) {
         return equals((ParameterizedType)type1, type2);
      } else if (type1 instanceof GenericArrayType) {
         return equals((GenericArrayType)type1, type2);
      } else {
         return type1 instanceof WildcardType ? equals((WildcardType)type1, type2) : false;
      }
   }

   private static boolean equals(ParameterizedType parameterizedType, Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType other = (ParameterizedType)type;
         if (equals(parameterizedType.getRawType(), other.getRawType()) && equals(parameterizedType.getOwnerType(), other.getOwnerType())) {
            return equals(parameterizedType.getActualTypeArguments(), other.getActualTypeArguments());
         }
      }

      return false;
   }

   private static boolean equals(GenericArrayType genericArrayType, Type type) {
      return type instanceof GenericArrayType && equals(genericArrayType.getGenericComponentType(), ((GenericArrayType)type).getGenericComponentType());
   }

   private static boolean equals(WildcardType wildcardType, Type type) {
      if (!(type instanceof WildcardType)) {
         return false;
      } else {
         WildcardType other = (WildcardType)type;
         return equals(getImplicitLowerBounds(wildcardType), getImplicitLowerBounds(other)) && equals(getImplicitUpperBounds(wildcardType), getImplicitUpperBounds(other));
      }
   }

   private static boolean equals(Type[] type1, Type[] type2) {
      if (type1.length == type2.length) {
         for(int i = 0; i < type1.length; ++i) {
            if (!equals(type1[i], type2[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static String toString(Type type) {
      Validate.notNull(type);
      if (type instanceof Class) {
         return classToString((Class)type);
      } else if (type instanceof ParameterizedType) {
         return parameterizedTypeToString((ParameterizedType)type);
      } else if (type instanceof WildcardType) {
         return wildcardTypeToString((WildcardType)type);
      } else if (type instanceof TypeVariable) {
         return typeVariableToString((TypeVariable)type);
      } else if (type instanceof GenericArrayType) {
         return genericArrayTypeToString((GenericArrayType)type);
      } else {
         throw new IllegalArgumentException(ObjectUtils.identityToString(type));
      }
   }

   public static String toLongString(TypeVariable<?> var) {
      Validate.notNull(var, "var is null");
      StringBuilder buf = new StringBuilder();
      GenericDeclaration d = var.getGenericDeclaration();
      if (d instanceof Class) {
         Class c;
         for(c = (Class)d; c.getEnclosingClass() != null; c = c.getEnclosingClass()) {
            buf.insert(0, c.getSimpleName()).insert(0, '.');
         }

         buf.insert(0, c.getName());
      } else if (d instanceof Type) {
         buf.append(toString((Type)d));
      } else {
         buf.append(d);
      }

      return buf.append(':').append(typeVariableToString(var)).toString();
   }

   public static <T> Typed<T> wrap(Type type) {
      return () -> {
         return type;
      };
   }

   public static <T> Typed<T> wrap(Class<T> type) {
      return wrap((Type)type);
   }

   private static String classToString(Class<?> cls) {
      if (cls.isArray()) {
         return toString((Type)cls.getComponentType()) + "[]";
      } else {
         StringBuilder buf = new StringBuilder();
         if (cls.getEnclosingClass() != null) {
            buf.append(classToString(cls.getEnclosingClass())).append('.').append(cls.getSimpleName());
         } else {
            buf.append(cls.getName());
         }

         if (cls.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", cls.getTypeParameters());
            buf.append('>');
         }

         return buf.toString();
      }
   }

   private static String typeVariableToString(TypeVariable<?> typeVariable) {
      StringBuilder buf = new StringBuilder(typeVariable.getName());
      Type[] bounds = typeVariable.getBounds();
      if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
         buf.append(" extends ");
         appendAllTo(buf, " & ", typeVariable.getBounds());
      }

      return buf.toString();
   }

   private static String parameterizedTypeToString(ParameterizedType parameterizedType) {
      StringBuilder builder = new StringBuilder();
      Type useOwner = parameterizedType.getOwnerType();
      Class<?> raw = (Class)parameterizedType.getRawType();
      if (useOwner == null) {
         builder.append(raw.getName());
      } else {
         if (useOwner instanceof Class) {
            builder.append(((Class)useOwner).getName());
         } else {
            builder.append(useOwner.toString());
         }

         builder.append('.').append(raw.getSimpleName());
      }

      int[] recursiveTypeIndexes = findRecursiveTypes(parameterizedType);
      if (recursiveTypeIndexes.length > 0) {
         appendRecursiveTypes(builder, recursiveTypeIndexes, parameterizedType.getActualTypeArguments());
      } else {
         appendAllTo(builder.append('<'), ", ", parameterizedType.getActualTypeArguments()).append('>');
      }

      return builder.toString();
   }

   private static void appendRecursiveTypes(StringBuilder builder, int[] recursiveTypeIndexes, Type[] argumentTypes) {
      for(int i = 0; i < recursiveTypeIndexes.length; ++i) {
         appendAllTo(builder.append('<'), ", ", argumentTypes[i].toString()).append('>');
      }

      Type[] argumentsFiltered = (Type[])ArrayUtils.removeAll((Object[])argumentTypes, (int[])recursiveTypeIndexes);
      if (argumentsFiltered.length > 0) {
         appendAllTo(builder.append('<'), ", ", argumentsFiltered).append('>');
      }

   }

   private static int[] findRecursiveTypes(ParameterizedType parameterizedType) {
      Type[] filteredArgumentTypes = (Type[])Arrays.copyOf(parameterizedType.getActualTypeArguments(), parameterizedType.getActualTypeArguments().length);
      int[] indexesToRemove = new int[0];

      for(int i = 0; i < filteredArgumentTypes.length; ++i) {
         if (filteredArgumentTypes[i] instanceof TypeVariable && containsVariableTypeSameParametrizedTypeBound((TypeVariable)filteredArgumentTypes[i], parameterizedType)) {
            indexesToRemove = ArrayUtils.add(indexesToRemove, i);
         }
      }

      return indexesToRemove;
   }

   private static boolean containsVariableTypeSameParametrizedTypeBound(TypeVariable<?> typeVariable, ParameterizedType parameterizedType) {
      return ArrayUtils.contains(typeVariable.getBounds(), parameterizedType);
   }

   private static String wildcardTypeToString(WildcardType wildcardType) {
      StringBuilder buf = (new StringBuilder()).append('?');
      Type[] lowerBounds = wildcardType.getLowerBounds();
      Type[] upperBounds = wildcardType.getUpperBounds();
      if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
         appendAllTo(buf.append(" super "), " & ", lowerBounds);
      } else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
         appendAllTo(buf.append(" extends "), " & ", upperBounds);
      }

      return buf.toString();
   }

   private static String genericArrayTypeToString(GenericArrayType genericArrayType) {
      return String.format("%s[]", toString(genericArrayType.getGenericComponentType()));
   }

   private static <T> StringBuilder appendAllTo(StringBuilder builder, String sep, T... types) {
      Validate.notEmpty(Validate.noNullElements(types));
      if (types.length > 0) {
         builder.append(toString(types[0]));

         for(int i = 1; i < types.length; ++i) {
            builder.append(sep).append(toString(types[i]));
         }
      }

      return builder;
   }

   private static <T> String toString(T object) {
      return object instanceof Type ? toString((Type)object) : object.toString();
   }

   private static final class WildcardTypeImpl implements WildcardType {
      private final Type[] upperBounds;
      private final Type[] lowerBounds;

      private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
         this.upperBounds = (Type[])ObjectUtils.defaultIfNull(upperBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
         this.lowerBounds = (Type[])ObjectUtils.defaultIfNull(lowerBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
      }

      public Type[] getUpperBounds() {
         return (Type[])this.upperBounds.clone();
      }

      public Type[] getLowerBounds() {
         return (Type[])this.lowerBounds.clone();
      }

      public String toString() {
         return TypeUtils.toString((Type)this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof WildcardType && TypeUtils.equals((WildcardType)this, (Type)((WildcardType)obj));
      }

      public int hashCode() {
         int result = 18688;
         int result = result | Arrays.hashCode(this.upperBounds);
         result <<= 8;
         result |= Arrays.hashCode(this.lowerBounds);
         return result;
      }

      // $FF: synthetic method
      WildcardTypeImpl(Type[] x0, Type[] x1, Object x2) {
         this(x0, x1);
      }
   }

   private static final class ParameterizedTypeImpl implements ParameterizedType {
      private final Class<?> raw;
      private final Type useOwner;
      private final Type[] typeArguments;

      private ParameterizedTypeImpl(Class<?> rawClass, Type useOwner, Type[] typeArguments) {
         this.raw = rawClass;
         this.useOwner = useOwner;
         this.typeArguments = (Type[])Arrays.copyOf(typeArguments, typeArguments.length, Type[].class);
      }

      public Type getRawType() {
         return this.raw;
      }

      public Type getOwnerType() {
         return this.useOwner;
      }

      public Type[] getActualTypeArguments() {
         return (Type[])this.typeArguments.clone();
      }

      public String toString() {
         return TypeUtils.toString((Type)this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof ParameterizedType && TypeUtils.equals((ParameterizedType)this, (Type)((ParameterizedType)obj));
      }

      public int hashCode() {
         int result = 1136;
         int result = result | this.raw.hashCode();
         result <<= 4;
         result |= Objects.hashCode(this.useOwner);
         result <<= 8;
         result |= Arrays.hashCode(this.typeArguments);
         return result;
      }

      // $FF: synthetic method
      ParameterizedTypeImpl(Class x0, Type x1, Type[] x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType {
      private final Type componentType;

      private GenericArrayTypeImpl(Type componentType) {
         this.componentType = componentType;
      }

      public Type getGenericComponentType() {
         return this.componentType;
      }

      public String toString() {
         return TypeUtils.toString((Type)this);
      }

      public boolean equals(Object obj) {
         return obj == this || obj instanceof GenericArrayType && TypeUtils.equals((GenericArrayType)this, (Type)((GenericArrayType)obj));
      }

      public int hashCode() {
         int result = 1072;
         int result = result | this.componentType.hashCode();
         return result;
      }

      // $FF: synthetic method
      GenericArrayTypeImpl(Type x0, Object x1) {
         this(x0);
      }
   }

   public static class WildcardTypeBuilder implements Builder<WildcardType> {
      private Type[] upperBounds;
      private Type[] lowerBounds;

      private WildcardTypeBuilder() {
      }

      public TypeUtils.WildcardTypeBuilder withUpperBounds(Type... bounds) {
         this.upperBounds = bounds;
         return this;
      }

      public TypeUtils.WildcardTypeBuilder withLowerBounds(Type... bounds) {
         this.lowerBounds = bounds;
         return this;
      }

      public WildcardType build() {
         return new TypeUtils.WildcardTypeImpl(this.upperBounds, this.lowerBounds);
      }

      // $FF: synthetic method
      WildcardTypeBuilder(Object x0) {
         this();
      }
   }
}
