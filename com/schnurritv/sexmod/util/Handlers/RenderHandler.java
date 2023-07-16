package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.allie.AllieModel;
import com.schnurritv.sexmod.girls.allie.AllieRenderer;
import com.schnurritv.sexmod.girls.allie.PlayerAllie;
import com.schnurritv.sexmod.girls.allie.PlayerAllieRenderer;
import com.schnurritv.sexmod.girls.base.GirlRenderer;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirlRenderer;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import com.schnurritv.sexmod.girls.bee.BeeModel;
import com.schnurritv.sexmod.girls.bee.PlayerBee;
import com.schnurritv.sexmod.girls.bee.PlayerBeeRenderer;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.bia.BiaModel;
import com.schnurritv.sexmod.girls.bia.PlayerBia;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.cat.CatModel;
import com.schnurritv.sexmod.girls.cat.CatRenderer;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishHook;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishHookRenderer;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.ellie.EllieModel;
import com.schnurritv.sexmod.girls.ellie.PlayerEllie;
import com.schnurritv.sexmod.girls.ellie.PlayerEllieRenderer;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.jenny.JennyModel;
import com.schnurritv.sexmod.girls.jenny.PlayerJenny;
import com.schnurritv.sexmod.girls.slime.PlayerSlime;
import com.schnurritv.sexmod.girls.slime.PlayerSlimeRenderer;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.girls.slime.SlimeModel;
import com.schnurritv.sexmod.girls.slime.friendlySlime.FriendlySlimeEntity;
import com.schnurritv.sexmod.girls.slime.friendlySlime.FriendlySlimeRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler {
   public static void registerEntityRenders() {
      RenderingRegistry.registerEntityRenderingHandler(JennyEntity.class, (manager) -> {
         return new GirlRenderer(manager, new JennyModel(), -0.15D);
      });
      RenderingRegistry.registerEntityRenderingHandler(EllieEntity.class, (manager) -> {
         return new GirlRenderer(manager, new EllieModel(), 0.05D);
      });
      RenderingRegistry.registerEntityRenderingHandler(SlimeEntity.class, (manager) -> {
         return new GirlRenderer(manager, new SlimeModel(), -0.2D);
      });
      RenderingRegistry.registerEntityRenderingHandler(BiaEntity.class, (manager) -> {
         return new GirlRenderer(manager, new BiaModel(), -0.4D);
      });
      RenderingRegistry.registerEntityRenderingHandler(AllieEntity.class, (manager) -> {
         return new AllieRenderer(manager, new AllieModel(), -0.4D);
      });
      RenderingRegistry.registerEntityRenderingHandler(BeeEntity.class, (manager) -> {
         return new GirlRenderer(manager, new BeeModel(), -0.4D);
      });
      RenderingRegistry.registerEntityRenderingHandler(FriendlySlimeEntity.class, FriendlySlimeRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler(CatEntity.class, (manager) -> {
         return new CatRenderer(manager, new CatModel(), -0.4D);
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerBia.class, (manager) -> {
         return new PlayerGirlRenderer(manager, new BiaModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerJenny.class, (manager) -> {
         return new PlayerGirlRenderer(manager, new JennyModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerEllie.class, (manager) -> {
         return new PlayerEllieRenderer(manager, new EllieModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerSlime.class, (manager) -> {
         return new PlayerSlimeRenderer(manager, new SlimeModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerAllie.class, (manager) -> {
         return new PlayerAllieRenderer(manager, new AllieModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(PlayerBee.class, (manager) -> {
         return new PlayerBeeRenderer(manager, new BeeModel());
      });
      RenderingRegistry.registerEntityRenderingHandler(CatFishHook.class, CatFishHookRenderer::new);
   }
}
