package software.bernie.example.entity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BikeEntity extends EntityAnimal implements IAnimatable {
   private AnimationFactory factory = new AnimationFactory(this);

   private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.bike.idle", true));
      return PlayState.CONTINUE;
   }

   public BikeEntity(World worldIn) {
      super(worldIn);
      this.field_70158_ak = true;
      this.func_70105_a(0.5F, 0.6F);
   }

   public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
      if (!this.func_184207_aI()) {
         player.func_184220_m(this);
         return super.func_184645_a(player, hand);
      } else {
         return super.func_184645_a(player, hand);
      }
   }

   protected void func_180429_a(BlockPos pos, Block blockIn) {
   }

   public void func_191986_a(float strafe, float vertical, float forward) {
      if (this.func_70089_S() && this.func_184207_aI()) {
         EntityLivingBase livingentity = (EntityLivingBase)this.func_184179_bs();
         this.field_70177_z = livingentity.field_70177_z;
         this.field_70126_B = this.field_70177_z;
         this.field_70125_A = livingentity.field_70125_A * 0.5F;
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         this.field_70761_aq = this.field_70177_z;
         this.field_70759_as = this.field_70761_aq;
         float f = livingentity.field_70702_br * 0.5F;
         float f1 = livingentity.field_191988_bg;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         this.func_70659_e(0.3F);
         super.func_191986_a(f, vertical, f1);
      }

   }

   @Nullable
   public Entity func_184179_bs() {
      return this.func_184188_bt().isEmpty() ? null : (Entity)this.func_184188_bt().get(0);
   }

   public boolean func_82171_bF() {
      return true;
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "controller", 0.0F, this::predicate));
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   @Nullable
   public EntityAgeable func_90011_a(EntityAgeable ageable) {
      return null;
   }
}
