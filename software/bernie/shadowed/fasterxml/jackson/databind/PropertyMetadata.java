package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.annotation.Nulls;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;

public class PropertyMetadata implements Serializable {
   private static final long serialVersionUID = -1L;
   public static final PropertyMetadata STD_REQUIRED;
   public static final PropertyMetadata STD_OPTIONAL;
   public static final PropertyMetadata STD_REQUIRED_OR_OPTIONAL;
   protected final Boolean _required;
   protected final String _description;
   protected final Integer _index;
   protected final String _defaultValue;
   protected final transient PropertyMetadata.MergeInfo _mergeInfo;
   protected Nulls _valueNulls;
   protected Nulls _contentNulls;

   protected PropertyMetadata(Boolean req, String desc, Integer index, String def, PropertyMetadata.MergeInfo mergeInfo, Nulls valueNulls, Nulls contentNulls) {
      this._required = req;
      this._description = desc;
      this._index = index;
      this._defaultValue = def != null && !def.isEmpty() ? def : null;
      this._mergeInfo = mergeInfo;
      this._valueNulls = valueNulls;
      this._contentNulls = contentNulls;
   }

   public static PropertyMetadata construct(Boolean req, String desc, Integer index, String defaultValue) {
      if (desc == null && index == null && defaultValue == null) {
         if (req == null) {
            return STD_REQUIRED_OR_OPTIONAL;
         } else {
            return req ? STD_REQUIRED : STD_OPTIONAL;
         }
      } else {
         return new PropertyMetadata(req, desc, index, defaultValue, (PropertyMetadata.MergeInfo)null, (Nulls)null, (Nulls)null);
      }
   }

   /** @deprecated */
   @Deprecated
   public static PropertyMetadata construct(boolean req, String desc, Integer index, String defaultValue) {
      if (desc == null && index == null && defaultValue == null) {
         return req ? STD_REQUIRED : STD_OPTIONAL;
      } else {
         return new PropertyMetadata(req, desc, index, defaultValue, (PropertyMetadata.MergeInfo)null, (Nulls)null, (Nulls)null);
      }
   }

   protected Object readResolve() {
      if (this._description == null && this._index == null && this._defaultValue == null && this._mergeInfo == null && this._valueNulls == null && this._contentNulls == null) {
         if (this._required == null) {
            return STD_REQUIRED_OR_OPTIONAL;
         } else {
            return this._required ? STD_REQUIRED : STD_OPTIONAL;
         }
      } else {
         return this;
      }
   }

   public PropertyMetadata withDescription(String desc) {
      return new PropertyMetadata(this._required, desc, this._index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
   }

   public PropertyMetadata withMergeInfo(PropertyMetadata.MergeInfo mergeInfo) {
      return new PropertyMetadata(this._required, this._description, this._index, this._defaultValue, mergeInfo, this._valueNulls, this._contentNulls);
   }

   public PropertyMetadata withNulls(Nulls valueNulls, Nulls contentNulls) {
      return new PropertyMetadata(this._required, this._description, this._index, this._defaultValue, this._mergeInfo, valueNulls, contentNulls);
   }

   public PropertyMetadata withDefaultValue(String def) {
      if (def != null && !def.isEmpty()) {
         if (def.equals(this._defaultValue)) {
            return this;
         }
      } else {
         if (this._defaultValue == null) {
            return this;
         }

         def = null;
      }

      return new PropertyMetadata(this._required, this._description, this._index, def, this._mergeInfo, this._valueNulls, this._contentNulls);
   }

   public PropertyMetadata withIndex(Integer index) {
      return new PropertyMetadata(this._required, this._description, index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
   }

   public PropertyMetadata withRequired(Boolean b) {
      if (b == null) {
         if (this._required == null) {
            return this;
         }
      } else if (b.equals(this._required)) {
         return this;
      }

      return new PropertyMetadata(b, this._description, this._index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
   }

   public String getDescription() {
      return this._description;
   }

   public String getDefaultValue() {
      return this._defaultValue;
   }

   public boolean hasDefaultValue() {
      return this._defaultValue != null;
   }

   public boolean isRequired() {
      return this._required != null && this._required;
   }

   public Boolean getRequired() {
      return this._required;
   }

   public Integer getIndex() {
      return this._index;
   }

   public boolean hasIndex() {
      return this._index != null;
   }

   public PropertyMetadata.MergeInfo getMergeInfo() {
      return this._mergeInfo;
   }

   public Nulls getValueNulls() {
      return this._valueNulls;
   }

   public Nulls getContentNulls() {
      return this._contentNulls;
   }

   static {
      STD_REQUIRED = new PropertyMetadata(Boolean.TRUE, (String)null, (Integer)null, (String)null, (PropertyMetadata.MergeInfo)null, (Nulls)null, (Nulls)null);
      STD_OPTIONAL = new PropertyMetadata(Boolean.FALSE, (String)null, (Integer)null, (String)null, (PropertyMetadata.MergeInfo)null, (Nulls)null, (Nulls)null);
      STD_REQUIRED_OR_OPTIONAL = new PropertyMetadata((Boolean)null, (String)null, (Integer)null, (String)null, (PropertyMetadata.MergeInfo)null, (Nulls)null, (Nulls)null);
   }

   public static final class MergeInfo {
      public final AnnotatedMember getter;
      public final boolean fromDefaults;

      protected MergeInfo(AnnotatedMember getter, boolean fromDefaults) {
         this.getter = getter;
         this.fromDefaults = fromDefaults;
      }

      public static PropertyMetadata.MergeInfo createForDefaults(AnnotatedMember getter) {
         return new PropertyMetadata.MergeInfo(getter, true);
      }

      public static PropertyMetadata.MergeInfo createForTypeOverride(AnnotatedMember getter) {
         return new PropertyMetadata.MergeInfo(getter, false);
      }

      public static PropertyMetadata.MergeInfo createForPropertyOverride(AnnotatedMember getter) {
         return new PropertyMetadata.MergeInfo(getter, false);
      }
   }
}
