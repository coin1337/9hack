package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RawValue;

public interface JsonNodeCreator {
   ValueNode booleanNode(boolean var1);

   ValueNode nullNode();

   ValueNode numberNode(byte var1);

   ValueNode numberNode(Byte var1);

   ValueNode numberNode(short var1);

   ValueNode numberNode(Short var1);

   ValueNode numberNode(int var1);

   ValueNode numberNode(Integer var1);

   ValueNode numberNode(long var1);

   ValueNode numberNode(Long var1);

   ValueNode numberNode(BigInteger var1);

   ValueNode numberNode(float var1);

   ValueNode numberNode(Float var1);

   ValueNode numberNode(double var1);

   ValueNode numberNode(Double var1);

   ValueNode numberNode(BigDecimal var1);

   ValueNode textNode(String var1);

   ValueNode binaryNode(byte[] var1);

   ValueNode binaryNode(byte[] var1, int var2, int var3);

   ValueNode pojoNode(Object var1);

   ValueNode rawValueNode(RawValue var1);

   ArrayNode arrayNode();

   ArrayNode arrayNode(int var1);

   ObjectNode objectNode();
}
