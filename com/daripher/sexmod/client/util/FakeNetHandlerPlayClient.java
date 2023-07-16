package com.daripher.sexmod.client.util;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.EnumPacketDirection;

public class FakeNetHandlerPlayClient extends NetHandlerPlayClient {
   private NetworkPlayerInfo playerInfo;

   public FakeNetHandlerPlayClient(Minecraft mcIn) {
      super(mcIn, mcIn.field_71462_r, new FakeNetworkManager(EnumPacketDirection.CLIENTBOUND), mcIn.func_110432_I().func_148256_e());
   }

   public NetworkPlayerInfo func_175102_a(UUID uniqueId) {
      return this.playerInfo;
   }

   @Nullable
   public NetworkPlayerInfo func_175104_a(String name) {
      return this.playerInfo;
   }
}
