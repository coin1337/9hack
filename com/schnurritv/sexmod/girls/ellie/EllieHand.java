package com.schnurritv.sexmod.girls.ellie;

import com.schnurritv.sexmod.girls.base.Hand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class EllieHand extends ModelBase implements Hand {
   private final ModelRenderer bb_main;
   private final ModelRenderer bb_main_r1;
   private final ModelRenderer getHandRenderer;

   public EllieHand() {
      this.field_78090_t = 16;
      this.field_78089_u = 16;
      this.bb_main = new ModelRenderer(this);
      this.bb_main.func_78793_a(-5.0F, 1.5708F, 0.0F);
      this.bb_main_r1 = new ModelRenderer(this);
      this.bb_main_r1.func_78793_a(-1.0F, -3.0F, 1.0F);
      this.bb_main.func_78792_a(this.bb_main_r1);
      this.setRotationAngle(this.bb_main_r1, 0.0F, 1.5708F, 0.0F);
      this.bb_main_r1.field_78804_l.add(new ModelBox(this.bb_main_r1, 0, 0, -1.0F, -3.0F, -1.0F, 2, 6, 2, 0.0F, false));
      this.getHandRenderer = new ModelRenderer(this);
      this.getHandRenderer.func_78793_a(0.0F, 0.0F, 0.0F);
   }

   public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.bb_main.func_78785_a(f5);
      this.getHandRenderer.func_78785_a(f5);
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
