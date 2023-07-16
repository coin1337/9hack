package software.bernie.shadowed.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ByteBufferFeeder extends NonBlockingInputFeeder {
   void feedInput(ByteBuffer var1) throws IOException;
}
