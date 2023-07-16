package software.bernie.geckolib3.geo.raw.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.geo.raw.pojo.MinecraftGeometry;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;

public class RawGeometryTree {
   public HashMap<String, RawBoneGroup> topLevelBones = new HashMap();
   public ModelProperties properties;

   public static RawGeometryTree parseHierarchy(RawGeoModel model, ResourceLocation location) {
      RawGeometryTree hierarchy = new RawGeometryTree();
      MinecraftGeometry geometry = model.getMinecraftGeometry()[0];
      hierarchy.properties = geometry.getProperties();
      List<Bone> bones = new ArrayList(Arrays.asList(geometry.getBones()));
      int index = bones.size() - 1;
      int loopsWithoutChange = 0;

      while(true) {
         ++loopsWithoutChange;
         if (loopsWithoutChange > 10000) {
            GeckoLib.LOGGER.warn("Some bones in " + location.toString() + " do not have existing parents: ");
            GeckoLib.LOGGER.warn((String)bones.stream().map((x) -> {
               return x.getName();
            }).collect(Collectors.joining(", ")));
            break;
         }

         Bone bone = (Bone)bones.get(index);
         if (!hasParent(bone)) {
            hierarchy.topLevelBones.put(bone.getName(), new RawBoneGroup(bone));
            bones.remove(bone);
            loopsWithoutChange = 0;
         } else {
            RawBoneGroup groupFromHierarchy = getGroupFromHierarchy(hierarchy, bone.getParent());
            if (groupFromHierarchy != null) {
               groupFromHierarchy.children.put(bone.getName(), new RawBoneGroup(bone));
               bones.remove(bone);
               loopsWithoutChange = 0;
            }
         }

         if (index == 0) {
            index = bones.size() - 1;
            if (index == -1) {
               break;
            }
         } else {
            --index;
         }
      }

      return hierarchy;
   }

   public static boolean hasParent(Bone bone) {
      return bone.getParent() != null;
   }

   public static RawBoneGroup getGroupFromHierarchy(RawGeometryTree hierarchy, String bone) {
      HashMap<String, RawBoneGroup> flatList = new HashMap();
      Iterator var3 = hierarchy.topLevelBones.values().iterator();

      while(var3.hasNext()) {
         RawBoneGroup group = (RawBoneGroup)var3.next();
         flatList.put(group.selfBone.getName(), group);
         traverse(flatList, group);
      }

      return (RawBoneGroup)flatList.get(bone);
   }

   public static void traverse(HashMap<String, RawBoneGroup> flatList, RawBoneGroup group) {
      Iterator var2 = group.children.values().iterator();

      while(var2.hasNext()) {
         RawBoneGroup child = (RawBoneGroup)var2.next();
         flatList.put(child.selfBone.getName(), child);
         traverse(flatList, child);
      }

   }
}
