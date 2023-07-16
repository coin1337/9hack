package software.bernie.geckolib3.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrameLocation;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;
import software.bernie.geckolib3.core.keyframe.VectorKeyFrameList;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.util.Axis;
import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class AnimationController<T extends IAnimatable> {
   static List<AnimationController.ModelFetcher<?>> modelFetchers = new ArrayList();
   protected T animatable;
   protected AnimationController.IAnimationPredicate<T> animationPredicate;
   private final String name;
   protected AnimationState animationState;
   public double transitionLengthTicks;
   private AnimationController.ISoundListener<T> soundListener;
   private AnimationController.IParticleListener<T> particleListener;
   private AnimationController.ICustomInstructionListener<T> customInstructionListener;
   public boolean isJustStarting;
   private final HashMap<String, BoneAnimationQueue> boneAnimationQueues;
   public double tickOffset;
   protected Queue<Animation> animationQueue;
   protected Animation currentAnimation;
   protected AnimationBuilder currentAnimationBuilder;
   protected boolean shouldResetTick;
   private final HashMap<String, BoneSnapshot> boneSnapshots;
   private boolean justStopped;
   protected boolean justStartedTransition;
   public Function<Double, Double> customEasingMethod;
   protected boolean needsAnimationReload;
   public double animationSpeed;
   private final Set<EventKeyFrame<?>> executedKeyFrames;
   public EasingType easingType;

   public static void addModelFetcher(AnimationController.ModelFetcher<?> fetcher) {
      modelFetchers.add(fetcher);
   }

   public static void removeModelFetcher(AnimationController.ModelFetcher<?> fetcher) {
      Objects.requireNonNull(fetcher);
      modelFetchers.remove(fetcher);
   }

   public void setAnimation(AnimationBuilder builder) {
      IAnimatableModel<T> model = this.getModel(this.animatable);
      if (model != null) {
         if (builder != null && builder.getRawAnimationList().size() != 0) {
            if (!builder.getRawAnimationList().equals(this.currentAnimationBuilder.getRawAnimationList()) || this.needsAnimationReload) {
               AtomicBoolean encounteredError = new AtomicBoolean(false);
               LinkedList<Animation> animations = (LinkedList)builder.getRawAnimationList().stream().map((rawAnimation) -> {
                  Animation animation = model.getAnimation(rawAnimation.animationName, this.animatable);
                  if (animation == null) {
                     System.out.printf("Could not load animation: %s. Is it missing?", rawAnimation.animationName);
                     encounteredError.set(true);
                  }

                  if (animation != null && rawAnimation.loop != null) {
                     animation.loop = rawAnimation.loop;
                  }

                  return animation;
               }).collect(Collectors.toCollection(LinkedList::new));
               if (encounteredError.get()) {
                  return;
               }

               this.animationQueue = animations;
               this.currentAnimationBuilder = builder;
               this.shouldResetTick = true;
               this.animationState = AnimationState.Transitioning;
               this.justStartedTransition = true;
               this.needsAnimationReload = false;
            }
         } else {
            this.animationState = AnimationState.Stopped;
         }
      }

   }

   public AnimationController(T animatable, String name, float transitionLengthTicks, AnimationController.IAnimationPredicate<T> animationPredicate) {
      this.animationState = AnimationState.Stopped;
      this.isJustStarting = false;
      this.boneAnimationQueues = new HashMap();
      this.animationQueue = new LinkedList();
      this.currentAnimationBuilder = new AnimationBuilder();
      this.shouldResetTick = false;
      this.boneSnapshots = new HashMap();
      this.justStopped = false;
      this.justStartedTransition = false;
      this.needsAnimationReload = false;
      this.animationSpeed = 1.0D;
      this.executedKeyFrames = new HashSet();
      this.easingType = EasingType.NONE;
      this.animatable = animatable;
      this.name = name;
      this.transitionLengthTicks = (double)transitionLengthTicks;
      this.animationPredicate = animationPredicate;
      this.tickOffset = 0.0D;
   }

   public AnimationController(T animatable, String name, float transitionLengthTicks, EasingType easingtype, AnimationController.IAnimationPredicate<T> animationPredicate) {
      this.animationState = AnimationState.Stopped;
      this.isJustStarting = false;
      this.boneAnimationQueues = new HashMap();
      this.animationQueue = new LinkedList();
      this.currentAnimationBuilder = new AnimationBuilder();
      this.shouldResetTick = false;
      this.boneSnapshots = new HashMap();
      this.justStopped = false;
      this.justStartedTransition = false;
      this.needsAnimationReload = false;
      this.animationSpeed = 1.0D;
      this.executedKeyFrames = new HashSet();
      this.easingType = EasingType.NONE;
      this.animatable = animatable;
      this.name = name;
      this.transitionLengthTicks = (double)transitionLengthTicks;
      this.easingType = easingtype;
      this.animationPredicate = animationPredicate;
      this.tickOffset = 0.0D;
   }

   public AnimationController(T animatable, String name, float transitionLengthTicks, Function<Double, Double> customEasingMethod, AnimationController.IAnimationPredicate<T> animationPredicate) {
      this.animationState = AnimationState.Stopped;
      this.isJustStarting = false;
      this.boneAnimationQueues = new HashMap();
      this.animationQueue = new LinkedList();
      this.currentAnimationBuilder = new AnimationBuilder();
      this.shouldResetTick = false;
      this.boneSnapshots = new HashMap();
      this.justStopped = false;
      this.justStartedTransition = false;
      this.needsAnimationReload = false;
      this.animationSpeed = 1.0D;
      this.executedKeyFrames = new HashSet();
      this.easingType = EasingType.NONE;
      this.animatable = animatable;
      this.name = name;
      this.transitionLengthTicks = (double)transitionLengthTicks;
      this.customEasingMethod = customEasingMethod;
      this.easingType = EasingType.CUSTOM;
      this.animationPredicate = animationPredicate;
      this.tickOffset = 0.0D;
   }

   public String getName() {
      return this.name;
   }

   public Animation getCurrentAnimation() {
      return this.currentAnimation;
   }

   public AnimationState getAnimationState() {
      return this.animationState;
   }

   public HashMap<String, BoneAnimationQueue> getBoneAnimationQueues() {
      return this.boneAnimationQueues;
   }

   public void registerSoundListener(AnimationController.ISoundListener<T> soundListener) {
      this.soundListener = soundListener;
   }

   public void registerParticleListener(AnimationController.IParticleListener<T> particleListener) {
      this.particleListener = particleListener;
   }

   public void registerCustomInstructionListener(AnimationController.ICustomInstructionListener<T> customInstructionListener) {
      this.customInstructionListener = customInstructionListener;
   }

   public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList, HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser, boolean crashWhenCantFindBone) {
      if (this.currentAnimation != null) {
         IAnimatableModel<T> model = this.getModel(this.animatable);
         if (model != null) {
            Animation animation = model.getAnimation(this.currentAnimation.animationName, this.animatable);
            if (animation != null) {
               boolean loop = this.currentAnimation.loop;
               this.currentAnimation = animation;
               this.currentAnimation.loop = loop;
            }
         }
      }

      this.createInitialQueues(modelRendererList);
      double actualTick = tick;
      tick = this.adjustTick(tick);
      if (this.animationState == AnimationState.Transitioning && tick >= this.transitionLengthTicks) {
         this.shouldResetTick = true;
         this.animationState = AnimationState.Running;
         tick = this.adjustTick(actualTick);
      }

      assert tick >= 0.0D : "GeckoLib: Tick was less than zero";

      PlayState playState = this.testAnimationPredicate(event);
      if (playState != PlayState.STOP && (this.currentAnimation != null || this.animationQueue.size() != 0)) {
         if (!this.justStartedTransition || !this.shouldResetTick && !this.justStopped) {
            if (this.currentAnimation == null && this.animationQueue.size() != 0) {
               this.shouldResetTick = true;
               this.animationState = AnimationState.Transitioning;
               this.justStartedTransition = true;
               this.needsAnimationReload = false;
               tick = this.adjustTick(actualTick);
            } else if (this.animationState != AnimationState.Transitioning) {
               this.animationState = AnimationState.Running;
            }
         } else {
            this.justStopped = false;
            tick = this.adjustTick(actualTick);
         }

         if (this.animationState == AnimationState.Transitioning) {
            if (tick == 0.0D || this.isJustStarting) {
               this.justStartedTransition = false;
               this.currentAnimation = (Animation)this.animationQueue.poll();
               this.resetEventKeyFrames();
               this.saveSnapshotsForAnimation(this.currentAnimation, boneSnapshotCollection);
            }

            if (this.currentAnimation != null) {
               this.setAnimTime(parser, 0.0D);
               Iterator var11 = this.currentAnimation.boneAnimations.iterator();

               while(var11.hasNext()) {
                  BoneAnimation boneAnimation = (BoneAnimation)var11.next();
                  BoneAnimationQueue boneAnimationQueue = (BoneAnimationQueue)this.boneAnimationQueues.get(boneAnimation.boneName);
                  BoneSnapshot boneSnapshot = (BoneSnapshot)this.boneSnapshots.get(boneAnimation.boneName);
                  Optional<IBone> first = modelRendererList.stream().filter((x) -> {
                     return x.getName().equals(boneAnimation.boneName);
                  }).findFirst();
                  if (!first.isPresent()) {
                     if (crashWhenCantFindBone) {
                        throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);
                     }
                  } else {
                     BoneSnapshot initialSnapshot = ((IBone)first.get()).getInitialSnapshot();

                     assert boneSnapshot != null : "Bone snapshot was null";

                     VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
                     VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames;
                     VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames;
                     AnimationPoint xPoint;
                     AnimationPoint yPoint;
                     AnimationPoint zPoint;
                     if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
                        xPoint = this.getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, 0.0D, true, Axis.X);
                        yPoint = this.getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, 0.0D, true, Axis.Y);
                        zPoint = this.getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, 0.0D, true, Axis.Z);
                        boneAnimationQueue.rotationXQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.rotationValueX - initialSnapshot.rotationValueX, xPoint.animationStartValue));
                        boneAnimationQueue.rotationYQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.rotationValueY - initialSnapshot.rotationValueY, yPoint.animationStartValue));
                        boneAnimationQueue.rotationZQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.rotationValueZ - initialSnapshot.rotationValueZ, zPoint.animationStartValue));
                     }

                     if (!positionKeyFrames.xKeyFrames.isEmpty()) {
                        xPoint = this.getAnimationPointAtTick(positionKeyFrames.xKeyFrames, 0.0D, true, Axis.X);
                        yPoint = this.getAnimationPointAtTick(positionKeyFrames.yKeyFrames, 0.0D, true, Axis.Y);
                        zPoint = this.getAnimationPointAtTick(positionKeyFrames.zKeyFrames, 0.0D, true, Axis.Z);
                        boneAnimationQueue.positionXQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.positionOffsetX, xPoint.animationStartValue));
                        boneAnimationQueue.positionYQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.positionOffsetY, yPoint.animationStartValue));
                        boneAnimationQueue.positionZQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.positionOffsetZ, zPoint.animationStartValue));
                     }

                     if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
                        xPoint = this.getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, 0.0D, true, Axis.X);
                        yPoint = this.getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, 0.0D, true, Axis.Y);
                        zPoint = this.getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, 0.0D, true, Axis.Z);
                        boneAnimationQueue.scaleXQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.scaleValueX, xPoint.animationStartValue));
                        boneAnimationQueue.scaleYQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.scaleValueY, yPoint.animationStartValue));
                        boneAnimationQueue.scaleZQueue.add(new AnimationPoint((KeyFrame)null, tick, this.transitionLengthTicks, boneSnapshot.scaleValueZ, zPoint.animationStartValue));
                     }
                  }
               }
            }
         } else if (this.getAnimationState() == AnimationState.Running) {
            this.processCurrentAnimation(tick, actualTick, parser, crashWhenCantFindBone);
         }

      } else {
         this.animationState = AnimationState.Stopped;
         this.justStopped = true;
      }
   }

   private void setAnimTime(MolangParser parser, double tick) {
      parser.setValue("query.anim_time", tick / 20.0D);
      parser.setValue("query.life_time", tick / 20.0D);
   }

   private IAnimatableModel<T> getModel(T animatable) {
      Iterator var2 = modelFetchers.iterator();

      IAnimatableModel model;
      do {
         if (!var2.hasNext()) {
            System.out.printf("Could not find suitable model for animatable of type %s. Did you register a Model Fetcher?%n", animatable.getClass());
            return null;
         }

         AnimationController.ModelFetcher<?> modelFetcher = (AnimationController.ModelFetcher)var2.next();
         model = (IAnimatableModel)modelFetcher.apply(animatable);
      } while(model == null);

      return model;
   }

   protected PlayState testAnimationPredicate(AnimationEvent<T> event) {
      return this.animationPredicate.test(event);
   }

   private void saveSnapshotsForAnimation(Animation animation, HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
      Iterator var3 = boneSnapshotCollection.values().iterator();

      while(var3.hasNext()) {
         Pair<IBone, BoneSnapshot> snapshot = (Pair)var3.next();
         if (animation != null && animation.boneAnimations != null && animation.boneAnimations.stream().anyMatch((x) -> {
            return x.boneName.equals(((IBone)snapshot.getLeft()).getName());
         })) {
            this.boneSnapshots.put(((IBone)snapshot.getLeft()).getName(), new BoneSnapshot((BoneSnapshot)snapshot.getRight()));
         }
      }

   }

   private void processCurrentAnimation(double tick, double actualTick, MolangParser parser, boolean crashWhenCantFindBone) {
      assert this.currentAnimation != null;

      if (tick >= this.currentAnimation.animationLength) {
         this.resetEventKeyFrames();
         if (!this.currentAnimation.loop) {
            Animation peek = (Animation)this.animationQueue.peek();
            if (peek == null) {
               this.animationState = AnimationState.Stopped;
               return;
            }

            this.animationState = AnimationState.Transitioning;
            this.shouldResetTick = true;
            this.currentAnimation = (Animation)this.animationQueue.peek();
         } else {
            this.shouldResetTick = true;
            tick = this.adjustTick(actualTick);
         }
      }

      this.setAnimTime(parser, tick);
      List<BoneAnimation> boneAnimations = this.currentAnimation.boneAnimations;
      Iterator var8 = boneAnimations.iterator();

      while(var8.hasNext()) {
         BoneAnimation boneAnimation = (BoneAnimation)var8.next();
         BoneAnimationQueue boneAnimationQueue = (BoneAnimationQueue)this.boneAnimationQueues.get(boneAnimation.boneName);
         if (boneAnimationQueue == null) {
            if (crashWhenCantFindBone) {
               throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);
            }
         } else {
            VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
            VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames;
            VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames;
            if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
               boneAnimationQueue.rotationXQueue.add(this.getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, tick, true, Axis.X));
               boneAnimationQueue.rotationYQueue.add(this.getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, tick, true, Axis.Y));
               boneAnimationQueue.rotationZQueue.add(this.getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, tick, true, Axis.Z));
            }

            if (!positionKeyFrames.xKeyFrames.isEmpty()) {
               boneAnimationQueue.positionXQueue.add(this.getAnimationPointAtTick(positionKeyFrames.xKeyFrames, tick, false, Axis.X));
               boneAnimationQueue.positionYQueue.add(this.getAnimationPointAtTick(positionKeyFrames.yKeyFrames, tick, false, Axis.Y));
               boneAnimationQueue.positionZQueue.add(this.getAnimationPointAtTick(positionKeyFrames.zKeyFrames, tick, false, Axis.Z));
            }

            if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
               boneAnimationQueue.scaleXQueue.add(this.getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, tick, false, Axis.X));
               boneAnimationQueue.scaleYQueue.add(this.getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, tick, false, Axis.Y));
               boneAnimationQueue.scaleZQueue.add(this.getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, tick, false, Axis.Z));
            }
         }
      }

      if (this.soundListener != null || this.particleListener != null || this.customInstructionListener != null) {
         var8 = this.currentAnimation.soundKeyFrames.iterator();

         EventKeyFrame customInstructionKeyFrame;
         while(var8.hasNext()) {
            customInstructionKeyFrame = (EventKeyFrame)var8.next();
            if (!this.executedKeyFrames.contains(customInstructionKeyFrame) && tick >= customInstructionKeyFrame.getStartTick()) {
               SoundKeyframeEvent<T> event = new SoundKeyframeEvent(this.animatable, tick, (String)customInstructionKeyFrame.getEventData(), this);
               this.soundListener.playSound(event);
               this.executedKeyFrames.add(customInstructionKeyFrame);
            }
         }

         var8 = this.currentAnimation.particleKeyFrames.iterator();

         while(var8.hasNext()) {
            ParticleEventKeyFrame particleEventKeyFrame = (ParticleEventKeyFrame)var8.next();
            if (!this.executedKeyFrames.contains(particleEventKeyFrame) && tick >= particleEventKeyFrame.getStartTick()) {
               ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent(this.animatable, tick, particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script, this);
               this.particleListener.summonParticle(event);
               this.executedKeyFrames.add(particleEventKeyFrame);
            }
         }

         var8 = this.currentAnimation.customInstructionKeyframes.iterator();

         while(var8.hasNext()) {
            customInstructionKeyFrame = (EventKeyFrame)var8.next();
            if (!this.executedKeyFrames.contains(customInstructionKeyFrame) && tick >= customInstructionKeyFrame.getStartTick()) {
               CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent(this.animatable, tick, (String)customInstructionKeyFrame.getEventData(), this);
               this.customInstructionListener.executeInstruction(event);
               this.executedKeyFrames.add(customInstructionKeyFrame);
            }
         }
      }

      if (this.transitionLengthTicks == 0.0D && this.shouldResetTick && this.animationState == AnimationState.Transitioning) {
         this.currentAnimation = (Animation)this.animationQueue.poll();
      }

   }

   private void createInitialQueues(List<IBone> modelRendererList) {
      this.boneAnimationQueues.clear();
      Iterator var2 = modelRendererList.iterator();

      while(var2.hasNext()) {
         IBone modelRenderer = (IBone)var2.next();
         this.boneAnimationQueues.put(modelRenderer.getName(), new BoneAnimationQueue(modelRenderer));
      }

   }

   protected double adjustTick(double tick) {
      if (this.shouldResetTick) {
         if (this.getAnimationState() == AnimationState.Transitioning) {
            this.tickOffset = tick;
         } else if (this.getAnimationState() == AnimationState.Running) {
            this.tickOffset = tick;
         }

         this.shouldResetTick = false;
         return 0.0D;
      } else {
         return this.animationSpeed * Math.max(tick - this.tickOffset, 0.0D);
      }
   }

   private AnimationPoint getAnimationPointAtTick(List<KeyFrame<IValue>> frames, double tick, boolean isRotation, Axis axis) {
      KeyFrameLocation<KeyFrame<IValue>> location = this.getCurrentKeyFrameLocation(frames, tick);
      KeyFrame<IValue> currentFrame = location.currentFrame;
      double startValue = ((IValue)currentFrame.getStartValue()).get();
      double endValue = ((IValue)currentFrame.getEndValue()).get();
      if (isRotation) {
         if (!(currentFrame.getStartValue() instanceof ConstantValue)) {
            startValue = Math.toRadians(startValue);
            if (axis == Axis.X || axis == Axis.Y) {
               startValue *= -1.0D;
            }
         }

         if (!(currentFrame.getEndValue() instanceof ConstantValue)) {
            endValue = Math.toRadians(endValue);
            if (axis == Axis.X || axis == Axis.Y) {
               endValue *= -1.0D;
            }
         }
      }

      return new AnimationPoint(currentFrame, location.currentTick, currentFrame.getLength(), startValue, endValue);
   }

   private KeyFrameLocation<KeyFrame<IValue>> getCurrentKeyFrameLocation(List<KeyFrame<IValue>> frames, double ageInTicks) {
      double totalTimeTracker = 0.0D;
      Iterator var6 = frames.iterator();

      KeyFrame frame;
      do {
         if (!var6.hasNext()) {
            return new KeyFrameLocation((KeyFrame)frames.get(frames.size() - 1), ageInTicks);
         }

         frame = (KeyFrame)var6.next();
         totalTimeTracker += frame.getLength();
      } while(!(totalTimeTracker > ageInTicks));

      double tick = ageInTicks - (totalTimeTracker - frame.getLength());
      return new KeyFrameLocation(frame, tick);
   }

   private void resetEventKeyFrames() {
      this.executedKeyFrames.clear();
   }

   public void markNeedsReload() {
      this.needsAnimationReload = true;
   }

   public void clearAnimationCache() {
      this.currentAnimationBuilder = new AnimationBuilder();
   }

   public double getAnimationSpeed() {
      return this.animationSpeed;
   }

   public void setAnimationSpeed(double animationSpeed) {
      this.animationSpeed = animationSpeed;
   }

   @FunctionalInterface
   public interface ModelFetcher<T> extends Function<IAnimatable, IAnimatableModel<T>> {
   }

   @FunctionalInterface
   public interface ICustomInstructionListener<A extends IAnimatable> {
      void executeInstruction(CustomInstructionKeyframeEvent<A> var1);
   }

   @FunctionalInterface
   public interface IParticleListener<A extends IAnimatable> {
      void summonParticle(ParticleKeyFrameEvent<A> var1);
   }

   @FunctionalInterface
   public interface ISoundListener<A extends IAnimatable> {
      void playSound(SoundKeyframeEvent<A> var1);
   }

   @FunctionalInterface
   public interface IAnimationPredicate<P extends IAnimatable> {
      PlayState test(AnimationEvent<P> var1);
   }
}
