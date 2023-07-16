package software.bernie.shadowed.fasterxml.jackson.databind.cfg;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonAutoDetect;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;

public abstract class ConfigOverride {
   protected JsonFormat.Value _format;
   protected JsonInclude.Value _include;
   protected JsonInclude.Value _includeAsProperty;
   protected JsonIgnoreProperties.Value _ignorals;
   protected JsonSetter.Value _setterInfo;
   protected JsonAutoDetect.Value _visibility;
   protected Boolean _isIgnoredType;
   protected Boolean _mergeable;

   protected ConfigOverride() {
   }

   protected ConfigOverride(ConfigOverride src) {
      this._format = src._format;
      this._include = src._include;
      this._includeAsProperty = src._includeAsProperty;
      this._ignorals = src._ignorals;
      this._isIgnoredType = src._isIgnoredType;
      this._mergeable = src._mergeable;
   }

   public static ConfigOverride empty() {
      return ConfigOverride.Empty.INSTANCE;
   }

   public JsonFormat.Value getFormat() {
      return this._format;
   }

   public JsonInclude.Value getInclude() {
      return this._include;
   }

   public JsonInclude.Value getIncludeAsProperty() {
      return this._includeAsProperty;
   }

   public JsonIgnoreProperties.Value getIgnorals() {
      return this._ignorals;
   }

   public Boolean getIsIgnoredType() {
      return this._isIgnoredType;
   }

   public JsonSetter.Value getSetterInfo() {
      return this._setterInfo;
   }

   public JsonAutoDetect.Value getVisibility() {
      return this._visibility;
   }

   public Boolean getMergeable() {
      return this._mergeable;
   }

   static final class Empty extends ConfigOverride {
      static final ConfigOverride.Empty INSTANCE = new ConfigOverride.Empty();

      private Empty() {
      }
   }
}
