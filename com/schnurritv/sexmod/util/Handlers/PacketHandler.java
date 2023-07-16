package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.Packets.BeeOpenChest;
import com.schnurritv.sexmod.Packets.CatActivateFishing;
import com.schnurritv.sexmod.Packets.CatEatingDone;
import com.schnurritv.sexmod.Packets.CatThrowAwayItem;
import com.schnurritv.sexmod.Packets.ChangeDataParameter;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.JennyAwaitPlayerDoggy;
import com.schnurritv.sexmod.Packets.MakeRichWish;
import com.schnurritv.sexmod.Packets.OpenGirlInventory;
import com.schnurritv.sexmod.Packets.PrepareAction;
import com.schnurritv.sexmod.Packets.RemoveItems;
import com.schnurritv.sexmod.Packets.ResetGirl;
import com.schnurritv.sexmod.Packets.SendChatMessage;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SendEllieToPlayer;
import com.schnurritv.sexmod.Packets.SendGirlToBed;
import com.schnurritv.sexmod.Packets.SetNewHome;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.SetSlimePregnant;
import com.schnurritv.sexmod.Packets.SexPrompt;
import com.schnurritv.sexmod.Packets.StartStandingSexAnimation;
import com.schnurritv.sexmod.Packets.SummonAllie;
import com.schnurritv.sexmod.Packets.TeleportPlayer;
import com.schnurritv.sexmod.Packets.UpdatePlayerModel;
import com.schnurritv.sexmod.Packets.UploadInventoryToServer;
import com.schnurritv.sexmod.Packets.WishDone;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static SimpleNetworkWrapper INSTANCE;
   private static int ID = 0;

   private static int nextID() {
      return ID++;
   }

   public static void registerMessages() {
      INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("sexmodchannel");
      INSTANCE.registerMessage(SendChatMessage.Handler.class, SendChatMessage.class, nextID(), Side.CLIENT);
      INSTANCE.registerMessage(SendChatMessage.Handler.class, SendChatMessage.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SetPlayerMovement.Handler.class, SetPlayerMovement.class, nextID(), Side.CLIENT);
      INSTANCE.registerMessage(TeleportPlayer.Handler.class, TeleportPlayer.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SendGirlToBed.Handler.class, SendGirlToBed.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(JennyAwaitPlayerDoggy.Handler.class, JennyAwaitPlayerDoggy.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SendEllieToPlayer.Handler.class, SendEllieToPlayer.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(PrepareAction.Handler.class, PrepareAction.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(ClearAnimationCache.Handler.class, ClearAnimationCache.class, nextID(), Side.CLIENT);
      INSTANCE.registerMessage(ClearAnimationCache.Handler.class, ClearAnimationCache.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(ResetGirl.Handler.class, ResetGirl.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SetSlimePregnant.Handler.class, SetSlimePregnant.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SetSlimePregnant.Handler.class, SetSlimePregnant.class, nextID(), Side.CLIENT);
      INSTANCE.registerMessage(ChangeDataParameter.Handler.class, ChangeDataParameter.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(OpenGirlInventory.Handler.class, OpenGirlInventory.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SendCompanionHome.Handler.class, SendCompanionHome.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SetNewHome.Handler.class, SetNewHome.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(UploadInventoryToServer.Handler.class, UploadInventoryToServer.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(RemoveItems.Handler.class, RemoveItems.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SummonAllie.Handler.class, SummonAllie.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(WishDone.Handler.class, WishDone.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(MakeRichWish.Handler.class, MakeRichWish.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(UpdatePlayerModel.Handler.class, UpdatePlayerModel.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SexPrompt.Handler.class, SexPrompt.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(SexPrompt.Handler.class, SexPrompt.class, nextID(), Side.CLIENT);
      INSTANCE.registerMessage(StartStandingSexAnimation.Handler.class, StartStandingSexAnimation.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(BeeOpenChest.Handler.class, BeeOpenChest.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(CatActivateFishing.Handler.class, CatActivateFishing.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(CatEatingDone.Handler.class, CatEatingDone.class, nextID(), Side.SERVER);
      INSTANCE.registerMessage(CatThrowAwayItem.Handler.class, CatThrowAwayItem.class, nextID(), Side.SERVER);
   }
}
