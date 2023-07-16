package com.schnurritv.sexmod.girls.bee;

import com.schnurritv.sexmod.girls.base.Hand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class BeeHand extends ModelBase implements Hand {
   private final ModelRenderer bb_main;

   public BeeHand() {
      this.field_78090_t = 16;
      this.field_78089_u = 16;
      this.bb_main = new ModelRenderer(this);
      this.bb_main.func_78793_a(-5.0F, 2.5F, 0.0F);
      this.bb_main.field_78804_l.add(new ModelBox(this.bb_main, 0, 0, -2.0F, -6.0F, 0.0F, 2, 6, 2, 0.0F, false));
   }

   public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.bb_main.func_78785_a(f5);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }

   public ModelRenderer getHandRenderer() {
      return this.bb_main;
   }
}
