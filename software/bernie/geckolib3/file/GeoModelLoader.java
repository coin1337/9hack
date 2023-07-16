package software.bernie.geckolib3.file;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.geo.exception.GeoModelException;
import software.bernie.geckolib3.geo.raw.pojo.Converter;
import software.bernie.geckolib3.geo.raw.pojo.FormatVersion;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.GeoBuilder;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class GeoModelLoader {
   public GeoModel loadModel(IResourceManager resourceManager, ResourceLocation location) {
      try {
         RawGeoModel rawModel = Converter.fromJsonString(AnimationFileLoader.getResourceAsString(location, resourceManager));
         if (rawModel.getFormatVersion() != FormatVersion.VERSION_1_12_0) {
            throw new GeoModelException(location, "Wrong geometry json version, expected 1.12.0");
         } else {
            RawGeometryTree rawGeometryTree = RawGeometryTree.parseHierarchy(rawModel, location);
            return GeoBuilder.getGeoBuilder(location.func_110624_b()).constructGeoModel(rawGeometryTree);
         }
      } catch (Exception var5) {
         GeckoLib.LOGGER.error(String.format("Error parsing %S", location), var5);
         throw new RuntimeException(var5);
      }
   }
}
