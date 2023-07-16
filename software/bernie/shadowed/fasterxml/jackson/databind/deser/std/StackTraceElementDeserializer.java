package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;

public class StackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement> {
   private static final long serialVersionUID = 1L;

   public StackTraceElementDeserializer() {
      super(StackTraceElement.class);
   }

   public StackTraceElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.START_OBJECT) {
         String className = "";
         String methodName = "";
         String fileName = "";
         String moduleName = null;
         String moduleVersion = null;
         String classLoaderName = null;
         int lineNumber = -1;

         while((t = p.nextValue()) != JsonToken.END_OBJECT) {
            String propName = p.getCurrentName();
            if ("className".equals(propName)) {
               className = p.getText();
            } else if ("classLoaderName".equals(propName)) {
               classLoaderName = p.getText();
            } else if ("fileName".equals(propName)) {
               fileName = p.getText();
            } else if ("lineNumber".equals(propName)) {
               if (t.isNumeric()) {
                  lineNumber = p.getIntValue();
               } else {
                  lineNumber = this._parseIntPrimitive(p, ctxt);
               }
            } else if ("methodName".equals(propName)) {
               methodName = p.getText();
            } else if (!"nativeMethod".equals(propName)) {
               if ("moduleName".equals(propName)) {
                  moduleName = p.getText();
               } else if ("moduleVersion".equals(propName)) {
                  moduleVersion = p.getText();
               } else {
                  this.handleUnknownProperty(p, ctxt, this._valueClass, propName);
               }
            }
         }

         return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, classLoaderName);
      } else if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
         p.nextToken();
         StackTraceElement value = this.deserialize(p, ctxt);
         if (p.nextToken() != JsonToken.END_ARRAY) {
            this.handleMissingEndArrayForSingle(p, ctxt);
         }

         return value;
      } else {
         return (StackTraceElement)ctxt.handleUnexpectedToken(this._valueClass, p);
      }
   }

   /** @deprecated */
   @Deprecated
   protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion) {
      return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, (String)null);
   }

   protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion, String classLoaderName) {
      return new StackTraceElement(className, methodName, fileName, lineNumber);
   }
}
