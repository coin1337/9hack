package software.bernie.shadowed.fasterxml.jackson.core;

import java.io.IOException;

public abstract class TreeCodec {
   public abstract <T extends TreeNode> T readTree(JsonParser var1) throws IOException, JsonProcessingException;

   public abstract void writeTree(JsonGenerator var1, TreeNode var2) throws IOException, JsonProcessingException;

   public abstract TreeNode createArrayNode();

   public abstract TreeNode createObjectNode();

   public abstract JsonParser treeAsTokens(TreeNode var1);
}
