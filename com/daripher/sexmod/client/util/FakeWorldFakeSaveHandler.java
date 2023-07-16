package com.daripher.sexmod.client.util;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorldFakeSaveHandler implements ISaveHandler {
   @Nullable
   public WorldInfo func_75757_d() {
      return null;
   }

   public void func_75762_c() throws MinecraftException {
   }

   public IChunkLoader func_75763_a(WorldProvider provider) {
      return null;
   }

   public void func_75755_a(WorldInfo worldInformation, NBTTagCompound tagCompound) {
   }

   public void func_75761_a(WorldInfo worldInformation) {
   }

   public IPlayerFileData func_75756_e() {
      return null;
   }

   public void func_75759_a() {
   }

   public File func_75765_b() {
      return null;
   }

   public File func_75758_b(String mapName) {
      return null;
   }

   public TemplateManager func_186340_h() {
      return new TemplateManager("", Minecraft.func_71410_x().func_184126_aj());
   }
}
