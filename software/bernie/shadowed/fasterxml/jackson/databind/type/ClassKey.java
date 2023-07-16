package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.io.Serializable;

public final class ClassKey implements Comparable<ClassKey>, Serializable {
   private static final long serialVersionUID = 1L;
   private String _className;
   private Class<?> _class;
   private int _hashCode;

   public ClassKey() {
      this._class = null;
      this._className = null;
      this._hashCode = 0;
   }

   public ClassKey(Class<?> clz) {
      this._class = clz;
      this._className = clz.getName();
      this._hashCode = this._className.hashCode();
   }

   public void reset(Class<?> clz) {
      this._class = clz;
      this._className = clz.getName();
      this._hashCode = this._className.hashCode();
   }

   public int compareTo(ClassKey other) {
      return this._className.compareTo(other._className);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         ClassKey other = (ClassKey)o;
         return other._class == this._class;
      }
   }

   public int hashCode() {
      return this._hashCode;
   }

   public String toString() {
      return this._className;
   }
}
