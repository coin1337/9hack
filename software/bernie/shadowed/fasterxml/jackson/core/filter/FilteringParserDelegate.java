package software.bernie.shadowed.fasterxml.jackson.core.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.util.JsonParserDelegate;

public class FilteringParserDelegate extends JsonParserDelegate {
   protected TokenFilter rootFilter;
   protected boolean _allowMultipleMatches;
   protected boolean _includePath;
   /** @deprecated */
   @Deprecated
   protected boolean _includeImmediateParent;
   protected JsonToken _currToken;
   protected JsonToken _lastClearedToken;
   protected TokenFilterContext _headContext;
   protected TokenFilterContext _exposedContext;
   protected TokenFilter _itemFilter;
   protected int _matchCount;

   public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
      super(p);
      this.rootFilter = f;
      this._itemFilter = f;
      this._headContext = TokenFilterContext.createRootContext(f);
      this._includePath = includePath;
      this._allowMultipleMatches = allowMultipleMatches;
   }

   public TokenFilter getFilter() {
      return this.rootFilter;
   }

   public int getMatchCount() {
      return this._matchCount;
   }

   public JsonToken getCurrentToken() {
      return this._currToken;
   }

   public JsonToken currentToken() {
      return this._currToken;
   }

   public final int getCurrentTokenId() {
      JsonToken t = this._currToken;
      return t == null ? 0 : t.id();
   }

   public final int currentTokenId() {
      JsonToken t = this._currToken;
      return t == null ? 0 : t.id();
   }

   public boolean hasCurrentToken() {
      return this._currToken != null;
   }

   public boolean hasTokenId(int id) {
      JsonToken t = this._currToken;
      if (t == null) {
         return 0 == id;
      } else {
         return t.id() == id;
      }
   }

   public final boolean hasToken(JsonToken t) {
      return this._currToken == t;
   }

   public boolean isExpectedStartArrayToken() {
      return this._currToken == JsonToken.START_ARRAY;
   }

   public boolean isExpectedStartObjectToken() {
      return this._currToken == JsonToken.START_OBJECT;
   }

   public JsonLocation getCurrentLocation() {
      return this.delegate.getCurrentLocation();
   }

   public JsonStreamContext getParsingContext() {
      return this._filterContext();
   }

   public String getCurrentName() throws IOException {
      JsonStreamContext ctxt = this._filterContext();
      if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
         return ctxt.getCurrentName();
      } else {
         JsonStreamContext parent = ctxt.getParent();
         return parent == null ? null : parent.getCurrentName();
      }
   }

   public void clearCurrentToken() {
      if (this._currToken != null) {
         this._lastClearedToken = this._currToken;
         this._currToken = null;
      }

   }

   public JsonToken getLastClearedToken() {
      return this._lastClearedToken;
   }

   public void overrideCurrentName(String name) {
      throw new UnsupportedOperationException("Can not currently override name during filtering read");
   }

   public JsonToken nextToken() throws IOException {
      if (!this._allowMultipleMatches && this._currToken != null && this._exposedContext == null && this._currToken.isScalarValue() && !this._headContext.isStartHandled() && !this._includePath && this._itemFilter == TokenFilter.INCLUDE_ALL) {
         return this._currToken = null;
      } else {
         TokenFilterContext ctxt = this._exposedContext;
         JsonToken t;
         if (ctxt != null) {
            while(true) {
               t = ctxt.nextTokenToRead();
               if (t != null) {
                  this._currToken = t;
                  return t;
               }

               if (ctxt == this._headContext) {
                  this._exposedContext = null;
                  if (ctxt.inArray()) {
                     t = this.delegate.getCurrentToken();
                     this._currToken = t;
                     return t;
                  }
                  break;
               }

               ctxt = this._headContext.findChildOf(ctxt);
               this._exposedContext = ctxt;
               if (ctxt == null) {
                  throw this._constructError("Unexpected problem: chain of filtered context broken");
               }
            }
         }

         t = this.delegate.nextToken();
         if (t == null) {
            this._currToken = t;
            return t;
         } else {
            TokenFilter f;
            switch(t.id()) {
            case 1:
               f = this._itemFilter;
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildObjectContext(f, true);
                  return this._currToken = t;
               }

               if (f == null) {
                  this.delegate.skipChildren();
               } else {
                  f = this._headContext.checkValue(f);
                  if (f == null) {
                     this.delegate.skipChildren();
                  } else {
                     if (f != TokenFilter.INCLUDE_ALL) {
                        f = f.filterStartObject();
                     }

                     this._itemFilter = f;
                     if (f == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildObjectContext(f, true);
                        return this._currToken = t;
                     }

                     this._headContext = this._headContext.createChildObjectContext(f, false);
                     if (this._includePath) {
                        t = this._nextTokenWithBuffering(this._headContext);
                        if (t != null) {
                           this._currToken = t;
                           return t;
                        }
                     }
                  }
               }
               break;
            case 2:
            case 4:
               boolean returnEnd = this._headContext.isStartHandled();
               f = this._headContext.getFilter();
               if (f != null && f != TokenFilter.INCLUDE_ALL) {
                  f.filterFinishArray();
               }

               this._headContext = this._headContext.getParent();
               this._itemFilter = this._headContext.getFilter();
               if (returnEnd) {
                  return this._currToken = t;
               }
               break;
            case 3:
               f = this._itemFilter;
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildArrayContext(f, true);
                  return this._currToken = t;
               }

               if (f == null) {
                  this.delegate.skipChildren();
               } else {
                  f = this._headContext.checkValue(f);
                  if (f == null) {
                     this.delegate.skipChildren();
                  } else {
                     if (f != TokenFilter.INCLUDE_ALL) {
                        f = f.filterStartArray();
                     }

                     this._itemFilter = f;
                     if (f == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildArrayContext(f, true);
                        return this._currToken = t;
                     }

                     this._headContext = this._headContext.createChildArrayContext(f, false);
                     if (this._includePath) {
                        t = this._nextTokenWithBuffering(this._headContext);
                        if (t != null) {
                           this._currToken = t;
                           return t;
                        }
                     }
                  }
               }
               break;
            case 5:
               String name = this.delegate.getCurrentName();
               f = this._headContext.setFieldName(name);
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._itemFilter = f;
                  if (!this._includePath && this._includeImmediateParent && !this._headContext.isStartHandled()) {
                     t = this._headContext.nextTokenToRead();
                     this._exposedContext = this._headContext;
                  }

                  return this._currToken = t;
               }

               if (f == null) {
                  this.delegate.nextToken();
                  this.delegate.skipChildren();
               } else {
                  f = f.includeProperty(name);
                  if (f == null) {
                     this.delegate.nextToken();
                     this.delegate.skipChildren();
                  } else {
                     this._itemFilter = f;
                     if (f == TokenFilter.INCLUDE_ALL) {
                        if (this._verifyAllowedMatches()) {
                           if (this._includePath) {
                              return this._currToken = t;
                           }
                        } else {
                           this.delegate.nextToken();
                           this.delegate.skipChildren();
                        }
                     }

                     if (this._includePath) {
                        t = this._nextTokenWithBuffering(this._headContext);
                        if (t != null) {
                           this._currToken = t;
                           return t;
                        }
                     }
                  }
               }
               break;
            default:
               f = this._itemFilter;
               if (f == TokenFilter.INCLUDE_ALL) {
                  return this._currToken = t;
               }

               if (f != null) {
                  f = this._headContext.checkValue(f);
                  if ((f == TokenFilter.INCLUDE_ALL || f != null && f.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                     return this._currToken = t;
                  }
               }
            }

            return this._nextToken2();
         }
      }
   }

   protected final JsonToken _nextToken2() throws IOException {
      while(true) {
         JsonToken t = this.delegate.nextToken();
         if (t == null) {
            this._currToken = t;
            return t;
         }

         TokenFilter f;
         switch(t.id()) {
         case 1:
            f = this._itemFilter;
            if (f == TokenFilter.INCLUDE_ALL) {
               this._headContext = this._headContext.createChildObjectContext(f, true);
               return this._currToken = t;
            }

            if (f == null) {
               this.delegate.skipChildren();
            } else {
               f = this._headContext.checkValue(f);
               if (f == null) {
                  this.delegate.skipChildren();
               } else {
                  if (f != TokenFilter.INCLUDE_ALL) {
                     f = f.filterStartObject();
                  }

                  this._itemFilter = f;
                  if (f == TokenFilter.INCLUDE_ALL) {
                     this._headContext = this._headContext.createChildObjectContext(f, true);
                     return this._currToken = t;
                  }

                  this._headContext = this._headContext.createChildObjectContext(f, false);
                  if (!this._includePath) {
                     continue;
                  }

                  t = this._nextTokenWithBuffering(this._headContext);
                  if (t == null) {
                     continue;
                  }

                  this._currToken = t;
                  return t;
               }
            }
            break;
         case 2:
         case 4:
            boolean returnEnd = this._headContext.isStartHandled();
            f = this._headContext.getFilter();
            if (f != null && f != TokenFilter.INCLUDE_ALL) {
               f.filterFinishArray();
            }

            this._headContext = this._headContext.getParent();
            this._itemFilter = this._headContext.getFilter();
            if (!returnEnd) {
               break;
            }

            return this._currToken = t;
         case 3:
            f = this._itemFilter;
            if (f == TokenFilter.INCLUDE_ALL) {
               this._headContext = this._headContext.createChildArrayContext(f, true);
               return this._currToken = t;
            }

            if (f == null) {
               this.delegate.skipChildren();
            } else {
               f = this._headContext.checkValue(f);
               if (f == null) {
                  this.delegate.skipChildren();
               } else {
                  if (f != TokenFilter.INCLUDE_ALL) {
                     f = f.filterStartArray();
                  }

                  this._itemFilter = f;
                  if (f == TokenFilter.INCLUDE_ALL) {
                     this._headContext = this._headContext.createChildArrayContext(f, true);
                     return this._currToken = t;
                  }

                  this._headContext = this._headContext.createChildArrayContext(f, false);
                  if (!this._includePath) {
                     continue;
                  }

                  t = this._nextTokenWithBuffering(this._headContext);
                  if (t == null) {
                     continue;
                  }

                  this._currToken = t;
                  return t;
               }
            }
            break;
         case 5:
            String name = this.delegate.getCurrentName();
            f = this._headContext.setFieldName(name);
            if (f == TokenFilter.INCLUDE_ALL) {
               this._itemFilter = f;
               return this._currToken = t;
            }

            if (f == null) {
               this.delegate.nextToken();
               this.delegate.skipChildren();
            } else {
               f = f.includeProperty(name);
               if (f == null) {
                  this.delegate.nextToken();
                  this.delegate.skipChildren();
               } else {
                  this._itemFilter = f;
                  if (f == TokenFilter.INCLUDE_ALL) {
                     if (!this._verifyAllowedMatches() || !this._includePath) {
                        continue;
                     }

                     return this._currToken = t;
                  } else {
                     if (!this._includePath) {
                        continue;
                     }

                     t = this._nextTokenWithBuffering(this._headContext);
                     if (t == null) {
                        continue;
                     }

                     this._currToken = t;
                     return t;
                  }
               }
            }
            break;
         default:
            f = this._itemFilter;
            if (f == TokenFilter.INCLUDE_ALL) {
               return this._currToken = t;
            }

            if (f != null) {
               f = this._headContext.checkValue(f);
               if ((f == TokenFilter.INCLUDE_ALL || f != null && f.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                  return this._currToken = t;
               }
            }
         }
      }
   }

   protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException {
      while(true) {
         JsonToken t = this.delegate.nextToken();
         if (t == null) {
            return t;
         }

         TokenFilter f;
         switch(t.id()) {
         case 1:
            f = this._itemFilter;
            if (f == TokenFilter.INCLUDE_ALL) {
               this._headContext = this._headContext.createChildObjectContext(f, true);
               return t;
            }

            if (f == null) {
               this.delegate.skipChildren();
            } else {
               f = this._headContext.checkValue(f);
               if (f == null) {
                  this.delegate.skipChildren();
                  break;
               }

               if (f != TokenFilter.INCLUDE_ALL) {
                  f = f.filterStartObject();
               }

               this._itemFilter = f;
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildObjectContext(f, true);
                  return this._nextBuffered(buffRoot);
               }

               this._headContext = this._headContext.createChildObjectContext(f, false);
            }
            break;
         case 2:
         case 4:
            f = this._headContext.getFilter();
            if (f != null && f != TokenFilter.INCLUDE_ALL) {
               f.filterFinishArray();
            }

            boolean gotEnd = this._headContext == buffRoot;
            boolean returnEnd = gotEnd && this._headContext.isStartHandled();
            this._headContext = this._headContext.getParent();
            this._itemFilter = this._headContext.getFilter();
            if (returnEnd) {
               return t;
            }
            break;
         case 3:
            f = this._headContext.checkValue(this._itemFilter);
            if (f == null) {
               this.delegate.skipChildren();
               break;
            }

            if (f != TokenFilter.INCLUDE_ALL) {
               f = f.filterStartArray();
            }

            this._itemFilter = f;
            if (f == TokenFilter.INCLUDE_ALL) {
               this._headContext = this._headContext.createChildArrayContext(f, true);
               return this._nextBuffered(buffRoot);
            }

            this._headContext = this._headContext.createChildArrayContext(f, false);
            break;
         case 5:
            String name = this.delegate.getCurrentName();
            f = this._headContext.setFieldName(name);
            if (f == TokenFilter.INCLUDE_ALL) {
               this._itemFilter = f;
               return this._nextBuffered(buffRoot);
            }

            if (f == null) {
               this.delegate.nextToken();
               this.delegate.skipChildren();
            } else {
               f = f.includeProperty(name);
               if (f == null) {
                  this.delegate.nextToken();
                  this.delegate.skipChildren();
               } else {
                  this._itemFilter = f;
                  if (f != TokenFilter.INCLUDE_ALL) {
                     break;
                  }

                  if (this._verifyAllowedMatches()) {
                     return this._nextBuffered(buffRoot);
                  }

                  this._itemFilter = this._headContext.setFieldName(name);
               }
            }
            break;
         default:
            f = this._itemFilter;
            if (f == TokenFilter.INCLUDE_ALL) {
               return this._nextBuffered(buffRoot);
            }

            if (f != null) {
               f = this._headContext.checkValue(f);
               if ((f == TokenFilter.INCLUDE_ALL || f != null && f.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                  return this._nextBuffered(buffRoot);
               }
            }
         }
      }
   }

   private JsonToken _nextBuffered(TokenFilterContext buffRoot) throws IOException {
      this._exposedContext = buffRoot;
      TokenFilterContext ctxt = buffRoot;
      JsonToken t = buffRoot.nextTokenToRead();
      if (t != null) {
         return t;
      } else {
         while(ctxt != this._headContext) {
            ctxt = this._exposedContext.findChildOf(ctxt);
            this._exposedContext = ctxt;
            if (ctxt == null) {
               throw this._constructError("Unexpected problem: chain of filtered context broken");
            }

            t = this._exposedContext.nextTokenToRead();
            if (t != null) {
               return t;
            }
         }

         throw this._constructError("Internal error: failed to locate expected buffered tokens");
      }
   }

   private final boolean _verifyAllowedMatches() throws IOException {
      if (this._matchCount != 0 && !this._allowMultipleMatches) {
         return false;
      } else {
         ++this._matchCount;
         return true;
      }
   }

   public JsonToken nextValue() throws IOException {
      JsonToken t = this.nextToken();
      if (t == JsonToken.FIELD_NAME) {
         t = this.nextToken();
      }

      return t;
   }

   public JsonParser skipChildren() throws IOException {
      if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
         return this;
      } else {
         int open = 1;

         while(true) {
            JsonToken t = this.nextToken();
            if (t == null) {
               return this;
            }

            if (t.isStructStart()) {
               ++open;
            } else if (t.isStructEnd()) {
               --open;
               if (open == 0) {
                  return this;
               }
            }
         }
      }
   }

   public String getText() throws IOException {
      return this.delegate.getText();
   }

   public boolean hasTextCharacters() {
      return this.delegate.hasTextCharacters();
   }

   public char[] getTextCharacters() throws IOException {
      return this.delegate.getTextCharacters();
   }

   public int getTextLength() throws IOException {
      return this.delegate.getTextLength();
   }

   public int getTextOffset() throws IOException {
      return this.delegate.getTextOffset();
   }

   public BigInteger getBigIntegerValue() throws IOException {
      return this.delegate.getBigIntegerValue();
   }

   public boolean getBooleanValue() throws IOException {
      return this.delegate.getBooleanValue();
   }

   public byte getByteValue() throws IOException {
      return this.delegate.getByteValue();
   }

   public short getShortValue() throws IOException {
      return this.delegate.getShortValue();
   }

   public BigDecimal getDecimalValue() throws IOException {
      return this.delegate.getDecimalValue();
   }

   public double getDoubleValue() throws IOException {
      return this.delegate.getDoubleValue();
   }

   public float getFloatValue() throws IOException {
      return this.delegate.getFloatValue();
   }

   public int getIntValue() throws IOException {
      return this.delegate.getIntValue();
   }

   public long getLongValue() throws IOException {
      return this.delegate.getLongValue();
   }

   public JsonParser.NumberType getNumberType() throws IOException {
      return this.delegate.getNumberType();
   }

   public Number getNumberValue() throws IOException {
      return this.delegate.getNumberValue();
   }

   public int getValueAsInt() throws IOException {
      return this.delegate.getValueAsInt();
   }

   public int getValueAsInt(int defaultValue) throws IOException {
      return this.delegate.getValueAsInt(defaultValue);
   }

   public long getValueAsLong() throws IOException {
      return this.delegate.getValueAsLong();
   }

   public long getValueAsLong(long defaultValue) throws IOException {
      return this.delegate.getValueAsLong(defaultValue);
   }

   public double getValueAsDouble() throws IOException {
      return this.delegate.getValueAsDouble();
   }

   public double getValueAsDouble(double defaultValue) throws IOException {
      return this.delegate.getValueAsDouble(defaultValue);
   }

   public boolean getValueAsBoolean() throws IOException {
      return this.delegate.getValueAsBoolean();
   }

   public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
      return this.delegate.getValueAsBoolean(defaultValue);
   }

   public String getValueAsString() throws IOException {
      return this.delegate.getValueAsString();
   }

   public String getValueAsString(String defaultValue) throws IOException {
      return this.delegate.getValueAsString(defaultValue);
   }

   public Object getEmbeddedObject() throws IOException {
      return this.delegate.getEmbeddedObject();
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      return this.delegate.getBinaryValue(b64variant);
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      return this.delegate.readBinaryValue(b64variant, out);
   }

   public JsonLocation getTokenLocation() {
      return this.delegate.getTokenLocation();
   }

   protected JsonStreamContext _filterContext() {
      return this._exposedContext != null ? this._exposedContext : this._headContext;
   }
}
