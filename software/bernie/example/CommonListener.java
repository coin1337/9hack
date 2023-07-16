package software.bernie.example;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import software.bernie.example.block.BotariumBlock;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;

public class CommonListener {
   private static IForgeRegistry<Item> itemRegistry;
   private static IForgeRegistry<Block> blockRegistry;

   @SubscribeEvent
   public void onRegisterBlocks(Register<Block> event) {
      blockRegistry = event.getRegistry();
      BlockRegistry.BOTARIUM_BLOCK = new BotariumBlock();
      BlockRegistry.FERTILIZER_BLOCK = new FertilizerBlock();
      BlockRegistry.BOTARIUM_BLOCK.func_149647_a(GeckoLibMod.getGeckolibItemGroup());
      BlockRegistry.FERTILIZER_BLOCK.func_149647_a(GeckoLibMod.getGeckolibItemGroup());
      registerBlock(BlockRegistry.BOTARIUM_BLOCK, (String)"botariumblock");
      registerBlock(BlockRegistry.FERTILIZER_BLOCK, (String)"fertilizerblock");
   }

   @SubscribeEvent
   public void onRegisterEntities(Register<EntityEntry> event) {
      int id = 0;
      IForgeRegistry var10000 = event.getRegistry();
      EntityEntryBuilder var10001 = EntityEntryBuilder.create().entity(BikeEntity.class).name("Bike");
      ResourceLocation var10002 = new ResourceLocation("geckolib3", "bike");
      int var3 = id + 1;
      var10000.register(var10001.id(var10002, id).tracker(160, 2, false).build());
      event.getRegistry().register(EntityEntryBuilder.create().entity(GeoExampleEntity.class).name("Example").id(new ResourceLocation("geckolib3", "example"), var3++).tracker(160, 2, false).build());
      event.getRegistry().register(EntityEntryBuilder.create().entity(GeoExampleEntityLayer.class).name("ExampleLayer").id(new ResourceLocation("geckolib3", "examplelayer"), var3++).tracker(160, 2, false).build());
      GameRegistry.registerTileEntity(BotariumTileEntity.class, new ResourceLocation("geckolib3", "botariumtile"));
      GameRegistry.registerTileEntity(FertilizerTileEntity.class, new ResourceLocation("geckolib3", "fertilizertile"));
   }

   @SubscribeEvent
   public void onRegisterItems(Register<Item> event) {
      itemRegistry = event.getRegistry();
      ItemRegistry.JACK_IN_THE_BOX = (JackInTheBoxItem)registerItem(new JackInTheBoxItem(), (String)"jackintheboxitem");
      ItemRegistry.POTATO_HEAD = (PotatoArmorItem)registerItem(new PotatoArmorItem(ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD), (String)"potato_head");
      ItemRegistry.POTATO_CHEST = (PotatoArmorItem)registerItem(new PotatoArmorItem(ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST), (String)"potato_chest");
      ItemRegistry.POTATO_LEGGINGS = (PotatoArmorItem)registerItem(new PotatoArmorItem(ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS), (String)"potato_leggings");
      ItemRegistry.POTATO_BOOTS = (PotatoArmorItem)registerItem(new PotatoArmorItem(ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET), (String)"potato_boots");
      ItemRegistry.BOTARIUM = registerItem(new ItemBlock(BlockRegistry.BOTARIUM_BLOCK), (String)"botarium");
      ItemRegistry.FERTILIZER = registerItem(new ItemBlock(BlockRegistry.FERTILIZER_BLOCK), (String)"fertilizer");
   }

   public static <T extends Item> T registerItem(T item, String name) {
      registerItem(item, new ResourceLocation("geckolib3", name));
      return item;
   }

   public static <T extends Item> T registerItem(T item, ResourceLocation name) {
      itemRegistry.register(((Item)item.setRegistryName(name)).func_77655_b(name.toString().replace(":", ".")));
      return item;
   }

   public static void registerBlock(Block block, String name) {
      registerBlock(block, new ResourceLocation("geckolib3", name));
   }

   public static void registerBlock(Block block, ResourceLocation name) {
      blockRegistry.register(((Block)block.setRegistryName(name)).func_149663_c(name.toString().replace(":", ".")));
   }

   @SubscribeEvent
   public void onRegisterSoundEvents(Register<SoundEvent> event) {
      ResourceLocation location = new ResourceLocation("geckolib3", "jack_music");
      SoundRegistry.JACK_MUSIC = (SoundEvent)(new SoundEvent(location)).setRegistryName(location);
      event.getRegistry().register(SoundRegistry.JACK_MUSIC);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onModelRegistry(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.JACK_IN_THE_BOX, 0, new ModelResourceLocation("geckolib3:jackintheboxitem", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.BOTARIUM, 0, new ModelResourceLocation("geckolib3:botarium", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.FERTILIZER, 0, new ModelResourceLocation("geckolib3:fertilizer", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_HEAD, 0, new ModelResourceLocation("geckolib3:potato_head", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_CHEST, 0, new ModelResourceLocation("geckolib3:potato_chest", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_LEGGINGS, 0, new ModelResourceLocation("geckolib3:potato_leggings", "inventory"));
      ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_BOOTS, 0, new ModelResourceLocation("geckolib3:potato_boots", "inventory"));
      ItemRegistry.JACK_IN_THE_BOX.setTileEntityItemStackRenderer(new JackInTheBoxRenderer());
   }
}
