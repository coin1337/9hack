package software.bernie.geckolib3.geo.render.built;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;

public class GeoModel {
   public List<GeoBone> topLevelBones = new ArrayList();
   public ModelProperties properties;

   public Optional<GeoBone> getBone(String name) {
      Iterator var2 = this.topLevelBones.iterator();

      GeoBone optionalBone;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         GeoBone bone = (GeoBone)var2.next();
         optionalBone = this.getBoneRecursively(name, bone);
      } while(optionalBone == null);

      return Optional.of(optionalBone);
   }

   private GeoBone getBoneRecursively(String name, GeoBone bone) {
      if (bone.name.equals(name)) {
         return bone;
      } else {
         Iterator var3 = bone.childBones.iterator();

         GeoBone optionalBone;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            GeoBone childBone = (GeoBone)var3.next();
            if (childBone.name.equals(name)) {
               return childBone;
            }

            optionalBone = this.getBoneRecursively(name, childBone);
         } while(optionalBone == null);

         return optionalBone;
      }
   }
}
