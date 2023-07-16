package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

/** @deprecated */
@Deprecated
public class ExtendedMessageFormat extends MessageFormat {
   private static final long serialVersionUID = -2362048321261811743L;
   private static final int HASH_SEED = 31;
   private static final String DUMMY_PATTERN = "";
   private static final char START_FMT = ',';
   private static final char END_FE = '}';
   private static final char START_FE = '{';
   private static final char QUOTE = '\'';
   private String toPattern;
   private final Map<String, ? extends FormatFactory> registry;

   public ExtendedMessageFormat(String pattern) {
      this(pattern, Locale.getDefault());
   }

   public ExtendedMessageFormat(String pattern, Locale locale) {
      this(pattern, locale, (Map)null);
   }

   public ExtendedMessageFormat(String pattern, Map<String, ? extends FormatFactory> registry) {
      this(pattern, Locale.getDefault(), registry);
   }

   public ExtendedMessageFormat(String pattern, Locale locale, Map<String, ? extends FormatFactory> registry) {
      super("");
      this.setLocale(locale);
      this.registry = registry;
      this.applyPattern(pattern);
   }

   public String toPattern() {
      return this.toPattern;
   }

   public final void applyPattern(String pattern) {
      if (this.registry == null) {
         super.applyPattern(pattern);
         this.toPattern = super.toPattern();
      } else {
         ArrayList<Format> foundFormats = new ArrayList();
         ArrayList<String> foundDescriptions = new ArrayList();
         StringBuilder stripCustom = new StringBuilder(pattern.length());
         ParsePosition pos = new ParsePosition(0);
         char[] c = pattern.toCharArray();
         int fmtCount = 0;

         int i;
         while(pos.getIndex() < pattern.length()) {
            switch(c[pos.getIndex()]) {
            case '\'':
               this.appendQuotedString(pattern, pos, stripCustom);
               break;
            case '{':
               ++fmtCount;
               this.seekNonWs(pattern, pos);
               int start = pos.getIndex();
               i = this.readArgumentIndex(pattern, this.next(pos));
               stripCustom.append('{').append(i);
               this.seekNonWs(pattern, pos);
               Format format = null;
               String formatDescription = null;
               if (c[pos.getIndex()] == ',') {
                  formatDescription = this.parseFormatDescription(pattern, this.next(pos));
                  format = this.getFormat(formatDescription);
                  if (format == null) {
                     stripCustom.append(',').append(formatDescription);
                  }
               }

               foundFormats.add(format);
               foundDescriptions.add(format == null ? null : formatDescription);
               Validate.isTrue(foundFormats.size() == fmtCount);
               Validate.isTrue(foundDescriptions.size() == fmtCount);
               if (c[pos.getIndex()] != '}') {
                  throw new IllegalArgumentException("Unreadable format element at position " + start);
               }
            default:
               stripCustom.append(c[pos.getIndex()]);
               this.next(pos);
            }
         }

         super.applyPattern(stripCustom.toString());
         this.toPattern = this.insertFormats(super.toPattern(), foundDescriptions);
         if (this.containsElements(foundFormats)) {
            Format[] origFormats = this.getFormats();
            i = 0;

            for(Iterator it = foundFormats.iterator(); it.hasNext(); ++i) {
               Format f = (Format)it.next();
               if (f != null) {
                  origFormats[i] = f;
               }
            }

            super.setFormats(origFormats);
         }

      }
   }

   public void setFormat(int formatElementIndex, Format newFormat) {
      throw new UnsupportedOperationException();
   }

   public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
      throw new UnsupportedOperationException();
   }

   public void setFormats(Format[] newFormats) {
      throw new UnsupportedOperationException();
   }

   public void setFormatsByArgumentIndex(Format[] newFormats) {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!super.equals(obj)) {
         return false;
      } else if (ObjectUtils.notEqual(this.getClass(), obj.getClass())) {
         return false;
      } else {
         ExtendedMessageFormat rhs = (ExtendedMessageFormat)obj;
         if (ObjectUtils.notEqual(this.toPattern, rhs.toPattern)) {
            return false;
         } else {
            return !ObjectUtils.notEqual(this.registry, rhs.registry);
         }
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + Objects.hashCode(this.registry);
      result = 31 * result + Objects.hashCode(this.toPattern);
      return result;
   }

   private Format getFormat(String desc) {
      if (this.registry != null) {
         String name = desc;
         String args = null;
         int i = desc.indexOf(44);
         if (i > 0) {
            name = desc.substring(0, i).trim();
            args = desc.substring(i + 1).trim();
         }

         FormatFactory factory = (FormatFactory)this.registry.get(name);
         if (factory != null) {
            return factory.getFormat(name, args, this.getLocale());
         }
      }

      return null;
   }

   private int readArgumentIndex(String pattern, ParsePosition pos) {
      int start = pos.getIndex();
      this.seekNonWs(pattern, pos);
      StringBuilder result = new StringBuilder();

      boolean error;
      for(error = false; !error && pos.getIndex() < pattern.length(); this.next(pos)) {
         char c = pattern.charAt(pos.getIndex());
         if (Character.isWhitespace(c)) {
            this.seekNonWs(pattern, pos);
            c = pattern.charAt(pos.getIndex());
            if (c != ',' && c != '}') {
               error = true;
               continue;
            }
         }

         if ((c == ',' || c == '}') && result.length() > 0) {
            try {
               return Integer.parseInt(result.toString());
            } catch (NumberFormatException var8) {
            }
         }

         error = !Character.isDigit(c);
         result.append(c);
      }

      if (error) {
         throw new IllegalArgumentException("Invalid format argument index at position " + start + ": " + pattern.substring(start, pos.getIndex()));
      } else {
         throw new IllegalArgumentException("Unterminated format element at position " + start);
      }
   }

   private String parseFormatDescription(String pattern, ParsePosition pos) {
      int start = pos.getIndex();
      this.seekNonWs(pattern, pos);
      int text = pos.getIndex();

      for(int depth = 1; pos.getIndex() < pattern.length(); this.next(pos)) {
         switch(pattern.charAt(pos.getIndex())) {
         case '\'':
            this.getQuotedString(pattern, pos);
            break;
         case '{':
            ++depth;
            break;
         case '}':
            --depth;
            if (depth == 0) {
               return pattern.substring(text, pos.getIndex());
            }
         }
      }

      throw new IllegalArgumentException("Unterminated format element at position " + start);
   }

   private String insertFormats(String pattern, ArrayList<String> customPatterns) {
      if (!this.containsElements(customPatterns)) {
         return pattern;
      } else {
         StringBuilder sb = new StringBuilder(pattern.length() * 2);
         ParsePosition pos = new ParsePosition(0);
         int fe = -1;
         int depth = 0;

         while(pos.getIndex() < pattern.length()) {
            char c = pattern.charAt(pos.getIndex());
            switch(c) {
            case '\'':
               this.appendQuotedString(pattern, pos, sb);
               break;
            case '{':
               ++depth;
               sb.append('{').append(this.readArgumentIndex(pattern, this.next(pos)));
               if (depth == 1) {
                  ++fe;
                  String customPattern = (String)customPatterns.get(fe);
                  if (customPattern != null) {
                     sb.append(',').append(customPattern);
                  }
               }
               break;
            case '}':
               --depth;
            default:
               sb.append(c);
               this.next(pos);
            }
         }

         return sb.toString();
      }
   }

   private void seekNonWs(String pattern, ParsePosition pos) {
      int len = false;
      char[] buffer = pattern.toCharArray();

      int len;
      do {
         len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
         pos.setIndex(pos.getIndex() + len);
      } while(len > 0 && pos.getIndex() < pattern.length());

   }

   private ParsePosition next(ParsePosition pos) {
      pos.setIndex(pos.getIndex() + 1);
      return pos;
   }

   private StringBuilder appendQuotedString(String pattern, ParsePosition pos, StringBuilder appendTo) {
      assert pattern.toCharArray()[pos.getIndex()] == '\'' : "Quoted string must start with quote character";

      if (appendTo != null) {
         appendTo.append('\'');
      }

      this.next(pos);
      int start = pos.getIndex();
      char[] c = pattern.toCharArray();

      for(int i = pos.getIndex(); i < pattern.length(); ++i) {
         if (c[pos.getIndex()] == '\'') {
            this.next(pos);
            return appendTo == null ? null : appendTo.append(c, start, pos.getIndex() - start);
         }

         this.next(pos);
      }

      throw new IllegalArgumentException("Unterminated quoted string at position " + start);
   }

   private void getQuotedString(String pattern, ParsePosition pos) {
      this.appendQuotedString(pattern, pos, (StringBuilder)null);
   }

   private boolean containsElements(Collection<?> coll) {
      if (coll != null && !coll.isEmpty()) {
         Iterator var2 = coll.iterator();

         Object name;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            name = var2.next();
         } while(name == null);

         return true;
      } else {
         return false;
      }
   }
}
