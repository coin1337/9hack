package com.schnurritv.sexmod.girls.allie.lamp;

import com.schnurritv.sexmod.Packets.SummonAllie;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LampItem extends Item implements IAnimatable {
   public static final LampItem LAMP_ITEM = new LampItem();
   private final AnimationFactory factory = new AnimationFactory(this);
   AnimationController<LampItem> controller;
   int tick = 0;

   public LampItem() {
      this.func_77637_a(CreativeTabs.field_78026_f);
      this.field_77777_bU = 1;
   }

   public static void registerLamp() {
      LAMP_ITEM.setRegistryName("sexmod", "allies_lamp");
      LAMP_ITEM.func_77655_b("allies_lamp");
      MinecraftForge.EVENT_BUS.register(LampItem.class);
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      event.getRegistry().register(LAMP_ITEM);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModel(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(LAMP_ITEM, 0, new ModelResourceLocation("sexmod:allies_lamp"));
      LAMP_ITEM.setTileEntityItemStackRenderer(new LampRenderer());
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void hideHotbar(Pre event) {
      Minecraft mc = Minecraft.func_71410_x();
      ItemStack stack = mc.field_71439_g.func_184614_ca();
      if (stack.func_77973_b() instanceof LampItem && stack.func_77942_o() && stack.func_77978_p().func_186857_a("user").equals(mc.field_71439_g.getPersistentID()) && event.getType() == ElementType.HOTBAR) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void putInChest(LootTableLoadEvent event) {
      HashSet<ResourceLocation> lootChests = new HashSet();
      lootChests.add(LootTableList.field_186424_f);
      lootChests.add(LootTableList.field_186429_k);
      lootChests.add(LootTableList.field_186422_d);
      lootChests.add(LootTableList.field_191192_o);
      if (lootChests.contains(event.getName())) {
         LootPool pool = event.getTable().getPool("pool3");
         if (pool == null) {
            pool = event.getTable().getPool("pool2");
         }

         if (pool != null) {
            pool.addEntry(new LootEntryItem(LAMP_ITEM, 5, 0, new LootFunction[0], new LootCondition[0], "sexmod:allies_lamp"));
         }
      }

   }

   public void registerControllers(AnimationData data) {
      this.controller = new AnimationController(this, "controller", 2.0F, this::predicate);
      data.addAnimationController(this.controller);
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack stack = playerIn.func_184586_b(handIn);
      if (!(stack.func_77973_b() instanceof LampItem)) {
         return super.func_77659_a(worldIn, playerIn, handIn);
      } else {
         PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(playerIn.getPersistentID());
         if (girl != null) {
            return super.func_77659_a(worldIn, playerIn, handIn);
         } else {
            NBTTagCompound nbt;
            if (stack.func_77942_o()) {
               nbt = stack.func_77978_p();
            } else {
               nbt = new NBTTagCompound();
            }

            if (nbt.func_74762_e("uses") >= 3) {
               Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString("you are out of wishes. Find a new lamp!"));
            } else {
               Vec3d alliesPos = playerIn.func_174791_d().func_72441_c(-Math.sin((double)playerIn.field_70759_as * 0.017453292519943295D) * 2.0D, 0.0D, Math.cos((double)playerIn.field_70759_as * 0.017453292519943295D) * 2.0D);
               BlockPos blockPos = (new BlockPos(alliesPos)).func_177982_a(0, -1, 0);
               nbt.func_186854_a("user", playerIn.getPersistentID());
               if (!worldIn.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150354_m)) {
                  nbt.func_74768_a("uses", nbt.func_74762_e("uses") + 1);
               }

               stack.func_77982_d(nbt);
            }

            return super.func_77659_a(worldIn, playerIn, handIn);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o()) {
         int wishesLeft = 3 - stack.func_77978_p().func_74762_e("uses");
         switch(wishesLeft) {
         case 0:
            tooltip.add("no wishes left");
            break;
         case 1:
            tooltip.add("1 wish left");
            break;
         case 2:
            tooltip.add("2 wishes left");
         }
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public void func_77663_a(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, worldIn, entityIn, itemSlot, isSelected);
      if (stack.func_77942_o() && worldIn.field_72995_K && stack.func_77978_p().func_186857_a("user") != null && stack.func_77978_p().func_186857_a("user").equals(Minecraft.func_71410_x().field_71439_g.getPersistentID()) && ++this.tick > 60) {
         EntityPlayer player = (EntityPlayer)entityIn;
         Vec3d pos = entityIn.func_174791_d().func_72441_c(-Math.sin((double)player.field_70759_as * 0.017453292519943295D) * 2.0D, 0.0D, Math.cos((double)player.field_70759_as * 0.017453292519943295D) * 2.0D);

         for(int i = 0; i < 40; ++i) {
            double factor = Reference.RANDOM.nextDouble() + 1.0D;
            double motionX = Reference.RANDOM.nextGaussian() * factor * 0.1D;
            double motionY = Reference.RANDOM.nextGaussian() * factor * 0.1D;
            double motionZ = Reference.RANDOM.nextGaussian() * factor * 0.1D;
            worldIn.func_175688_a(EnumParticleTypes.PORTAL, pos.field_72450_a + (double)(Reference.RANDOM.nextFloat() * 0.5F), pos.field_72448_b + (double)Reference.RANDOM.nextFloat(), pos.field_72449_c + (double)(Reference.RANDOM.nextFloat() * 0.5F), motionX, motionY, motionZ, new int[0]);
         }

         if (this.tick == 100) {
            HandlePlayerMovement.setActive(false);
            PacketHandler.INSTANCE.sendToServer(new SummonAllie());
         }

         if (this.tick > 120) {
            stack.func_77978_p().func_186854_a("user", UUID.randomUUID());
            this.tick = 0;
         }
      }

   }

   @SideOnly(Side.CLIENT)
   protected <segs extends IAnimatable> PlayState predicate(AnimationEvent<segs> event) {
      ItemStack stack = Minecraft.func_71410_x().field_71439_g.func_184614_ca();
      if (!(stack.func_77973_b() instanceof LampItem)) {
         this.controller.setAnimation((new AnimationBuilder()).addAnimation("animation.lamp.null", true));
      }

      if (stack.func_77942_o()) {
         UUID userId = stack.func_77978_p().func_186857_a("user");
         if (userId.equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
            this.controller.setAnimation((new AnimationBuilder()).addAnimation("animation.lamp.rub", false));
         } else {
            this.controller.setAnimation((new AnimationBuilder()).addAnimation("animation.lamp.null", true));
         }
      }

      return PlayState.CONTINUE;
   }
}
