package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

/** @deprecated */
@Deprecated
public class LookupTranslator extends CharSequenceTranslator {
   private final HashMap<String, String> lookupMap = new HashMap();
   private final HashSet<Character> prefixSet = new HashSet();
   private final int shortest;
   private final int longest;

   public LookupTranslator(CharSequence[]... lookup) {
      int _shortest = Integer.MAX_VALUE;
      int _longest = 0;
      if (lookup != null) {
         CharSequence[][] var4 = lookup;
         int var5 = lookup.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CharSequence[] seq = var4[var6];
            this.lookupMap.put(seq[0].toString(), seq[1].toString());
            this.prefixSet.add(seq[0].charAt(0));
            int sz = seq[0].length();
            if (sz < _shortest) {
               _shortest = sz;
            }

            if (sz > _longest) {
               _longest = sz;
            }
         }
      }

      this.shortest = _shortest;
      this.longest = _longest;
   }

   public int translate(CharSequence input, int index, Writer out) throws IOException {
      if (this.prefixSet.contains(input.charAt(index))) {
         int max = this.longest;
         if (index + this.longest > input.length()) {
            max = input.length() - index;
         }

         for(int i = max; i >= this.shortest; --i) {
            CharSequence subSeq = input.subSequence(index, index + i);
            String result = (String)this.lookupMap.get(subSeq.toString());
            if (result != null) {
               out.write(result);
               return i;
            }
         }
      }

      return 0;
   }
}
