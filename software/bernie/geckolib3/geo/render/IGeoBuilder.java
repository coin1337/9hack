package software.bernie.geckolib3.geo.render;

import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public interface IGeoBuilder {
   GeoModel constructGeoModel(RawGeometryTree var1);

   GeoBone constructBone(RawBoneGroup var1, ModelProperties var2, GeoBone var3);
}
