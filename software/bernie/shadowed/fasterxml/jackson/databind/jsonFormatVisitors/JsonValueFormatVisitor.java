package software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.Set;

public interface JsonValueFormatVisitor {
   void format(JsonValueFormat var1);

   void enumTypes(Set<String> var1);

   public static class Base implements JsonValueFormatVisitor {
      public void format(JsonValueFormat format) {
      }

      public void enumTypes(Set<String> enums) {
      }
   }
}
