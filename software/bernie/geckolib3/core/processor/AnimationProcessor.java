package software.bernie.geckolib3.core.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.snapshot.DirtyTracker;
import software.bernie.geckolib3.core.util.MathUtil;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class AnimationProcessor<T extends IAnimatable> {
   public boolean reloadAnimations = false;
   private List<IBone> modelRendererList = new ArrayList();
   private double lastTickValue = -1.0D;
   private Set<Integer> animatedEntities = new HashSet();
   private final IAnimatableModel animatedModel;

   public AnimationProcessor(IAnimatableModel animatedModel) {
      this.animatedModel = animatedModel;
   }

   public void tickAnimation(IAnimatable entity, Integer uniqueID, double seekTime, AnimationEvent event, MolangParser parser, boolean crashWhenCantFindBone) {
      if (seekTime != this.lastTickValue) {
         this.animatedEntities.clear();
      } else if (this.animatedEntities.contains(uniqueID)) {
         return;
      }

      this.lastTickValue = seekTime;
      this.animatedEntities.add(uniqueID);
      AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
      HashMap<String, DirtyTracker> modelTracker = this.createNewDirtyTracker();
      this.updateBoneSnapshots(manager.getBoneSnapshotCollection());
      HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshots = manager.getBoneSnapshotCollection();
      Iterator var11 = manager.getAnimationControllers().values().iterator();

      Iterator var13;
      IBone model;
      BoneSnapshot initialSnapshot;
      BoneSnapshot saveSnapshot;
      while(var11.hasNext()) {
         AnimationController<T> controller = (AnimationController)var11.next();
         if (this.reloadAnimations) {
            controller.markNeedsReload();
            controller.getBoneAnimationQueues().clear();
         }

         controller.isJustStarting = manager.isFirstTick;
         event.setController(controller);
         controller.process(seekTime, event, this.modelRendererList, boneSnapshots, parser, crashWhenCantFindBone);
         var13 = controller.getBoneAnimationQueues().values().iterator();

         while(var13.hasNext()) {
            BoneAnimationQueue boneAnimation = (BoneAnimationQueue)var13.next();
            model = boneAnimation.bone;
            initialSnapshot = (BoneSnapshot)((Pair)boneSnapshots.get(model.getName())).getRight();
            saveSnapshot = model.getInitialSnapshot();
            AnimationPoint rXPoint = (AnimationPoint)boneAnimation.rotationXQueue.poll();
            AnimationPoint rYPoint = (AnimationPoint)boneAnimation.rotationYQueue.poll();
            AnimationPoint rZPoint = (AnimationPoint)boneAnimation.rotationZQueue.poll();
            AnimationPoint pXPoint = (AnimationPoint)boneAnimation.positionXQueue.poll();
            AnimationPoint pYPoint = (AnimationPoint)boneAnimation.positionYQueue.poll();
            AnimationPoint pZPoint = (AnimationPoint)boneAnimation.positionZQueue.poll();
            AnimationPoint sXPoint = (AnimationPoint)boneAnimation.scaleXQueue.poll();
            AnimationPoint sYPoint = (AnimationPoint)boneAnimation.scaleYQueue.poll();
            AnimationPoint sZPoint = (AnimationPoint)boneAnimation.scaleZQueue.poll();
            DirtyTracker dirtyTracker = (DirtyTracker)modelTracker.get(model.getName());
            if (dirtyTracker != null) {
               if (rXPoint != null && rYPoint != null && rZPoint != null) {
                  model.setRotationX(MathUtil.lerpValues(rXPoint, controller.easingType, controller.customEasingMethod) + saveSnapshot.rotationValueX);
                  model.setRotationY(MathUtil.lerpValues(rYPoint, controller.easingType, controller.customEasingMethod) + saveSnapshot.rotationValueY);
                  model.setRotationZ(MathUtil.lerpValues(rZPoint, controller.easingType, controller.customEasingMethod) + saveSnapshot.rotationValueZ);
                  initialSnapshot.rotationValueX = model.getRotationX();
                  initialSnapshot.rotationValueY = model.getRotationY();
                  initialSnapshot.rotationValueZ = model.getRotationZ();
                  initialSnapshot.isCurrentlyRunningRotationAnimation = true;
                  dirtyTracker.hasRotationChanged = true;
               }

               if (pXPoint != null && pYPoint != null && pZPoint != null) {
                  model.setPositionX(MathUtil.lerpValues(pXPoint, controller.easingType, controller.customEasingMethod));
                  model.setPositionY(MathUtil.lerpValues(pYPoint, controller.easingType, controller.customEasingMethod));
                  model.setPositionZ(MathUtil.lerpValues(pZPoint, controller.easingType, controller.customEasingMethod));
                  initialSnapshot.positionOffsetX = model.getPositionX();
                  initialSnapshot.positionOffsetY = model.getPositionY();
                  initialSnapshot.positionOffsetZ = model.getPositionZ();
                  initialSnapshot.isCurrentlyRunningPositionAnimation = true;
                  dirtyTracker.hasPositionChanged = true;
               }

               if (sXPoint != null && sYPoint != null && sZPoint != null) {
                  model.setScaleX(MathUtil.lerpValues(sXPoint, controller.easingType, controller.customEasingMethod));
                  model.setScaleY(MathUtil.lerpValues(sYPoint, controller.easingType, controller.customEasingMethod));
                  model.setScaleZ(MathUtil.lerpValues(sZPoint, controller.easingType, controller.customEasingMethod));
                  initialSnapshot.scaleValueX = model.getScaleX();
                  initialSnapshot.scaleValueY = model.getScaleY();
                  initialSnapshot.scaleValueZ = model.getScaleZ();
                  initialSnapshot.isCurrentlyRunningScaleAnimation = true;
                  dirtyTracker.hasScaleChanged = true;
               }
            }
         }
      }

      this.reloadAnimations = false;
      double resetTickLength = manager.getResetSpeed();
      var13 = modelTracker.entrySet().iterator();

      while(var13.hasNext()) {
         Entry<String, DirtyTracker> tracker = (Entry)var13.next();
         model = ((DirtyTracker)tracker.getValue()).model;
         initialSnapshot = model.getInitialSnapshot();
         saveSnapshot = (BoneSnapshot)((Pair)boneSnapshots.get(tracker.getKey())).getRight();
         if (saveSnapshot == null) {
            if (crashWhenCantFindBone) {
               throw new RuntimeException("Could not find save snapshot for bone: " + ((DirtyTracker)tracker.getValue()).model.getName() + ". Please don't add bones that are used in an animation at runtime.");
            }
         } else {
            double percentageReset;
            if (!((DirtyTracker)tracker.getValue()).hasRotationChanged) {
               if (saveSnapshot.isCurrentlyRunningRotationAnimation) {
                  saveSnapshot.mostRecentResetRotationTick = (float)seekTime;
                  saveSnapshot.isCurrentlyRunningRotationAnimation = false;
               }

               percentageReset = Math.min((seekTime - (double)saveSnapshot.mostRecentResetRotationTick) / resetTickLength, 1.0D);
               model.setRotationX(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.rotationValueX, (double)initialSnapshot.rotationValueX));
               model.setRotationY(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.rotationValueY, (double)initialSnapshot.rotationValueY));
               model.setRotationZ(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.rotationValueZ, (double)initialSnapshot.rotationValueZ));
               if (percentageReset >= 1.0D) {
                  saveSnapshot.rotationValueX = model.getRotationX();
                  saveSnapshot.rotationValueY = model.getRotationY();
                  saveSnapshot.rotationValueZ = model.getRotationZ();
               }
            }

            if (!((DirtyTracker)tracker.getValue()).hasPositionChanged) {
               if (saveSnapshot.isCurrentlyRunningPositionAnimation) {
                  saveSnapshot.mostRecentResetPositionTick = (float)seekTime;
                  saveSnapshot.isCurrentlyRunningPositionAnimation = false;
               }

               percentageReset = Math.min((seekTime - (double)saveSnapshot.mostRecentResetPositionTick) / resetTickLength, 1.0D);
               model.setPositionX(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.positionOffsetX, (double)initialSnapshot.positionOffsetX));
               model.setPositionY(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.positionOffsetY, (double)initialSnapshot.positionOffsetY));
               model.setPositionZ(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.positionOffsetZ, (double)initialSnapshot.positionOffsetZ));
               if (percentageReset >= 1.0D) {
                  saveSnapshot.positionOffsetX = model.getPositionX();
                  saveSnapshot.positionOffsetY = model.getPositionY();
                  saveSnapshot.positionOffsetZ = model.getPositionZ();
               }
            }

            if (!((DirtyTracker)tracker.getValue()).hasScaleChanged) {
               if (saveSnapshot.isCurrentlyRunningScaleAnimation) {
                  saveSnapshot.mostRecentResetScaleTick = (float)seekTime;
                  saveSnapshot.isCurrentlyRunningScaleAnimation = false;
               }

               percentageReset = Math.min((seekTime - (double)saveSnapshot.mostRecentResetScaleTick) / resetTickLength, 1.0D);
               model.setScaleX(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.scaleValueX, (double)initialSnapshot.scaleValueX));
               model.setScaleY(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.scaleValueY, (double)initialSnapshot.scaleValueY));
               model.setScaleZ(MathUtil.lerpValues(percentageReset, (double)saveSnapshot.scaleValueZ, (double)initialSnapshot.scaleValueZ));
               if (percentageReset >= 1.0D) {
                  saveSnapshot.scaleValueX = model.getScaleX();
                  saveSnapshot.scaleValueY = model.getScaleY();
                  saveSnapshot.scaleValueZ = model.getScaleZ();
               }
            }
         }
      }

      manager.isFirstTick = false;
   }

   private HashMap<String, DirtyTracker> createNewDirtyTracker() {
      HashMap<String, DirtyTracker> tracker = new HashMap();
      Iterator var2 = this.modelRendererList.iterator();

      while(var2.hasNext()) {
         IBone bone = (IBone)var2.next();
         tracker.put(bone.getName(), new DirtyTracker(false, false, false, bone));
      }

      return tracker;
   }

   private void updateBoneSnapshots(HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
      Iterator var2 = this.modelRendererList.iterator();

      while(var2.hasNext()) {
         IBone bone = (IBone)var2.next();
         if (!boneSnapshotCollection.containsKey(bone.getName())) {
            boneSnapshotCollection.put(bone.getName(), Pair.of(bone, new BoneSnapshot(bone.getInitialSnapshot())));
         }
      }

   }

   public IBone getBone(String boneName) {
      return (IBone)this.modelRendererList.stream().filter((x) -> {
         return x.getName().equals(boneName);
      }).findFirst().orElse((Object)null);
   }

   public void registerModelRenderer(IBone modelRenderer) {
      modelRenderer.saveInitialSnapshot();
      this.modelRendererList.add(modelRenderer);
   }

   public void clearModelRendererList() {
      this.modelRendererList.clear();
   }

   public List<IBone> getModelRendererList() {
      return this.modelRendererList;
   }

   public void preAnimationSetup(IAnimatable animatable, double seekTime) {
      this.animatedModel.setMolangQueries(animatable, seekTime);
   }
}
