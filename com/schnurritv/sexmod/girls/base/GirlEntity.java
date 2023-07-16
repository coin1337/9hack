package com.schnurritv.sexmod.girls.base;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ChangeDataParameter;
import com.schnurritv.sexmod.Packets.PrepareAction;
import com.schnurritv.sexmod.Packets.ResetGirl;
import com.schnurritv.sexmod.Packets.SendChatMessage;
import com.schnurritv.sexmod.Packets.TeleportPlayer;
import com.schnurritv.sexmod.companion.OpenAndCloseDoorBehindHer;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import com.schnurritv.sexmod.gui.menu.FighterUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.LootTableHandler;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class GirlEntity extends EntityCreature implements IAnimatable {
   private final AnimationFactory factory = new AnimationFactory(this);
   public EntityAIWanderAvoidWater aiWander;
   public LookAtNearbyEntity aiLookAtPlayer;
   public static ArrayList<GirlEntity> girlEntities = new ArrayList();
   public boolean shouldBeAtTargetYaw = false;
   public Vec3d playerCamPos;
   protected float playerYaw;
   protected EntityDataManager field_70180_af;
   protected PathNavigate field_70699_by;
   public Vec3d home;
   public EntityEnderPearl pearl;
   public float itemRendererSize;
   public boolean playerIsThrusting;
   public boolean playerIsCumming;
   public static final DataParameter<String> MASTER;
   public static final DataParameter<Boolean> SHOULD_BE_AT_TARGET;
   public static final DataParameter<String> TARGET_POS;
   public static final DataParameter<Float> TARGET_YAW;
   public static final DataParameter<String> GIRL_ID;
   public static final DataParameter<Integer> CURRENT_MODEL;
   public static final DataParameter<String> CURRENT_ACTION;
   public static final DataParameter<String> ANIMATION_FOLLOW_UP;
   public static final DataParameter<String> PLAYER_SHE_HAS_SEX_WITH;
   public static final DataParameter<String> WALK_SPEED;
   protected static final List<Item> TEMPTATION_ITEMS;
   public AnimationController actionController;
   public AnimationController movementController;
   public AnimationController eyesController;
   public String currentAnimationPath;
   public boolean currentAnimationLoop;

   public void setWalkSpeed(GirlEntity.WalkSpeed walkSpeed) {
      this.field_70180_af.func_187227_b(WALK_SPEED, walkSpeed.toString());
   }

   public GirlEntity.WalkSpeed getWalkSpeed() {
      return GirlEntity.WalkSpeed.valueOf((String)this.field_70180_af.func_187225_a(WALK_SPEED));
   }

   @SideOnly(Side.CLIENT)
   protected void changeDataParameterFromClient(String parameter, String value) {
      PacketHandler.INSTANCE.sendToServer(new ChangeDataParameter(this.girlId(), parameter, value));
   }

   public UUID girlId() {
      return UUID.fromString((String)this.field_70180_af.func_187225_a(GIRL_ID));
   }

   public GirlEntity.Action currentAction() {
      return GirlEntity.Action.valueOf((String)this.field_70180_af.func_187225_a(CURRENT_ACTION));
   }

   public void setCurrentAction(GirlEntity.Action newAction) {
      newAction = newAction == null ? GirlEntity.Action.NULL : newAction;
      if (this.field_70170_p.field_72995_K) {
         this.changeDataParameterFromClient("currentAction", newAction.toString());
      }

      this.field_70180_af.func_187227_b(CURRENT_ACTION, newAction.toString());
   }

   public UUID playerSheHasSexWith() {
      String uuidString = (String)this.field_70180_af.func_187225_a(PLAYER_SHE_HAS_SEX_WITH);
      return uuidString.equals("null") ? null : UUID.fromString(uuidString);
   }

   public void setPlayer(UUID player) {
      if (this.field_70170_p.field_72995_K) {
         this.changeDataParameterFromClient("playerSheHasSexWith", player.toString());
      } else {
         if (player == null) {
            this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, "null");
         } else {
            this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, player.toString());
         }

      }
   }

   public Vec3d targetPos() {
      String[] pos = ((String)this.field_70180_af.func_187225_a(TARGET_POS)).split("\\|");
      return new Vec3d(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
   }

   public void setTargetPos(Vec3d pos) {
      this.field_70180_af.func_187227_b(TARGET_POS, pos.field_72450_a + "|" + pos.field_72448_b + "|" + pos.field_72449_c);
   }

   public Float targetYaw() {
      return (Float)this.field_70180_af.func_187225_a(TARGET_YAW);
   }

   public void setTargetYaw(float yaw) {
      this.field_70180_af.func_187227_b(TARGET_YAW, yaw);
   }

   @SideOnly(Side.CLIENT)
   protected void setUpControllers() {
      this.actionController = new AnimationController(this, "action", 0.0F, this::predicate);
      this.movementController = new AnimationController(this, "movement", 5.0F, this::predicate);
      this.eyesController = new AnimationController(this, "eyes", 10.0F, this::predicate);
   }

   protected boolean func_70692_ba() {
      return false;
   }

   public abstract boolean canCloseUiWithoutHavingChosen();

   protected GirlEntity(World worldIn) {
      super(worldIn);
      this.home = Vec3d.field_186680_a;
      this.itemRendererSize = 1.0F;
      this.playerIsThrusting = false;
      this.playerIsCumming = false;
      if (!worldIn.field_72995_K || !(worldIn instanceof FakeWorld)) {
         girlEntities.add(this);
         if (!(this instanceof BeeEntity)) {
            ((PathNavigateGround)this.func_70661_as()).func_179688_b(true);
         }
      }

      if (worldIn.field_72995_K) {
         this.setUpControllers();
      }

   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70699_by = this.func_70661_as();
      this.field_70180_af = this.func_184212_Q();
      this.field_70180_af.func_187214_a(GIRL_ID, UUID.randomUUID().toString());
      this.field_70180_af.func_187214_a(CURRENT_MODEL, 1);
      this.field_70180_af.func_187214_a(CURRENT_ACTION, GirlEntity.Action.NULL.toString());
      this.field_70180_af.func_187214_a(ANIMATION_FOLLOW_UP, "");
      this.field_70180_af.func_187214_a(PLAYER_SHE_HAS_SEX_WITH, "null");
      this.field_70180_af.func_187214_a(SHOULD_BE_AT_TARGET, false);
      this.field_70180_af.func_187214_a(TARGET_YAW, 0.0F);
      this.field_70180_af.func_187214_a(TARGET_POS, "0|0|0");
      this.field_70180_af.func_187214_a(MASTER, "");
      this.field_70180_af.func_187214_a(WALK_SPEED, GirlEntity.WalkSpeed.WALK.toString());
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(30.0D);
   }

   protected void func_184651_r() {
      Reference.server = this.func_184102_h();
      this.aiWander = new EntityAIWanderAvoidWater(this, 0.35D);
      this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAITempt(this, 0.4D, false, new HashSet(TEMPTATION_ITEMS)));
      this.field_70714_bg.func_75776_a(3, new OpenAndCloseDoorBehindHer(this));
      this.field_70714_bg.func_75776_a(5, this.aiLookAtPlayer);
      this.field_70714_bg.func_75776_a(5, this.aiWander);
   }

   public void func_70014_b(NBTTagCompound compound) {
      compound.func_74780_a("homeX", this.home.field_72450_a);
      compound.func_74780_a("homeY", this.home.field_72448_b);
      compound.func_74780_a("homeZ", this.home.field_72449_c);
      super.func_70014_b(compound);
   }

   public void func_70037_a(NBTTagCompound compound) {
      super.func_70037_a(compound);
      this.home = new Vec3d(compound.func_74769_h("homeX"), compound.func_74769_h("homeY"), compound.func_74769_h("homeZ"));
   }

   public void func_70619_bc() {
      if ((Boolean)this.field_70180_af.func_187225_a(SHOULD_BE_AT_TARGET)) {
         this.func_70034_d(this.targetYaw());
         this.func_70080_a(this.targetPos().field_72450_a, this.targetPos().field_72448_b, this.targetPos().field_72449_c, this.targetYaw(), 0.0F);
         this.func_70101_b(this.targetYaw(), this.field_70125_A);
      } else if (this.shouldBeAtTargetYaw) {
         this.func_70034_d(this.targetYaw());
         this.func_70101_b(this.targetYaw(), this.field_70125_A);
      }

      if (this.home.equals(Vec3d.field_186680_a)) {
         this.home = new Vec3d(this.func_180425_c());
      }

   }

   public void onReset() {
   }

   @SideOnly(Side.CLIENT)
   public abstract boolean openMenu(EntityPlayer var1);

   @SideOnly(Side.CLIENT)
   protected static void renderMenu(EntityPlayer player, GirlEntity girl, String[] animation, ItemStack[] price, boolean shouldDrawEquipment) {
      Minecraft.func_71410_x().func_147108_a(new FighterUI(girl, player, animation, price, shouldDrawEquipment));
   }

   @SideOnly(Side.CLIENT)
   protected static void renderMenu(EntityPlayer player, GirlEntity girl, String[] animation, boolean shouldDrawEquipment) {
      Minecraft.func_71410_x().func_147108_a(new FighterUI(girl, player, animation, (ItemStack[])null, shouldDrawEquipment));
   }

   public void setActiveItemStack(ItemStack stack) {
      this.field_184627_bm = stack;
   }

   public void setActiveStackUse(int use) {
      this.field_184628_bn = use;
   }

   public Vec3d prevPos() {
      return new Vec3d(this.field_70169_q, this.field_70167_r, this.field_70166_s);
   }

   protected static Vec3d prevPos(GirlEntity entity) {
      return new Vec3d(entity.field_70169_q, entity.field_70167_r, entity.field_70166_s);
   }

   public GirlEntity getGirl() {
      return this;
   }

   public void stopCompanionShip() {
      if (this.field_70170_p.field_72995_K) {
         this.changeDataParameterFromClient("master", "");
         this.changeDataParameterFromClient("walk speed", GirlEntity.WalkSpeed.WALK.toString());
      } else {
         this.field_70180_af.func_187227_b(MASTER, "");
         this.field_70180_af.func_187227_b(WALK_SPEED, GirlEntity.WalkSpeed.WALK.toString());
      }

   }

   protected void TurnPlayerIntoCamera(EntityPlayerMP player, boolean autoMoveCamera) {
      player.field_70159_w = 0.0D;
      player.field_70181_x = 0.0D;
      player.field_70179_y = 0.0D;
      if (autoMoveCamera) {
         Vec3d forward = this.getInFrontOfPlayer(0.35D);
         player.func_70634_a(forward.field_72450_a, forward.field_72448_b, forward.field_72449_c);
      }

   }

   public void TurnPlayerIntoCamera(UUID playerID) {
      EntityPlayer player = this.field_70170_p.func_152378_a(playerID);
      player.field_70159_w = 0.0D;
      player.field_70181_x = 0.0D;
      player.field_70179_y = 0.0D;
      Vec3d forward = this.getInFrontOfPlayer(0.35D);
      player.func_70634_a(forward.field_72450_a, forward.field_72448_b, forward.field_72449_c);
      this.setTargetYaw(player.field_70759_as + 180.0F);
   }

   protected void prepareAction(boolean shouldPreparePayment, boolean shouldSetTargetPos, UUID player) {
      if (this.field_70170_p.field_72995_K) {
         PacketHandler.INSTANCE.sendToServer(new PrepareAction(this.girlId(), player, shouldPreparePayment, shouldSetTargetPos));
      } else {
         PrepareAction.Handler.prepareAction(this.girlId(), player, shouldPreparePayment, shouldSetTargetPos);
      }

   }

   public static ArrayList<GirlEntity> getGirlsByUUID(UUID uuid) {
      ArrayList<GirlEntity> girls = new ArrayList();
      Iterator var2 = girlEntities.iterator();

      while(var2.hasNext()) {
         GirlEntity girl = (GirlEntity)var2.next();
         if (girl.girlId().equals(uuid)) {
            girls.add(girl);
         }
      }

      return girls;
   }

   protected BlockPos findBed(BlockPos pos) {
      return this.findBed(pos, 1);
   }

   protected BlockPos findBed(BlockPos pos, int whichBed) {
      return this.findBlock(pos, whichBed, Blocks.field_150324_C, 22, 3, (HashSet)null);
   }

   protected BlockPos findBlock(BlockPos pos, int whichBlock, Block blockToSearch, int blocksToSearch, int searchYDifference, @Nullable HashSet<Biome> allowedBiomes) {
      int step = 1;
      int dir = -1;
      BlockPos searchPos = pos;
      int blocksFound = 0;

      while(step < blocksToSearch) {
         for(int move = 0; move < 2; ++move) {
            dir *= -1;

            int stepTaken;
            int y;
            for(stepTaken = 0; stepTaken < step; ++stepTaken) {
               searchPos = searchPos.func_177982_a(0, 0, dir);

               for(y = -searchYDifference; y < searchYDifference + 1; ++y) {
                  if (this.field_70170_p.func_180495_p(searchPos.func_177982_a(0, y, dir)).func_177230_c() == blockToSearch) {
                     ++blocksFound;
                     if (blocksFound >= whichBlock && (allowedBiomes == null || allowedBiomes.contains(this.field_70170_p.func_180494_b(searchPos.func_177982_a(dir, y, 0))))) {
                        return searchPos.func_177982_a(0, y, dir);
                     }
                  }
               }
            }

            for(stepTaken = 0; stepTaken < step; ++stepTaken) {
               searchPos = searchPos.func_177982_a(dir, 0, 0);

               for(y = -searchYDifference; y < searchYDifference + 1; ++y) {
                  if (this.field_70170_p.func_180495_p(searchPos.func_177982_a(dir, y, 0)).func_177230_c() == blockToSearch) {
                     ++blocksFound;
                     if (blocksFound >= whichBlock && (allowedBiomes == null || allowedBiomes.contains(this.field_70170_p.func_180494_b(searchPos.func_177982_a(dir, y, 0))))) {
                        return searchPos.func_177982_a(dir, y, 0);
                     }
                  }
               }
            }

            ++step;
         }
      }

      return null;
   }

   public boolean hasMaster() {
      return !((String)this.field_70180_af.func_187225_a(MASTER)).equals("");
   }

   protected ResourceLocation func_184647_J() {
      return LootTableHandler.JENNY;
   }

   @SideOnly(Side.CLIENT)
   public abstract void doAction(String var1, UUID var2);

   @SideOnly(Side.CLIENT)
   protected abstract <E extends IAnimatable> PlayState predicate(AnimationEvent<E> var1);

   @SideOnly(Side.CLIENT)
   protected void createAnimation(String path, boolean looped, AnimationEvent event) {
      event.getController().setAnimation((new AnimationBuilder()).addAnimation(path, looped));
      this.currentAnimationLoop = looped;
      this.currentAnimationPath = path;
   }

   @SideOnly(Side.CLIENT)
   public void registerControllers(AnimationData data) {
      data.addAnimationController(this.movementController);
      data.addAnimationController(this.eyesController);
   }

   protected void resetPlayer() {
      if (this.field_70170_p.field_72995_K && this.belongsToPlayer()) {
         this.playerCamPos = null;
         PacketHandler.INSTANCE.sendToServer(new ResetGirl(this.girlId(), true));
      } else if (!this.field_70170_p.field_72995_K) {
         ResetGirl.Handler.resetPlayer((EntityPlayerMP)this.field_70170_p.func_152378_a(this.playerSheHasSexWith()));
      }

   }

   protected static boolean isHavingSex(@Nonnull UUID player) {
      Iterator var1 = girlEntities.iterator();

      GirlEntity girl;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         girl = (GirlEntity)var1.next();
      } while(!player.equals(girl.playerSheHasSexWith()));

      return true;
   }

   protected static boolean isHavingSex(@Nonnull EntityPlayer player) {
      return isHavingSex(player.getPersistentID());
   }

   public void resetGirl() {
      this.playerCamPos = null;
      this.playerIsThrusting = false;
      this.playerIsCumming = false;
      this.func_189654_d(false);
      this.setCurrentAction((GirlEntity.Action)null);
      if (this.field_70170_p.field_72995_K) {
         this.clientReset();
      }

   }

   public void resetNavigator() {
      this.field_70699_by = this.func_175447_b(this.field_70170_p);
   }

   @SideOnly(Side.CLIENT)
   void clientReset() {
      if (this.belongsToPlayer()) {
         HandlePlayerMovement.setActive(true);
         Minecraft.func_71410_x().field_71439_g.func_82142_c(false);
         PacketHandler.INSTANCE.sendToServer(new ResetGirl(this.girlId()));
      }

   }

   @SideOnly(Side.CLIENT)
   public static void sendThrust(UUID playerUUID) {
      Iterator var1 = girlEntities.iterator();

      while(var1.hasNext()) {
         GirlEntity girl = (GirlEntity)var1.next();
         if (girl.belongsToPlayer() && girl.playerSheHasSexWith().equals(playerUUID) && !girl.playerIsThrusting) {
            girl.thrust();
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void sendCum(UUID playerUUID) {
      Iterator var1 = girlEntities.iterator();

      while(var1.hasNext()) {
         GirlEntity girl = (GirlEntity)var1.next();
         if (girl.belongsToPlayer() && girl.playerSheHasSexWith().equals(playerUUID) && !girl.playerIsCumming) {
            girl.cum();
         }
      }

   }

   @SideOnly(Side.CLIENT)
   protected abstract void thrust();

   @SideOnly(Side.CLIENT)
   protected abstract void cum();

   public TargetPoint getTargetPoint() {
      return new TargetPoint(this.field_71093_bK, this.field_70165_t, this.field_70163_u, this.field_70161_v, 50.0D);
   }

   protected void moveCamera(double x, double y, double z, float yaw, float pitch) {
      if (this.playerSheHasSexWith() == null) {
         System.out.println("couldnt move camera because the player isn't set");
      } else {
         EntityPlayer player = this.field_70170_p.func_152378_a(this.playerSheHasSexWith());
         if (this.playerCamPos == null) {
            this.playerCamPos = player.func_174791_d();
         }

         Vec3d newPos = this.playerCamPos;
         newPos = newPos.func_72441_c(-Math.sin((double)(this.playerYaw + 90.0F) * 0.017453292519943295D) * x, 0.0D, Math.cos((double)(this.playerYaw + 90.0F) * 0.017453292519943295D) * x);
         newPos = newPos.func_72441_c(0.0D, y, 0.0D);
         newPos = newPos.func_72441_c(-Math.sin((double)this.playerYaw * 0.017453292519943295D) * z, 0.0D, Math.cos((double)this.playerYaw * 0.017453292519943295D) * z);
         if (this.field_70170_p.field_72995_K) {
            PacketHandler.INSTANCE.sendToServer(new TeleportPlayer(player.getPersistentID().toString(), newPos, this.playerYaw + yaw, pitch));
         } else {
            player.func_70080_a(newPos.field_72450_a, newPos.field_72448_b, newPos.field_72449_c, this.playerYaw + yaw, pitch);
            player.func_70634_a(newPos.field_72450_a, newPos.field_72448_b, newPos.field_72449_c);
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
         }

      }
   }

   @SideOnly(Side.CLIENT)
   protected boolean belongsToPlayer() {
      if (!this.field_70170_p.field_72995_K) {
         return false;
      } else {
         EntityPlayer player = Minecraft.func_71410_x().field_71439_g;
         return player.getPersistentID().equals(this.playerSheHasSexWith()) || player.func_110124_au().equals(this.playerSheHasSexWith());
      }
   }

   protected abstract void checkFollowUp();

   protected abstract String getGirlName();

   public void say(String msg) {
      if (!this.field_70170_p.field_72995_K) {
         PacketHandler.INSTANCE.sendToAllAround(new SendChatMessage("<" + this.getGirlName() + "> " + msg, this.field_71093_bK, this.girlId()), new TargetPoint(this.field_71093_bK, this.field_70165_t, this.field_70163_u, this.field_70161_v, 40.0D));
      } else if (this.belongsToPlayer()) {
         PacketHandler.INSTANCE.sendToServer(new SendChatMessage("<" + this.getGirlName() + "> " + msg, this.field_71093_bK, this.girlId()));
      }

   }

   protected void say(String msg, boolean noPrefix) {
      if (noPrefix) {
         if (this.belongsToPlayer()) {
            PacketHandler.INSTANCE.sendToServer(new SendChatMessage(msg, this.field_71093_bK, this.girlId()));
         }
      } else {
         this.say(msg);
      }

   }

   protected void sayAround(String msg) {
      if (this.field_70170_p.field_72995_K) {
         Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString("<" + this.getGirlName() + "> " + msg));
      }

   }

   protected void sayAroundAsPlayer(UUID playerUUID, String msg) {
      EntityPlayer player = this.field_70170_p.func_152378_a(playerUUID);
      if (player == null) {
         System.out.println("Player with UUID " + playerUUID.toString() + " not found");
      } else {
         if (this.field_70170_p.field_72995_K) {
            Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString("<" + player.func_70005_c_() + "> " + msg));
         }

      }
   }

   public void playSoundAroundHer(SoundEvent sound, float volume, float pitch) {
      if (this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_184134_a((double)this.func_180425_c().func_177958_n(), (double)this.func_180425_c().func_177956_o(), (double)this.func_180425_c().func_177952_p(), sound, SoundCategory.NEUTRAL, volume, pitch, false);
      } else {
         this.field_70170_p.func_184133_a((EntityPlayer)null, this.func_180425_c(), sound, SoundCategory.PLAYERS, volume, pitch);
      }

   }

   public void playSoundAroundHer(SoundEvent sound) {
      this.playSoundAroundHer(sound, 1.0F, 1.0F);
   }

   public void playSoundAroundHer(SoundEvent[] sound) {
      this.playSoundAroundHer(sound[this.func_70681_au().nextInt(sound.length)], 1.0F, 1.0F);
   }

   public void playSoundAroundHer(SoundEvent sound, float volume) {
      this.playSoundAroundHer(sound, volume, 1.0F);
   }

   @SideOnly(Side.CLIENT)
   public boolean isClosestPlayer() {
      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 50.0D);
      return closestPlayer == null ? false : closestPlayer.getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID());
   }

   public Vec3d getInFrontOfPlayer() {
      return this.getInFrontOfPlayer(1.0D);
   }

   public Vec3d getInFrontOfPlayer(double distance) {
      EntityPlayer playerSheHasSexWith = this.field_70170_p.func_152378_a(this.playerSheHasSexWith());
      float playerYaw = playerSheHasSexWith.field_70177_z;
      return playerSheHasSexWith.func_174791_d().func_72441_c(-Math.sin((double)playerYaw * 0.017453292519943295D) * distance, 0.0D, Math.cos((double)playerYaw * 0.017453292519943295D) * distance);
   }

   public static void spawnParticleOnGirl(EnumParticleTypes particle, GirlEntity girl) {
      double motionX = Reference.RANDOM.nextGaussian() * 0.02D;
      double motionY = Reference.RANDOM.nextGaussian() * 0.02D;
      double motionZ = Reference.RANDOM.nextGaussian() * 0.02D;
      girl.field_70170_p.func_175688_a(particle, girl.field_70165_t + (double)(Reference.RANDOM.nextFloat() * girl.field_70130_N * 2.0F) - (double)girl.field_70130_N, girl.field_70163_u + 0.5D + (double)(Reference.RANDOM.nextFloat() * girl.field_70131_O), girl.field_70161_v + (double)(Reference.RANDOM.nextFloat() * girl.field_70130_N * 2.0F) - (double)girl.field_70130_N, motionX, motionY, motionZ, new int[0]);
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   public boolean func_70104_M() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   protected SoundEvent func_184639_G() {
      if (this.func_70681_au().nextInt(10000) == 0) {
         if (this.field_70170_p.field_72995_K && Minecraft.func_71410_x().field_71439_g.func_174791_d().func_72438_d(this.func_174791_d()) < 10.0D) {
            this.sayAround("whopa");
         }

         return SoundsHandler.random(SoundsHandler.MISC_FART);
      } else {
         return null;
      }
   }

   static {
      MASTER = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(73);
      SHOULD_BE_AT_TARGET = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187198_h).func_187156_b().func_187161_a(72);
      TARGET_POS = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(71);
      TARGET_YAW = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187193_c).func_187156_b().func_187161_a(70);
      GIRL_ID = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(69);
      CURRENT_MODEL = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187192_b).func_187156_b().func_187161_a(68);
      CURRENT_ACTION = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(67);
      ANIMATION_FOLLOW_UP = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(66);
      PLAYER_SHE_HAS_SEX_WITH = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(65);
      WALK_SPEED = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(64);
      TEMPTATION_ITEMS = Arrays.asList(Items.field_151166_bC, Items.field_151045_i, Items.field_151043_k, Items.field_151079_bi);
   }

   public static enum WalkSpeed {
      WALK,
      FAST_WALK,
      RUN;
   }

   public static enum Action {
      NULL(0, false, true),
      STARTBLOWJOB(2, true, false),
      SUCKBLOWJOB(2, true, false),
      CUMBLOWJOB(0, true, false),
      THRUSTBLOWJOB(2, true, false),
      PAYMENT(5, true, false),
      STARTDOGGY(2, false, false),
      WAITDOGGY(0, false, true),
      DOGGYSTART(0, true, false),
      DOGGYSLOW(2, true, false),
      DOGGYFAST(2, true, false),
      DOGGYCUM(2, true, false),
      STRIP(5, false, false),
      DASH(2, false, false),
      HUG(2, true, false),
      HUGIDLE(0, true, true),
      HUGSELECTED(0, true, false),
      UNDRESS(2, false, true),
      DRESS(2, false, true),
      SITDOWN(2, false, false),
      SITDOWNIDLE(0, false, true),
      COWGIRLSTART(0, true, false),
      COWGIRLSLOW(10, true, false),
      COWGIRLFAST(10, true, false),
      COWGIRLCUM(2, true, false),
      ATTACK(0, false, true),
      BOW(2, false, true),
      RIDE(0, false, true),
      SIT(0, false, true),
      THROW_PEARL(0, false, false),
      DOWNED(7, false, true),
      PAIZURI_START(0, true, false),
      PAIZURI_SLOW(0, true, true),
      PAIZURI_FAST(0, true, false),
      PAIZURI_CUM(0, true, false),
      MISSIONARY_START(0, true, false),
      MISSIONARY_SLOW(2, true, false),
      MISSIONARY_FAST(2, true, false),
      MISSIONARY_CUM(2, true, false),
      TALK_HORNY(5, true, false),
      TALK_IDLE(0, true, true),
      TALK_RESPONSE(2, true, false),
      ANAL_PREPARE(5, false, false),
      ANAL_WAIT(0, false, true),
      ANAL_START(2, true, false),
      ANAL_SLOW(2, true, true),
      ANAL_FAST(2, true, false),
      ANAL_CUM(2, true, false),
      SUMMON(0, false, false),
      SUMMON_WAIT(0, false, true),
      HEAD_PAT(0, true, false),
      DEEPTHROAT_PREPARE(0, false, false),
      DEEPTHROAT_START(0, true, false),
      DEEPTHROAT_SLOW(2, true, false),
      DEEPTHROAT_FAST(2, true, false),
      DEEPTHROAT_CUM(2, true, false),
      SUMMON_NORMAL(0, false, false),
      SUMMON_SAND(0, false, false),
      SUMMON_NORMAL_WAIT(2, false, true),
      DEEPTHROAT_NORMAL_PREPARE(2, false, false),
      RICH(0, false, false),
      RICH_NORMAL(0, false, false),
      CITIZEN_START(0, true, false),
      CITIZEN_SLOW(5, true, false),
      CITIZEN_FAST(0, true, false),
      CITIZEN_CUM(2, true, false),
      FISHING_START(5, false, false),
      FISHING_IDLE(0, false, true),
      FISHING_EAT(0, false, false),
      FISHING_THROW_AWAY(0, false, false),
      TOUCH_BOOBS_INTRO(0, true, false),
      TOUCH_BOOBS_SLOW(2, true, false),
      TOUCH_BOOBS_FAST(2, true, false),
      TOUCH_BOOBS_CUM(2, true, false);

      public int transitionTick;
      public boolean hasPlayer;
      public boolean autoBlink;

      private Action(int transitionTick, boolean hasPlayer, boolean autoBlink) {
         this.transitionTick = transitionTick;
         this.hasPlayer = hasPlayer;
         this.autoBlink = autoBlink;
      }
   }

   public static enum PaymentItems {
      DIAMOND,
      GOLD,
      EMERALD;
   }
}
