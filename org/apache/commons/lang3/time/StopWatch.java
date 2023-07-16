package org.apache.commons.lang3.time;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class StopWatch {
   private static final long NANO_2_MILLIS = 1000000L;
   private final String message;
   private StopWatch.State runningState;
   private StopWatch.SplitState splitState;
   private long startTime;
   private long startTimeMillis;
   private long stopTime;

   public static StopWatch create() {
      return new StopWatch();
   }

   public static StopWatch createStarted() {
      StopWatch sw = new StopWatch();
      sw.start();
      return sw;
   }

   public StopWatch() {
      this((String)null);
   }

   public StopWatch(String message) {
      this.runningState = StopWatch.State.UNSTARTED;
      this.splitState = StopWatch.SplitState.UNSPLIT;
      this.message = message;
   }

   public String formatSplitTime() {
      return DurationFormatUtils.formatDurationHMS(this.getSplitTime());
   }

   public String formatTime() {
      return DurationFormatUtils.formatDurationHMS(this.getTime());
   }

   public String getMessage() {
      return this.message;
   }

   public long getNanoTime() {
      if (this.runningState != StopWatch.State.STOPPED && this.runningState != StopWatch.State.SUSPENDED) {
         if (this.runningState == StopWatch.State.UNSTARTED) {
            return 0L;
         } else if (this.runningState == StopWatch.State.RUNNING) {
            return System.nanoTime() - this.startTime;
         } else {
            throw new RuntimeException("Illegal running state has occurred.");
         }
      } else {
         return this.stopTime - this.startTime;
      }
   }

   public long getSplitNanoTime() {
      if (this.splitState != StopWatch.SplitState.SPLIT) {
         throw new IllegalStateException("Stopwatch must be split to get the split time. ");
      } else {
         return this.stopTime - this.startTime;
      }
   }

   public long getSplitTime() {
      return this.getSplitNanoTime() / 1000000L;
   }

   public long getStartTime() {
      if (this.runningState == StopWatch.State.UNSTARTED) {
         throw new IllegalStateException("Stopwatch has not been started");
      } else {
         return this.startTimeMillis;
      }
   }

   public long getTime() {
      return this.getNanoTime() / 1000000L;
   }

   public long getTime(TimeUnit timeUnit) {
      return timeUnit.convert(this.getNanoTime(), TimeUnit.NANOSECONDS);
   }

   public boolean isStarted() {
      return this.runningState.isStarted();
   }

   public boolean isStopped() {
      return this.runningState.isStopped();
   }

   public boolean isSuspended() {
      return this.runningState.isSuspended();
   }

   public void reset() {
      this.runningState = StopWatch.State.UNSTARTED;
      this.splitState = StopWatch.SplitState.UNSPLIT;
   }

   public void resume() {
      if (this.runningState != StopWatch.State.SUSPENDED) {
         throw new IllegalStateException("Stopwatch must be suspended to resume. ");
      } else {
         this.startTime += System.nanoTime() - this.stopTime;
         this.runningState = StopWatch.State.RUNNING;
      }
   }

   public void split() {
      if (this.runningState != StopWatch.State.RUNNING) {
         throw new IllegalStateException("Stopwatch is not running. ");
      } else {
         this.stopTime = System.nanoTime();
         this.splitState = StopWatch.SplitState.SPLIT;
      }
   }

   public void start() {
      if (this.runningState == StopWatch.State.STOPPED) {
         throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
      } else if (this.runningState != StopWatch.State.UNSTARTED) {
         throw new IllegalStateException("Stopwatch already started. ");
      } else {
         this.startTime = System.nanoTime();
         this.startTimeMillis = System.currentTimeMillis();
         this.runningState = StopWatch.State.RUNNING;
      }
   }

   public void stop() {
      if (this.runningState != StopWatch.State.RUNNING && this.runningState != StopWatch.State.SUSPENDED) {
         throw new IllegalStateException("Stopwatch is not running. ");
      } else {
         if (this.runningState == StopWatch.State.RUNNING) {
            this.stopTime = System.nanoTime();
         }

         this.runningState = StopWatch.State.STOPPED;
      }
   }

   public void suspend() {
      if (this.runningState != StopWatch.State.RUNNING) {
         throw new IllegalStateException("Stopwatch must be running to suspend. ");
      } else {
         this.stopTime = System.nanoTime();
         this.runningState = StopWatch.State.SUSPENDED;
      }
   }

   public String toSplitString() {
      String msgStr = Objects.toString(this.message, "");
      String formattedTime = this.formatSplitTime();
      return msgStr.isEmpty() ? formattedTime : msgStr + " " + formattedTime;
   }

   public String toString() {
      String msgStr = Objects.toString(this.message, "");
      String formattedTime = this.formatTime();
      return msgStr.isEmpty() ? formattedTime : msgStr + " " + formattedTime;
   }

   public void unsplit() {
      if (this.splitState != StopWatch.SplitState.SPLIT) {
         throw new IllegalStateException("Stopwatch has not been split. ");
      } else {
         this.splitState = StopWatch.SplitState.UNSPLIT;
      }
   }

   private static enum State {
      RUNNING {
         boolean isStarted() {
            return true;
         }

         boolean isStopped() {
            return false;
         }

         boolean isSuspended() {
            return false;
         }
      },
      STOPPED {
         boolean isStarted() {
            return false;
         }

         boolean isStopped() {
            return true;
         }

         boolean isSuspended() {
            return false;
         }
      },
      SUSPENDED {
         boolean isStarted() {
            return true;
         }

         boolean isStopped() {
            return false;
         }

         boolean isSuspended() {
            return true;
         }
      },
      UNSTARTED {
         boolean isStarted() {
            return false;
         }

         boolean isStopped() {
            return true;
         }

         boolean isSuspended() {
            return false;
         }
      };

      private State() {
      }

      abstract boolean isStarted();

      abstract boolean isStopped();

      abstract boolean isSuspended();

      // $FF: synthetic method
      State(Object x2) {
         this();
      }
   }

   private static enum SplitState {
      SPLIT,
      UNSPLIT;
   }
}
