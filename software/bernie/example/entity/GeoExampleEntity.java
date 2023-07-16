package software.bernie.example.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeoExampleEntity extends EntityCreature implements IAnimatable, IAnimationTickable {
   private AnimationFactory factory = new AnimationFactory(this);

   private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.bat.fly", true));
      return PlayState.CONTINUE;
   }

   public GeoExampleEntity(World worldIn) {
      super(worldIn);
      this.field_70158_ak = true;
      this.func_70105_a(0.7F, 1.3F);
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "controller", 50.0F, this::predicate));
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      super.func_184651_r();
   }

   public int tickTimer() {
      return this.field_70173_aa;
   }

   public void tick() {
      super.func_70071_h_();
   }
}
