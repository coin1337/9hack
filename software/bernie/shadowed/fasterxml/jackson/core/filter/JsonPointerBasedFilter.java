package software.bernie.shadowed.fasterxml.jackson.core.filter;

import software.bernie.shadowed.fasterxml.jackson.core.JsonPointer;

public class JsonPointerBasedFilter extends TokenFilter {
   protected final JsonPointer _pathToMatch;

   public JsonPointerBasedFilter(String ptrExpr) {
      this(JsonPointer.compile(ptrExpr));
   }

   public JsonPointerBasedFilter(JsonPointer match) {
      this._pathToMatch = match;
   }

   public TokenFilter includeElement(int index) {
      JsonPointer next = this._pathToMatch.matchElement(index);
      if (next == null) {
         return null;
      } else {
         return (TokenFilter)(next.matches() ? TokenFilter.INCLUDE_ALL : new JsonPointerBasedFilter(next));
      }
   }

   public TokenFilter includeProperty(String name) {
      JsonPointer next = this._pathToMatch.matchProperty(name);
      if (next == null) {
         return null;
      } else {
         return (TokenFilter)(next.matches() ? TokenFilter.INCLUDE_ALL : new JsonPointerBasedFilter(next));
      }
   }

   public TokenFilter filterStartArray() {
      return this;
   }

   public TokenFilter filterStartObject() {
      return this;
   }

   protected boolean _includeScalar() {
      return this._pathToMatch.matches();
   }

   public String toString() {
      return "[JsonPointerFilter at: " + this._pathToMatch + "]";
   }
}
