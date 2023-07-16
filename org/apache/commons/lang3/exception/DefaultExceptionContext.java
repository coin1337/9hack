package org.apache.commons.lang3.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultExceptionContext implements ExceptionContext, Serializable {
   private static final long serialVersionUID = 20110706L;
   private final List<Pair<String, Object>> contextValues = new ArrayList();

   public DefaultExceptionContext addContextValue(String label, Object value) {
      this.contextValues.add(new ImmutablePair(label, value));
      return this;
   }

   public DefaultExceptionContext setContextValue(String label, Object value) {
      this.contextValues.removeIf((p) -> {
         return StringUtils.equals(label, (CharSequence)p.getKey());
      });
      this.addContextValue(label, value);
      return this;
   }

   public List<Object> getContextValues(String label) {
      List<Object> values = new ArrayList();
      Iterator var3 = this.contextValues.iterator();

      while(var3.hasNext()) {
         Pair<String, Object> pair = (Pair)var3.next();
         if (StringUtils.equals(label, (CharSequence)pair.getKey())) {
            values.add(pair.getValue());
         }
      }

      return values;
   }

   public Object getFirstContextValue(String label) {
      Iterator var2 = this.contextValues.iterator();

      Pair pair;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         pair = (Pair)var2.next();
      } while(!StringUtils.equals(label, (CharSequence)pair.getKey()));

      return pair.getValue();
   }

   public Set<String> getContextLabels() {
      Set<String> labels = new HashSet();
      Iterator var2 = this.contextValues.iterator();

      while(var2.hasNext()) {
         Pair<String, Object> pair = (Pair)var2.next();
         labels.add(pair.getKey());
      }

      return labels;
   }

   public List<Pair<String, Object>> getContextEntries() {
      return this.contextValues;
   }

   public String getFormattedExceptionMessage(String baseMessage) {
      StringBuilder buffer = new StringBuilder(256);
      if (baseMessage != null) {
         buffer.append(baseMessage);
      }

      if (!this.contextValues.isEmpty()) {
         if (buffer.length() > 0) {
            buffer.append('\n');
         }

         buffer.append("Exception Context:\n");
         int i = 0;

         for(Iterator var4 = this.contextValues.iterator(); var4.hasNext(); buffer.append("]\n")) {
            Pair<String, Object> pair = (Pair)var4.next();
            buffer.append("\t[");
            ++i;
            buffer.append(i);
            buffer.append(':');
            buffer.append((String)pair.getKey());
            buffer.append("=");
            Object value = pair.getValue();
            if (value == null) {
               buffer.append("null");
            } else {
               String valueStr;
               try {
                  valueStr = value.toString();
               } catch (Exception var9) {
                  valueStr = "Exception thrown on toString(): " + ExceptionUtils.getStackTrace(var9);
               }

               buffer.append(valueStr);
            }
         }

         buffer.append("---------------------------------");
      }

      return buffer.toString();
   }
}
