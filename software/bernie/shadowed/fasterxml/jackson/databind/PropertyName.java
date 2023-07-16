package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.io.SerializedString;
import software.bernie.shadowed.fasterxml.jackson.core.util.InternCache;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class PropertyName implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final String _USE_DEFAULT = "";
   private static final String _NO_NAME = "";
   public static final PropertyName USE_DEFAULT = new PropertyName("", (String)null);
   public static final PropertyName NO_NAME = new PropertyName(new String(""), (String)null);
   protected final String _simpleName;
   protected final String _namespace;
   protected SerializableString _encodedSimple;

   public PropertyName(String simpleName) {
      this(simpleName, (String)null);
   }

   public PropertyName(String simpleName, String namespace) {
      this._simpleName = ClassUtil.nonNullString(simpleName);
      this._namespace = namespace;
   }

   protected Object readResolve() {
      return this._namespace != null || this._simpleName != null && !"".equals(this._simpleName) ? this : USE_DEFAULT;
   }

   public static PropertyName construct(String simpleName) {
      return simpleName != null && simpleName.length() != 0 ? new PropertyName(InternCache.instance.intern(simpleName), (String)null) : USE_DEFAULT;
   }

   public static PropertyName construct(String simpleName, String ns) {
      if (simpleName == null) {
         simpleName = "";
      }

      return ns == null && simpleName.length() == 0 ? USE_DEFAULT : new PropertyName(InternCache.instance.intern(simpleName), ns);
   }

   public PropertyName internSimpleName() {
      if (this._simpleName.length() == 0) {
         return this;
      } else {
         String interned = InternCache.instance.intern(this._simpleName);
         return interned == this._simpleName ? this : new PropertyName(interned, this._namespace);
      }
   }

   public PropertyName withSimpleName(String simpleName) {
      if (simpleName == null) {
         simpleName = "";
      }

      return simpleName.equals(this._simpleName) ? this : new PropertyName(simpleName, this._namespace);
   }

   public PropertyName withNamespace(String ns) {
      if (ns == null) {
         if (this._namespace == null) {
            return this;
         }
      } else if (ns.equals(this._namespace)) {
         return this;
      }

      return new PropertyName(this._simpleName, ns);
   }

   public String getSimpleName() {
      return this._simpleName;
   }

   public SerializableString simpleAsEncoded(MapperConfig<?> config) {
      SerializableString sstr = this._encodedSimple;
      if (sstr == null) {
         if (config == null) {
            sstr = new SerializedString(this._simpleName);
         } else {
            sstr = config.compileString(this._simpleName);
         }

         this._encodedSimple = (SerializableString)sstr;
      }

      return (SerializableString)sstr;
   }

   public String getNamespace() {
      return this._namespace;
   }

   public boolean hasSimpleName() {
      return this._simpleName.length() > 0;
   }

   public boolean hasSimpleName(String str) {
      return this._simpleName.equals(str);
   }

   public boolean hasNamespace() {
      return this._namespace != null;
   }

   public boolean isEmpty() {
      return this._namespace == null && this._simpleName.isEmpty();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         PropertyName other = (PropertyName)o;
         if (this._simpleName == null) {
            if (other._simpleName != null) {
               return false;
            }
         } else if (!this._simpleName.equals(other._simpleName)) {
            return false;
         }

         if (this._namespace == null) {
            return null == other._namespace;
         } else {
            return this._namespace.equals(other._namespace);
         }
      }
   }

   public int hashCode() {
      return this._namespace == null ? this._simpleName.hashCode() : this._namespace.hashCode() ^ this._simpleName.hashCode();
   }

   public String toString() {
      return this._namespace == null ? this._simpleName : "{" + this._namespace + "}" + this._simpleName;
   }
}
