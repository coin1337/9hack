package com.schnurritv.sexmod.girls.slime.friendlySlime;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FriendlySlimeModel extends ModelBase {
   private final ModelRenderer cube;
   private final ModelRenderer eye0;
   private final ModelRenderer eye1;
   private final ModelRenderer mouth;
   private final ModelRenderer hair;

   public FriendlySlimeModel() {
      this.field_78090_t = 64;
      this.field_78089_u = 32;
      this.cube = new ModelRenderer(this);
      this.cube.func_78793_a(0.0F, 0.0F, 0.0F);
      this.cube.field_78804_l.add(new ModelBox(this.cube, 0, 16, -3.0F, 17.0F, -3.0F, 6, 6, 6, 0.0F, true));
      this.eye0 = new ModelRenderer(this);
      this.eye0.func_78793_a(0.0F, 0.0F, 0.0F);
      this.eye0.field_78804_l.add(new ModelBox(this.eye0, 32, 0, 1.3F, 18.0F, -3.5F, 2, 2, 2, 0.0F, true));
      this.eye1 = new ModelRenderer(this);
      this.eye1.func_78793_a(0.0F, 0.0F, 0.0F);
      this.eye1.field_78804_l.add(new ModelBox(this.eye1, 32, 4, -3.3F, 18.0F, -3.5F, 2, 2, 2, 0.0F, true));
      this.mouth = new ModelRenderer(this);
      this.mouth.func_78793_a(0.0F, 0.0F, 0.0F);
      this.mouth.field_78804_l.add(new ModelBox(this.mouth, 32, 8, -1.0F, 21.0F, -3.5F, 1, 1, 1, 0.0F, true));
      this.hair = new ModelRenderer(this);
      this.hair.func_78793_a(-0.5F, 0.0F, 0.1F);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(2.0F, 20.7406F, 4.0504F);
      this.hair.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, 1.0908F, 0.0F, 0.0F);
      cube_r1.field_78804_l.add(new ModelBox(cube_r1, 10, 11, -2.5F, 0.0F, 0.0F, 2, 2, 1, 0.0F, false));
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(2.0F, 19.9214F, 3.4768F);
      this.hair.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, 0.6109F, 0.0F, 0.0F);
      cube_r2.field_78804_l.add(new ModelBox(cube_r2, 10, 11, -3.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(2.0F, 19.0074F, 3.0643F);
      this.hair.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, 0.3491F, 0.0F, 0.0F);
      cube_r3.field_78804_l.add(new ModelBox(cube_r3, 10, 11, -4.0F, 0.0F, 0.075F, 5, 1, 1, 0.0F, false));
      ModelRenderer cube_r4 = new ModelRenderer(this);
      cube_r4.func_78793_a(0.0F, 17.925F, 3.5F);
      this.hair.func_78792_a(cube_r4);
      this.setRotationAngle(cube_r4, 0.1309F, 0.0F, 0.0F);
      cube_r4.field_78804_l.add(new ModelBox(cube_r4, 10, 11, -3.0F, -1.0F, -0.5F, 7, 2, 1, 0.0F, false));
   }

   public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.cube.func_78785_a(f5);
      this.eye0.func_78785_a(f5);
      this.eye1.func_78785_a(f5);
      this.mouth.func_78785_a(f5);
      this.hair.func_78785_a(f5);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
