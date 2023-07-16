package software.bernie.geckolib3.util;

import net.minecraft.client.model.ModelRenderer;
import software.bernie.geckolib3.core.processor.IBone;

public class GeoUtils {
   public static void copyRotations(ModelRenderer from, IBone to) {
      to.setRotationX(-from.field_78795_f);
      to.setRotationY(-from.field_78796_g);
      to.setRotationZ(from.field_78808_h);
   }
}
