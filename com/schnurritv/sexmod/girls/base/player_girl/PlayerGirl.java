package com.schnurritv.sexmod.girls.base.player_girl;

import com.google.common.base.Optional;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.SexPrompt;
import com.schnurritv.sexmod.gender_change.SexPromptManager;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import javax.vecmath.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PlayerGirl extends Fighter {
   public static final float SERVER_HEIGHT_DIFFERENCE = 255.0F;
   public static boolean ALLOW_FLYING = true;
   public Vector2f movementVector;
   public boolean isSneaking;
   public boolean isSprinting;
   public boolean isRiding;
   public boolean field_70122_E;
   public boolean isUsingItem;
   protected static final DataParameter<Optional<UUID>> OWNER;
   public static Hashtable<UUID, PlayerGirl> playerGirlTable;
   public static List<PlayerGirl> toBePutInTable;
   public boolean senderIsMale;

   protected PlayerGirl(World worldIn) {
      super(worldIn);
      this.movementVector = new Vector2f(0.0F, 0.0F);
      this.isSneaking = false;
      this.isSprinting = false;
      this.isRiding = false;
      this.field_70122_E = true;
      this.isUsingItem = false;
      this.senderIsMale = true;
      this.func_70105_a(0.1F, 0.1F);
      toBePutInTable.add(this);
      if (worldIn.field_72995_K) {
         this.setUpControllers();
      }

   }

   protected PlayerGirl(World worldIn, UUID player) {
      this(worldIn);
      this.field_70180_af.func_187227_b(OWNER, Optional.of(player));
   }

   public abstract float getNameTagHeight();

   public boolean hasBedAnimation() {
      return true;
   }

   protected String getGirlName() {
      if (((Optional)this.field_70180_af.func_187225_a(OWNER)).isPresent()) {
         EntityPlayer player = this.field_70170_p.func_152378_a((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get());
         if (player != null) {
            return player.func_70005_c_();
         }
      }

      return "anonymous horny girl";
   }

   public void startBedAnimation() {
   }

   public abstract void startStandingSex(String var1, UUID var2);

   public boolean func_70104_M() {
      return false;
   }

   public boolean func_70058_J() {
      return true;
   }

   public void setHandActive() {
      this.field_70180_af.func_187227_b(field_184621_as, Byte.valueOf("1"));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(OWNER, Optional.absent());
   }

   protected void allowOwnerFlying(boolean allow) {
      if (ALLOW_FLYING) {
         if (this.getOwner() != null) {
            EntityPlayer player = this.field_70170_p.func_152378_a(this.getOwner());
            if (player != null) {
               System.out.println(allow);
               player.field_71075_bZ.field_75101_c = allow;
               if (!allow) {
                  player.field_71075_bZ.field_75100_b = false;
               }

               player.func_71016_p();
            }
         }
      }
   }

   public static boolean isPlayerGirl(UUID player) {
      Iterator var1 = girlEntities.iterator();

      while(var1.hasNext()) {
         GirlEntity entity = (GirlEntity)var1.next();
         if (entity instanceof PlayerGirl) {
            PlayerGirl girl = (PlayerGirl)entity;
            if (player.equals(girl.getOwner())) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isPlayerGirl(EntityPlayer player) {
      return isPlayerGirl(player.getPersistentID());
   }

   protected EntityPlayer getClosestPlayer() {
      List<EntityPlayer> players = this.field_70170_p.field_73010_i;
      EntityPlayer closestPlayer = null;
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         EntityPlayer player = (EntityPlayer)var3.next();
         if (!player.getPersistentID().equals(((Optional)this.field_70180_af.func_187225_a(OWNER)).get())) {
            if (closestPlayer == null) {
               closestPlayer = player;
            } else {
               double closestDist = closestPlayer.func_70092_e(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c);
               double playerDist = player.func_70092_e(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c);
               if (playerDist < closestDist) {
                  closestPlayer = player;
               }
            }
         }
      }

      return closestPlayer;
   }

   public Vec3d getRenderPos() {
      return new Vec3d(this.field_70165_t, this.field_70163_u - 255.0D, this.field_70161_v);
   }

   protected void prepareAction(UUID male) {
      EntityPlayerMP malePlayer = (EntityPlayerMP)this.field_70170_p.func_152378_a(male);
      EntityPlayerMP girlPlayer = (EntityPlayerMP)this.field_70170_p.func_152378_a((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get());
      PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), malePlayer);
      PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), girlPlayer);
      this.setPlayer(male);
      this.field_70177_z = 0.0F;
      this.field_70759_as = 0.0F;
      malePlayer.field_70177_z = 180.0F;
      malePlayer.field_70759_as = 180.0F;
      malePlayer.func_189654_d(true);
      malePlayer.field_70145_X = true;
      malePlayer.func_70634_a(this.field_70165_t, this.getRenderPos().field_72448_b, this.field_70161_v + 1.0D);
      malePlayer.field_71075_bZ.field_75100_b = true;
      girlPlayer.field_71075_bZ.field_75100_b = true;
      this.TurnPlayerIntoCamera(male);
      this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
      this.setTargetPos(this.func_174791_d());
      this.setTargetYaw(0.0F);
   }

   protected void func_180429_a(BlockPos pos, Block blockIn) {
      super.func_180429_a(pos, blockIn);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         if (this.isOwner()) {
            SexPromptManager.INSTANCE.tick();
         }

      }
   }

   @SideOnly(Side.CLIENT)
   protected boolean isOwner() {
      return !((Optional)this.field_70180_af.func_187225_a(OWNER)).isPresent() ? false : ((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get()).equals(Minecraft.func_71410_x().field_71439_g.getPersistentID());
   }

   public void func_70619_bc() {
      this.field_70145_X = true;
      this.func_189654_d(true);
      EntityPlayer player = this.field_70170_p.func_152378_a((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get());
      if (player == null) {
         this.func_70634_a(this.field_70165_t, 255.0D, this.field_70161_v);
      } else {
         this.func_70634_a(player.field_70165_t, player.field_70163_u + 255.0D, player.field_70161_v);
      }

      tryPuttingGirlsInTable();
   }

   void updateArmor(EntityPlayer player) {
      this.field_70180_af.func_187227_b(HELMET, ItemStack.field_190927_a);
      this.field_70180_af.func_187227_b(CHEST_PLATE, ItemStack.field_190927_a);
      this.field_70180_af.func_187227_b(PANTS, ItemStack.field_190927_a);
      this.field_70180_af.func_187227_b(SHOES, ItemStack.field_190927_a);
      Iterator var2 = player.func_184193_aE().iterator();

      while(var2.hasNext()) {
         ItemStack stack = (ItemStack)var2.next();
         if (stack.func_77973_b() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)stack.func_77973_b();
            switch(item.func_185083_B_()) {
            case HEAD:
               this.field_70180_af.func_187227_b(HELMET, stack);
               break;
            case CHEST:
               this.field_70180_af.func_187227_b(CHEST_PLATE, stack);
               break;
            case LEGS:
               this.field_70180_af.func_187227_b(PANTS, stack);
               break;
            case FEET:
               this.field_70180_af.func_187227_b(SHOES, stack);
            }
         }
      }

   }

   public UUID getOwner() {
      return ((Optional)this.field_70180_af.func_187225_a(OWNER)).isPresent() ? (UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get() : null;
   }

   public void setOwner(Optional<UUID> owner) {
      this.field_70180_af.func_187227_b(OWNER, owner);
   }

   public void onDeletion() {
   }

   public void onCreation() {
   }

   public static void tryPuttingGirlsInTable() {
      List<PlayerGirl> toBeRemoved = new ArrayList();
      Iterator var1 = toBePutInTable.iterator();

      PlayerGirl girl;
      while(var1.hasNext()) {
         girl = (PlayerGirl)var1.next();
         if (girl.getOwner() != null) {
            playerGirlTable.put(girl.getOwner(), girl);
            toBeRemoved.add(girl);
         }
      }

      var1 = toBeRemoved.iterator();

      while(var1.hasNext()) {
         girl = (PlayerGirl)var1.next();
         toBePutInTable.remove(girl);
      }

      updateDead();
   }

   static void updateDead() {
      List<UUID> toBeRemoved = new ArrayList();
      Iterator var1 = playerGirlTable.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<UUID, PlayerGirl> entry = (Entry)var1.next();
         if (((PlayerGirl)entry.getValue()).field_70128_L) {
            toBeRemoved.add(entry.getKey());
         }
      }

      var1 = toBeRemoved.iterator();

      while(var1.hasNext()) {
         UUID girl = (UUID)var1.next();
         playerGirlTable.remove(girl);
      }

   }

   protected boolean isGirlPlayer(UUID player) {
      if (player == null) {
         return false;
      } else {
         PlayerGirl girl = (PlayerGirl)playerGirlTable.get(player);
         return girl != null;
      }
   }

   public void doAction(String actionName, UUID male) {
      if (((Optional)this.field_70180_af.func_187225_a(OWNER)).isPresent()) {
         PacketHandler.INSTANCE.sendToServer(new SexPrompt(actionName, male, (UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get(), this.senderIsMale));
         if (!this.senderIsMale) {
            Minecraft.func_71410_x().field_71474_y.field_74320_O = 1;
         }

         this.senderIsMale = true;
      }
   }

   public void func_70014_b(NBTTagCompound compound) {
      super.func_70014_b(compound);
      compound.func_74778_a("owner", ((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get()).toString());
   }

   public void func_70037_a(NBTTagCompound compound) {
      super.func_70037_a(compound);
      this.field_70180_af.func_187227_b(OWNER, Optional.of(UUID.fromString(compound.func_74779_i("owner"))));
      toBePutInTable.add(this);
   }

   public void playSoundAroundHer(SoundEvent sound) {
      if (this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_184134_a((double)this.func_180425_c().func_177958_n(), this.getRenderPos().field_72448_b, (double)this.func_180425_c().func_177952_p(), sound, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
      } else {
         this.field_70170_p.func_184133_a((EntityPlayer)null, new BlockPos(this.getRenderPos()), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
      }

   }

   public void playSoundAroundHer(SoundEvent sound, float volume) {
      if (this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_184134_a((double)this.func_180425_c().func_177958_n(), this.getRenderPos().field_72448_b, (double)this.func_180425_c().func_177952_p(), sound, SoundCategory.NEUTRAL, volume, 1.0F, false);
      } else {
         this.field_70170_p.func_184133_a((EntityPlayer)null, new BlockPos(this.getRenderPos()), sound, SoundCategory.PLAYERS, volume, 1.0F);
      }

   }

   protected void checkFollowUp() {
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return true;
   }

   static {
      OWNER = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187203_m).func_187156_b().func_187161_a(54);
      playerGirlTable = new Hashtable();
      toBePutInTable = new ArrayList();
   }
}
