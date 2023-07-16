package software.bernie.shadowed.fasterxml.jackson.databind.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.VisibilityChecker;

public class ConfigOverrides implements Serializable {
   private static final long serialVersionUID = 1L;
   protected Map<Class<?>, MutableConfigOverride> _overrides;
   protected JsonInclude.Value _defaultInclusion;
   protected JsonSetter.Value _defaultSetterInfo;
   protected VisibilityChecker<?> _visibilityChecker;
   protected Boolean _defaultMergeable;

   public ConfigOverrides() {
      this((Map)null, JsonInclude.Value.empty(), JsonSetter.Value.empty(), VisibilityChecker.Std.defaultInstance(), (Boolean)null);
   }

   protected ConfigOverrides(Map<Class<?>, MutableConfigOverride> overrides, JsonInclude.Value defIncl, JsonSetter.Value defSetter, VisibilityChecker<?> defVisibility, Boolean defMergeable) {
      this._overrides = overrides;
      this._defaultInclusion = defIncl;
      this._defaultSetterInfo = defSetter;
      this._visibilityChecker = defVisibility;
      this._defaultMergeable = defMergeable;
   }

   public ConfigOverrides copy() {
      Map newOverrides;
      if (this._overrides == null) {
         newOverrides = null;
      } else {
         newOverrides = this._newMap();
         Iterator i$ = this._overrides.entrySet().iterator();

         while(i$.hasNext()) {
            Entry<Class<?>, MutableConfigOverride> entry = (Entry)i$.next();
            newOverrides.put(entry.getKey(), ((MutableConfigOverride)entry.getValue()).copy());
         }
      }

      return new ConfigOverrides(newOverrides, this._defaultInclusion, this._defaultSetterInfo, this._visibilityChecker, this._defaultMergeable);
   }

   public ConfigOverride findOverride(Class<?> type) {
      return this._overrides == null ? null : (ConfigOverride)this._overrides.get(type);
   }

   public MutableConfigOverride findOrCreateOverride(Class<?> type) {
      if (this._overrides == null) {
         this._overrides = this._newMap();
      }

      MutableConfigOverride override = (MutableConfigOverride)this._overrides.get(type);
      if (override == null) {
         override = new MutableConfigOverride();
         this._overrides.put(type, override);
      }

      return override;
   }

   public JsonInclude.Value getDefaultInclusion() {
      return this._defaultInclusion;
   }

   public JsonSetter.Value getDefaultSetterInfo() {
      return this._defaultSetterInfo;
   }

   public Boolean getDefaultMergeable() {
      return this._defaultMergeable;
   }

   public VisibilityChecker<?> getDefaultVisibility() {
      return this._visibilityChecker;
   }

   public void setDefaultInclusion(JsonInclude.Value v) {
      this._defaultInclusion = v;
   }

   public void setDefaultSetterInfo(JsonSetter.Value v) {
      this._defaultSetterInfo = v;
   }

   public void setDefaultMergeable(Boolean v) {
      this._defaultMergeable = v;
   }

   public void setDefaultVisibility(VisibilityChecker<?> v) {
      this._visibilityChecker = v;
   }

   protected Map<Class<?>, MutableConfigOverride> _newMap() {
      return new HashMap();
   }
}
