package software.bernie.geckolib3.util.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.client.util.JsonException;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;
import software.bernie.geckolib3.core.keyframe.VectorKeyFrameList;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class JsonAnimationUtils {
   public static Set<Entry<String, JsonElement>> getAnimations(JsonObject json) {
      return getObjectListAsArray(json.getAsJsonObject("animations"));
   }

   public static List<Entry<String, JsonElement>> getBones(JsonObject json) {
      JsonObject bones = json.getAsJsonObject("bones");
      return bones == null ? new ArrayList() : new ArrayList(getObjectListAsArray(bones));
   }

   public static Set<Entry<String, JsonElement>> getRotationKeyFrames(JsonObject json) {
      JsonElement rotationObject = json.get("rotation");
      if (rotationObject.isJsonArray()) {
         return ImmutableSet.of(new SimpleEntry("0", rotationObject.getAsJsonArray()));
      } else if (rotationObject.isJsonPrimitive()) {
         JsonPrimitive primitive = rotationObject.getAsJsonPrimitive();
         Gson gson = new Gson();
         JsonElement jsonElement = gson.toJsonTree(Arrays.asList(primitive, primitive, primitive));
         return ImmutableSet.of(new SimpleEntry("0", jsonElement));
      } else {
         return getObjectListAsArray(rotationObject.getAsJsonObject());
      }
   }

   public static Set<Entry<String, JsonElement>> getPositionKeyFrames(JsonObject json) {
      JsonElement positionObject = json.get("position");
      if (positionObject.isJsonArray()) {
         return ImmutableSet.of(new SimpleEntry("0", positionObject.getAsJsonArray()));
      } else if (positionObject.isJsonPrimitive()) {
         JsonPrimitive primitive = positionObject.getAsJsonPrimitive();
         Gson gson = new Gson();
         JsonElement jsonElement = gson.toJsonTree(Arrays.asList(primitive, primitive, primitive));
         return ImmutableSet.of(new SimpleEntry("0", jsonElement));
      } else {
         return getObjectListAsArray(positionObject.getAsJsonObject());
      }
   }

   public static Set<Entry<String, JsonElement>> getScaleKeyFrames(JsonObject json) {
      JsonElement scaleObject = json.get("scale");
      if (scaleObject.isJsonArray()) {
         return ImmutableSet.of(new SimpleEntry("0", scaleObject.getAsJsonArray()));
      } else if (scaleObject.isJsonPrimitive()) {
         JsonPrimitive primitive = scaleObject.getAsJsonPrimitive();
         Gson gson = new Gson();
         JsonElement jsonElement = gson.toJsonTree(Arrays.asList(primitive, primitive, primitive));
         return ImmutableSet.of(new SimpleEntry("0", jsonElement));
      } else {
         return getObjectListAsArray(scaleObject.getAsJsonObject());
      }
   }

   public static ArrayList<Entry<String, JsonElement>> getSoundEffectFrames(JsonObject json) {
      JsonObject sound_effects = json.getAsJsonObject("sound_effects");
      return sound_effects == null ? new ArrayList() : new ArrayList(getObjectListAsArray(sound_effects));
   }

   public static ArrayList<Entry<String, JsonElement>> getParticleEffectFrames(JsonObject json) {
      JsonObject particle_effects = json.getAsJsonObject("particle_effects");
      return particle_effects == null ? new ArrayList() : new ArrayList(getObjectListAsArray(particle_effects));
   }

   public static ArrayList<Entry<String, JsonElement>> getCustomInstructionKeyFrames(JsonObject json) {
      JsonObject custom_instructions = json.getAsJsonObject("timeline");
      return custom_instructions == null ? new ArrayList() : new ArrayList(getObjectListAsArray(custom_instructions));
   }

   private static JsonElement getObjectByKey(Set<Entry<String, JsonElement>> json, String key) throws JsonException {
      return (JsonElement)((Entry)json.stream().filter((x) -> {
         return ((String)x.getKey()).equals(key);
      }).findFirst().orElseThrow(() -> {
         return new JsonException("Could not find key: " + key);
      })).getValue();
   }

   public static Entry<String, JsonElement> getAnimation(JsonObject animationFile, String animationName) throws JsonException {
      return new SimpleEntry(animationName, getObjectByKey(getAnimations(animationFile), animationName));
   }

   public static Set<Entry<String, JsonElement>> getObjectListAsArray(JsonObject json) {
      return json.entrySet();
   }

   public static Animation deserializeJsonToAnimation(Entry<String, JsonElement> element, MolangParser parser) throws ClassCastException, IllegalStateException {
      Animation animation = new Animation();
      JsonObject animationJsonObject = ((JsonElement)element.getValue()).getAsJsonObject();
      animation.animationName = (String)element.getKey();
      JsonElement animation_length = animationJsonObject.get("animation_length");
      animation.animationLength = animation_length == null ? null : AnimationUtils.convertSecondsToTicks(animation_length.getAsDouble());
      animation.boneAnimations = new ArrayList();
      JsonElement loop = animationJsonObject.get("loop");
      animation.loop = loop != null && loop.getAsBoolean();
      ArrayList<Entry<String, JsonElement>> soundEffectFrames = getSoundEffectFrames(animationJsonObject);
      if (soundEffectFrames != null) {
         Iterator var7 = soundEffectFrames.iterator();

         while(var7.hasNext()) {
            Entry<String, JsonElement> keyFrame = (Entry)var7.next();
            animation.soundKeyFrames.add(new EventKeyFrame(Double.parseDouble((String)keyFrame.getKey()) * 20.0D, ((JsonElement)keyFrame.getValue()).getAsJsonObject().get("effect").getAsString()));
         }
      }

      ArrayList<Entry<String, JsonElement>> particleKeyFrames = getParticleEffectFrames(animationJsonObject);
      if (particleKeyFrames != null) {
         Iterator var19 = particleKeyFrames.iterator();

         while(var19.hasNext()) {
            Entry<String, JsonElement> keyFrame = (Entry)var19.next();
            JsonObject object = ((JsonElement)keyFrame.getValue()).getAsJsonObject();
            JsonElement effect = object.get("effect");
            JsonElement locator = object.get("locator");
            JsonElement pre_effect_script = object.get("pre_effect_script");
            animation.particleKeyFrames.add(new ParticleEventKeyFrame(Double.parseDouble((String)keyFrame.getKey()) * 20.0D, effect == null ? "" : effect.getAsString(), locator == null ? "" : locator.getAsString(), pre_effect_script == null ? "" : pre_effect_script.getAsString()));
         }
      }

      ArrayList<Entry<String, JsonElement>> customInstructionKeyFrames = getCustomInstructionKeyFrames(animationJsonObject);
      if (customInstructionKeyFrames != null) {
         Iterator var21 = customInstructionKeyFrames.iterator();

         while(var21.hasNext()) {
            Entry<String, JsonElement> keyFrame = (Entry)var21.next();
            animation.customInstructionKeyframes.add(new EventKeyFrame(Double.parseDouble((String)keyFrame.getKey()) * 20.0D, keyFrame.getValue() instanceof JsonArray ? convertJsonArrayToList(((JsonElement)keyFrame.getValue()).getAsJsonArray()).toString() : ((JsonElement)keyFrame.getValue()).getAsString()));
         }
      }

      List<Entry<String, JsonElement>> bones = getBones(animationJsonObject);

      BoneAnimation boneAnimation;
      for(Iterator var24 = bones.iterator(); var24.hasNext(); animation.boneAnimations.add(boneAnimation)) {
         Entry<String, JsonElement> bone = (Entry)var24.next();
         boneAnimation = new BoneAnimation();
         boneAnimation.boneName = (String)bone.getKey();
         JsonObject boneJsonObj = ((JsonElement)bone.getValue()).getAsJsonObject();

         Set rotationKeyFramesJson;
         try {
            rotationKeyFramesJson = getScaleKeyFrames(boneJsonObj);
            boneAnimation.scaleKeyFrames = JsonKeyFrameUtils.convertJsonToKeyFrames(new ArrayList(rotationKeyFramesJson), parser);
         } catch (Exception var17) {
            boneAnimation.scaleKeyFrames = new VectorKeyFrameList();
         }

         try {
            rotationKeyFramesJson = getPositionKeyFrames(boneJsonObj);
            boneAnimation.positionKeyFrames = JsonKeyFrameUtils.convertJsonToKeyFrames(new ArrayList(rotationKeyFramesJson), parser);
         } catch (Exception var16) {
            boneAnimation.positionKeyFrames = new VectorKeyFrameList();
         }

         try {
            rotationKeyFramesJson = getRotationKeyFrames(boneJsonObj);
            boneAnimation.rotationKeyFrames = JsonKeyFrameUtils.convertJsonToRotationKeyFrames(new ArrayList(rotationKeyFramesJson), parser);
         } catch (Exception var15) {
            boneAnimation.rotationKeyFrames = new VectorKeyFrameList();
         }
      }

      if (animation.animationLength == null) {
         animation.animationLength = calculateLength(animation.boneAnimations);
      }

      return animation;
   }

   private static double calculateLength(List<BoneAnimation> boneAnimations) {
      double longestLength = 0.0D;

      double xKeyframeTime;
      double yKeyframeTime;
      double zKeyframeTime;
      for(Iterator var3 = boneAnimations.iterator(); var3.hasNext(); longestLength = maxAll(longestLength, xKeyframeTime, yKeyframeTime, zKeyframeTime)) {
         BoneAnimation animation = (BoneAnimation)var3.next();
         xKeyframeTime = animation.rotationKeyFrames.getLastKeyframeTime();
         yKeyframeTime = animation.positionKeyFrames.getLastKeyframeTime();
         zKeyframeTime = animation.scaleKeyFrames.getLastKeyframeTime();
      }

      return longestLength == 0.0D ? Double.MAX_VALUE : longestLength;
   }

   static List<IValue> convertJsonArrayToList(JsonArray array) {
      return (List)(new Gson()).fromJson(array, ArrayList.class);
   }

   public static double maxAll(double... values) {
      double max = 0.0D;
      double[] var3 = values;
      int var4 = values.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double value = var3[var5];
         max = Math.max(value, max);
      }

      return max;
   }
}
