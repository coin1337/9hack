package software.bernie.geckolib3.geo.render.built;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class GeoQuad {
   public GeoVertex[] vertices;
   public final Vec3i normal;
   public EnumFacing direction;

   public GeoQuad(GeoVertex[] verticesIn, float u1, float v1, float uSize, float vSize, float texWidth, float texHeight, Boolean mirrorIn, EnumFacing directionIn) {
      this.direction = directionIn;
      this.vertices = verticesIn;
      float u2 = u1 + uSize;
      float v2 = v1 + vSize;
      u1 /= texWidth;
      u2 /= texWidth;
      v1 /= texHeight;
      v2 /= texHeight;
      if (mirrorIn != null && mirrorIn) {
         this.vertices[0] = verticesIn[0].setTextureUV(u1, v1);
         this.vertices[1] = verticesIn[1].setTextureUV(u2, v1);
         this.vertices[2] = verticesIn[2].setTextureUV(u2, v2);
         this.vertices[3] = verticesIn[3].setTextureUV(u1, v2);
      } else {
         this.vertices[0] = verticesIn[0].setTextureUV(u2, v1);
         this.vertices[1] = verticesIn[1].setTextureUV(u1, v1);
         this.vertices[2] = verticesIn[2].setTextureUV(u1, v2);
         this.vertices[3] = verticesIn[3].setTextureUV(u2, v2);
      }

      this.normal = directionIn.func_176730_m();
   }

   public GeoQuad(GeoVertex[] verticesIn, double[] uvCoords, double[] uvSize, float texWidth, float texHeight, Boolean mirrorIn, EnumFacing directionIn) {
      this(verticesIn, (float)uvCoords[0], (float)uvCoords[1], (float)uvSize[0], (float)uvSize[1], texWidth, texHeight, mirrorIn, directionIn);
   }
}
