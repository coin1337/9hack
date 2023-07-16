package com.daripher.sexmod.client.util;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class FakeChunkProvider implements IChunkProvider {
   @Nullable
   public Chunk func_186026_b(int x, int z) {
      return null;
   }

   public Chunk func_186025_d(int x, int z) {
      return null;
   }

   public boolean func_73156_b() {
      return false;
   }

   public String func_73148_d() {
      return null;
   }

   public boolean func_191062_e(int x, int z) {
      return true;
   }
}
