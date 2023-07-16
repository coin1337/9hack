package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.ArrayUtils;

/** @deprecated */
@Deprecated
public class AggregateTranslator extends CharSequenceTranslator {
   private final CharSequenceTranslator[] translators;

   public AggregateTranslator(CharSequenceTranslator... translators) {
      this.translators = (CharSequenceTranslator[])ArrayUtils.clone((Object[])translators);
   }

   public int translate(CharSequence input, int index, Writer out) throws IOException {
      CharSequenceTranslator[] var4 = this.translators;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         CharSequenceTranslator translator = var4[var6];
         int consumed = translator.translate(input, index, out);
         if (consumed != 0) {
            return consumed;
         }
      }

      return 0;
   }
}
