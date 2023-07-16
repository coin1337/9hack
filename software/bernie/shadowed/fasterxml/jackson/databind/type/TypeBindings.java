package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class TypeBindings implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final String[] NO_STRINGS = new String[0];
   private static final JavaType[] NO_TYPES = new JavaType[0];
   private static final TypeBindings EMPTY;
   private final String[] _names;
   private final JavaType[] _types;
   private final String[] _unboundVariables;
   private final int _hashCode;

   private TypeBindings(String[] names, JavaType[] types, String[] uvars) {
      this._names = names == null ? NO_STRINGS : names;
      this._types = types == null ? NO_TYPES : types;
      if (this._names.length != this._types.length) {
         throw new IllegalArgumentException("Mismatching names (" + this._names.length + "), types (" + this._types.length + ")");
      } else {
         int h = 1;
         int i = 0;

         for(int len = this._types.length; i < len; ++i) {
            h += this._types[i].hashCode();
         }

         this._unboundVariables = uvars;
         this._hashCode = h;
      }
   }

   public static TypeBindings emptyBindings() {
      return EMPTY;
   }

   protected Object readResolve() {
      return this._names != null && this._names.length != 0 ? this : EMPTY;
   }

   public static TypeBindings create(Class<?> erasedType, List<JavaType> typeList) {
      JavaType[] types = typeList != null && !typeList.isEmpty() ? (JavaType[])typeList.toArray(new JavaType[typeList.size()]) : NO_TYPES;
      return create(erasedType, types);
   }

   public static TypeBindings create(Class<?> erasedType, JavaType[] types) {
      if (types == null) {
         types = NO_TYPES;
      } else {
         switch(types.length) {
         case 1:
            return create(erasedType, types[0]);
         case 2:
            return create(erasedType, types[0], types[1]);
         }
      }

      TypeVariable<?>[] vars = erasedType.getTypeParameters();
      String[] names;
      if (vars != null && vars.length != 0) {
         int len = vars.length;
         names = new String[len];

         for(int i = 0; i < len; ++i) {
            names[i] = vars[i].getName();
         }
      } else {
         names = NO_STRINGS;
      }

      if (names.length != types.length) {
         throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + (types.length == 1 ? "" : "s") + ": class expects " + names.length);
      } else {
         return new TypeBindings(names, types, (String[])null);
      }
   }

   public static TypeBindings create(Class<?> erasedType, JavaType typeArg1) {
      TypeVariable<?>[] vars = TypeBindings.TypeParamStash.paramsFor1(erasedType);
      int varLen = vars == null ? 0 : vars.length;
      if (varLen != 1) {
         throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
      } else {
         return new TypeBindings(new String[]{vars[0].getName()}, new JavaType[]{typeArg1}, (String[])null);
      }
   }

   public static TypeBindings create(Class<?> erasedType, JavaType typeArg1, JavaType typeArg2) {
      TypeVariable<?>[] vars = TypeBindings.TypeParamStash.paramsFor2(erasedType);
      int varLen = vars == null ? 0 : vars.length;
      if (varLen != 2) {
         throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 2 type parameters: class expects " + varLen);
      } else {
         return new TypeBindings(new String[]{vars[0].getName(), vars[1].getName()}, new JavaType[]{typeArg1, typeArg2}, (String[])null);
      }
   }

   public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType typeArg1) {
      TypeVariable<?>[] vars = erasedType.getTypeParameters();
      int varLen = vars == null ? 0 : vars.length;
      if (varLen == 0) {
         return EMPTY;
      } else if (varLen != 1) {
         throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
      } else {
         return new TypeBindings(new String[]{vars[0].getName()}, new JavaType[]{typeArg1}, (String[])null);
      }
   }

   public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType[] types) {
      TypeVariable<?>[] vars = erasedType.getTypeParameters();
      if (vars != null && vars.length != 0) {
         if (types == null) {
            types = NO_TYPES;
         }

         int len = vars.length;
         String[] names = new String[len];

         for(int i = 0; i < len; ++i) {
            names[i] = vars[i].getName();
         }

         if (names.length != types.length) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + (types.length == 1 ? "" : "s") + ": class expects " + names.length);
         } else {
            return new TypeBindings(names, types, (String[])null);
         }
      } else {
         return EMPTY;
      }
   }

   public TypeBindings withUnboundVariable(String name) {
      int len = this._unboundVariables == null ? 0 : this._unboundVariables.length;
      String[] names = len == 0 ? new String[1] : (String[])Arrays.copyOf(this._unboundVariables, len + 1);
      names[len] = name;
      return new TypeBindings(this._names, this._types, names);
   }

   public JavaType findBoundType(String name) {
      int i = 0;

      for(int len = this._names.length; i < len; ++i) {
         if (name.equals(this._names[i])) {
            JavaType t = this._types[i];
            if (t instanceof ResolvedRecursiveType) {
               ResolvedRecursiveType rrt = (ResolvedRecursiveType)t;
               JavaType t2 = rrt.getSelfReferencedType();
               if (t2 != null) {
                  t = t2;
               }
            }

            return t;
         }
      }

      return null;
   }

   public boolean isEmpty() {
      return this._types.length == 0;
   }

   public int size() {
      return this._types.length;
   }

   public String getBoundName(int index) {
      return index >= 0 && index < this._names.length ? this._names[index] : null;
   }

   public JavaType getBoundType(int index) {
      return index >= 0 && index < this._types.length ? this._types[index] : null;
   }

   public List<JavaType> getTypeParameters() {
      return this._types.length == 0 ? Collections.emptyList() : Arrays.asList(this._types);
   }

   public boolean hasUnbound(String name) {
      if (this._unboundVariables != null) {
         int i = this._unboundVariables.length;

         while(true) {
            --i;
            if (i < 0) {
               break;
            }

            if (name.equals(this._unboundVariables[i])) {
               return true;
            }
         }
      }

      return false;
   }

   public Object asKey(Class<?> rawBase) {
      return new TypeBindings.AsKey(rawBase, this._types, this._hashCode);
   }

   public String toString() {
      if (this._types.length == 0) {
         return "<>";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('<');
         int i = 0;

         for(int len = this._types.length; i < len; ++i) {
            if (i > 0) {
               sb.append(',');
            }

            String sig = this._types[i].getGenericSignature();
            sb.append(sig);
         }

         sb.append('>');
         return sb.toString();
      }
   }

   public int hashCode() {
      return this._hashCode;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         TypeBindings other = (TypeBindings)o;
         int len = this._types.length;
         if (len != other.size()) {
            return false;
         } else {
            JavaType[] otherTypes = other._types;

            for(int i = 0; i < len; ++i) {
               if (!otherTypes[i].equals(this._types[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   protected JavaType[] typeParameterArray() {
      return this._types;
   }

   static {
      EMPTY = new TypeBindings(NO_STRINGS, NO_TYPES, (String[])null);
   }

   static final class AsKey {
      private final Class<?> _raw;
      private final JavaType[] _params;
      private final int _hash;

      public AsKey(Class<?> raw, JavaType[] params, int hash) {
         this._raw = raw;
         this._params = params;
         this._hash = hash;
      }

      public int hashCode() {
         return this._hash;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            TypeBindings.AsKey other = (TypeBindings.AsKey)o;
            if (this._hash == other._hash && this._raw == other._raw) {
               JavaType[] otherParams = other._params;
               int len = this._params.length;
               if (len == otherParams.length) {
                  for(int i = 0; i < len; ++i) {
                     if (!this._params[i].equals(otherParams[i])) {
                        return false;
                     }
                  }

                  return true;
               }
            }

            return false;
         }
      }

      public String toString() {
         return this._raw.getName() + "<>";
      }
   }

   static class TypeParamStash {
      private static final TypeVariable<?>[] VARS_ABSTRACT_LIST = AbstractList.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_COLLECTION = Collection.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_ITERABLE = Iterable.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_LIST = List.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_ARRAY_LIST = ArrayList.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_MAP = Map.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_HASH_MAP = HashMap.class.getTypeParameters();
      private static final TypeVariable<?>[] VARS_LINKED_HASH_MAP = LinkedHashMap.class.getTypeParameters();

      public static TypeVariable<?>[] paramsFor1(Class<?> erasedType) {
         if (erasedType == Collection.class) {
            return VARS_COLLECTION;
         } else if (erasedType == List.class) {
            return VARS_LIST;
         } else if (erasedType == ArrayList.class) {
            return VARS_ARRAY_LIST;
         } else if (erasedType == AbstractList.class) {
            return VARS_ABSTRACT_LIST;
         } else {
            return erasedType == Iterable.class ? VARS_ITERABLE : erasedType.getTypeParameters();
         }
      }

      public static TypeVariable<?>[] paramsFor2(Class<?> erasedType) {
         if (erasedType == Map.class) {
            return VARS_MAP;
         } else if (erasedType == HashMap.class) {
            return VARS_HASH_MAP;
         } else {
            return erasedType == LinkedHashMap.class ? VARS_LINKED_HASH_MAP : erasedType.getTypeParameters();
         }
      }
   }
}
