package com.schnurritv.sexmod.companion;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Reference;
import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public abstract class CompanionBase extends EntityAIBase {
   public GirlEntity girl;
   public EntityPlayer master;
   public PathNavigate navigator;
   public EntityDataManager dataManager;
   public CompanionBase.Mode currentMode;
   public static final double WALK_SPEED = 0.5D;
   public static final double RUN_SPEED = 0.7D;
   public static final int MODE_SWITCH_COOLDOWN = 60;

   public CompanionBase(GirlEntity girl) {
      this.currentMode = CompanionBase.Mode.IDLE;
      this.girl = girl;
      this.navigator = girl.func_70661_as();
      this.dataManager = girl.func_184212_Q();
   }

   protected void tpToPlayer() {
      int trys = 0;

      BlockPos tpTo;
      do {
         tpTo = this.master.func_180425_c().func_177982_a(Reference.RANDOM.nextInt(10), 0, Reference.RANDOM.nextInt(10));
         ++trys;
      } while(trys < 20 && !this.girl.func_184595_k((double)tpTo.func_177958_n(), (double)tpTo.func_177956_o(), (double)tpTo.func_177952_p()));

      if (trys >= 20) {
         this.girl.func_70107_b(this.master.field_70165_t, this.master.field_70163_u, this.master.field_70161_v);
      }

      this.girl.field_70159_w = 0.0D;
      this.girl.field_70181_x = 0.0D;
      this.girl.field_70179_y = 0.0D;
   }

   protected double setMovementSpeed() {
      float distance = this.girl.func_70032_d(this.master);
      double speed;
      GirlEntity.WalkSpeed walkSpeed;
      if (this.master.func_70051_ag()) {
         speed = 0.7D;
         walkSpeed = GirlEntity.WalkSpeed.RUN;
      } else {
         speed = 0.5D;
         walkSpeed = GirlEntity.WalkSpeed.WALK;
      }

      double extraSpeed = Math.floor((double)(distance / 5.0F)) * 0.2D;
      speed += extraSpeed;
      if (this.girl.func_70090_H()) {
         speed *= 60.0D;
         walkSpeed = GirlEntity.WalkSpeed.WALK;
      }

      this.navigator.func_75489_a(speed);
      this.girl.setWalkSpeed(walkSpeed);
      return speed;
   }

   public void func_75251_c() {
      this.navigator.func_75499_g();
      this.currentMode = CompanionBase.Mode.IDLE;
      this.girl.setCurrentAction(GirlEntity.Action.NULL);
      this.dataManager.func_187227_b(GirlEntity.MASTER, "");
      this.navigator = null;
      this.dataManager = null;
      this.master = null;
   }

   public boolean func_75250_a() {
      return !((String)this.girl.func_184212_Q().func_187225_a(GirlEntity.MASTER)).equals("");
   }

   public boolean func_75253_b() {
      String uuidString = (String)this.dataManager.func_187225_a(GirlEntity.MASTER);
      return !uuidString.equals("") && this.girl.field_70170_p.func_152378_a(UUID.fromString(uuidString)) != null;
   }

   public void func_75249_e() {
      this.navigator = this.girl.func_70661_as();
      this.dataManager = this.girl.func_184212_Q();
      this.master = this.girl.field_70170_p.func_152378_a(UUID.fromString((String)this.dataManager.func_187225_a(GirlEntity.MASTER)));
   }

   public void func_75246_d() {
      this.currentMode = this.updateMode();
      if (this.girl.aiLookAtPlayer != null) {
         this.girl.aiLookAtPlayer.shouldLook = this.currentMode == CompanionBase.Mode.IDLE;
      }

      this.evalMode(this.currentMode);
   }

   protected abstract CompanionBase.Mode updateMode();

   protected abstract void evalMode(CompanionBase.Mode var1);

   @SubscribeEvent
   public void noDeath(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof GirlEntity) {
         GirlEntity girl = (GirlEntity)event.getEntityLiving();
         if (!((String)girl.func_184212_Q().func_187225_a(GirlEntity.MASTER)).equals("")) {
            event.setCanceled(true);
         }
      }

   }

   public static enum Mode {
      ATTACK,
      FOLLOW,
      IDLE,
      RIDE,
      DOWNED;
   }
}
