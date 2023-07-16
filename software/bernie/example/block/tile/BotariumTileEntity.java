package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BotariumTileEntity extends TileEntity implements IAnimatable {
   private final AnimationFactory factory = new AnimationFactory(this);

   private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      event.getController().transitionLengthTicks = 0.0D;
      event.getController().setAnimation((new AnimationBuilder()).addAnimation("Botarium.anim.deploy", true));
      return PlayState.CONTINUE;
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "controller", 0.0F, this::predicate));
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }
}
