package software.bernie.shadowed.fasterxml.jackson.core.async;

public interface NonBlockingInputFeeder {
   boolean needMoreInput();

   void endOfInput();
}
