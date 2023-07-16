package org.apache.commons.lang3.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionDiffBuilder<T> implements Builder<DiffResult<T>> {
   private final Object left;
   private final Object right;
   private final DiffBuilder<T> diffBuilder;

   public ReflectionDiffBuilder(T lhs, T rhs, ToStringStyle style) {
      this.left = lhs;
      this.right = rhs;
      this.diffBuilder = new DiffBuilder(lhs, rhs, style);
   }

   public DiffResult<T> build() {
      if (this.left.equals(this.right)) {
         return this.diffBuilder.build();
      } else {
         this.appendFields(this.left.getClass());
         return this.diffBuilder.build();
      }
   }

   private void appendFields(Class<?> clazz) {
      Field[] var2 = FieldUtils.getAllFields(clazz);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Field field = var2[var4];
         if (this.accept(field)) {
            try {
               this.diffBuilder.append(field.getName(), FieldUtils.readField(field, this.left, true), FieldUtils.readField(field, this.right, true));
            } catch (IllegalAccessException var7) {
               throw new InternalError("Unexpected IllegalAccessException: " + var7.getMessage());
            }
         }
      }

   }

   private boolean accept(Field field) {
      if (field.getName().indexOf(36) != -1) {
         return false;
      } else if (Modifier.isTransient(field.getModifiers())) {
         return false;
      } else {
         return !Modifier.isStatic(field.getModifiers());
      }
   }
}
