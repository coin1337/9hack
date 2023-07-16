package com.schnurritv.sexmod.companion.supporter;

import com.schnurritv.sexmod.companion.CompanionBase;
import com.schnurritv.sexmod.girls.base.GirlEntity;

public class SupporterCompanion extends CompanionBase {
   int shouldntFollowAnymoreTick = 0;
   int idlePosChangeTick = 0;

   public SupporterCompanion(GirlEntity girl) {
      super(girl);
   }

   public void func_75251_c() {
      super.func_75251_c();
      this.girl.field_70747_aH = 0.02F;
   }

   protected CompanionBase.Mode updateMode() {
      float distance = this.girl.func_70032_d(this.master);
      boolean shouldFollow = distance > 5.0F;
      if (this.girl.playerSheHasSexWith() == null && !shouldFollow && this.currentMode == CompanionBase.Mode.FOLLOW) {
         if (++this.shouldntFollowAnymoreTick > 60) {
            shouldFollow = false;
            this.shouldntFollowAnymoreTick = 0;
         } else {
            shouldFollow = true;
         }
      }

      return shouldFollow ? CompanionBase.Mode.FOLLOW : CompanionBase.Mode.IDLE;
   }

   protected void evalMode(CompanionBase.Mode mode) {
      switch(mode) {
      case FOLLOW:
         double distance = (double)this.girl.func_70032_d(this.master);
         if ((double)this.navigator.func_111269_d() > distance) {
            this.navigator.func_75499_g();
            this.navigator.func_75497_a(this.master, 0.5D);
         } else {
            this.tpToPlayer();
         }

         this.idlePosChangeTick = 300;
         this.setMovementSpeed();
         break;
      case IDLE:
         this.setMovementSpeed();
      }

   }

   protected double setMovementSpeed() {
      float distance = this.girl.func_70032_d(this.master);
      float speed = 0.02F;
      double extraSpeed = Math.min(0.7D, Math.floor((double)(distance / 3.0F)) * 0.05D);
      speed = (float)((double)speed + extraSpeed);
      this.girl.field_70747_aH = speed;
      return (double)speed;
   }
}
