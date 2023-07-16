package software.bernie.shadowed.fasterxml.jackson.databind.ext;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

public abstract class DOMDeserializer<T> extends FromStringDeserializer<T> {
   private static final long serialVersionUID = 1L;
   private static final DocumentBuilderFactory DEFAULT_PARSER_FACTORY;

   protected DOMDeserializer(Class<T> cls) {
      super(cls);
   }

   public abstract T _deserialize(String var1, DeserializationContext var2);

   protected final Document parse(String value) throws IllegalArgumentException {
      try {
         return this.documentBuilder().parse(new InputSource(new StringReader(value)));
      } catch (Exception var3) {
         throw new IllegalArgumentException("Failed to parse JSON String as XML: " + var3.getMessage(), var3);
      }
   }

   protected DocumentBuilder documentBuilder() throws ParserConfigurationException {
      return DEFAULT_PARSER_FACTORY.newDocumentBuilder();
   }

   static {
      DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
      parserFactory.setNamespaceAware(true);
      parserFactory.setExpandEntityReferences(false);

      try {
         parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      } catch (ParserConfigurationException var2) {
      } catch (Error var3) {
      }

      DEFAULT_PARSER_FACTORY = parserFactory;
   }

   public static class DocumentDeserializer extends DOMDeserializer<Document> {
      private static final long serialVersionUID = 1L;

      public DocumentDeserializer() {
         super(Document.class);
      }

      public Document _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
         return this.parse(value);
      }
   }

   public static class NodeDeserializer extends DOMDeserializer<Node> {
      private static final long serialVersionUID = 1L;

      public NodeDeserializer() {
         super(Node.class);
      }

      public Node _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
         return this.parse(value);
      }
   }
}
