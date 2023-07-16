package org.apache.commons.lang3.arch;

public class Processor {
   private final Processor.Arch arch;
   private final Processor.Type type;

   public Processor(Processor.Arch arch, Processor.Type type) {
      this.arch = arch;
      this.type = type;
   }

   public Processor.Arch getArch() {
      return this.arch;
   }

   public Processor.Type getType() {
      return this.type;
   }

   public boolean is32Bit() {
      return Processor.Arch.BIT_32.equals(this.arch);
   }

   public boolean is64Bit() {
      return Processor.Arch.BIT_64.equals(this.arch);
   }

   public boolean isX86() {
      return Processor.Type.X86.equals(this.type);
   }

   public boolean isIA64() {
      return Processor.Type.IA_64.equals(this.type);
   }

   public boolean isPPC() {
      return Processor.Type.PPC.equals(this.type);
   }

   public static enum Type {
      X86,
      IA_64,
      PPC,
      UNKNOWN;
   }

   public static enum Arch {
      BIT_32("32-bit"),
      BIT_64("64-bit"),
      UNKNOWN("Unknown");

      private final String label;

      private Arch(String label) {
         this.label = label;
      }

      public String getLabel() {
         return this.label;
      }
   }
}
