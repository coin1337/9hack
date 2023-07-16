package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.Serializable;

public class ViewMatcher implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final ViewMatcher EMPTY = new ViewMatcher();

   public boolean isVisibleForView(Class<?> activeView) {
      return false;
   }

   public static ViewMatcher construct(Class<?>[] views) {
      if (views == null) {
         return EMPTY;
      } else {
         switch(views.length) {
         case 0:
            return EMPTY;
         case 1:
            return new ViewMatcher.Single(views[0]);
         default:
            return new ViewMatcher.Multi(views);
         }
      }
   }

   private static final class Multi extends ViewMatcher implements Serializable {
      private static final long serialVersionUID = 1L;
      private final Class<?>[] _views;

      public Multi(Class<?>[] v) {
         this._views = v;
      }

      public boolean isVisibleForView(Class<?> activeView) {
         int i = 0;

         for(int len = this._views.length; i < len; ++i) {
            Class<?> view = this._views[i];
            if (activeView == view || view.isAssignableFrom(activeView)) {
               return true;
            }
         }

         return false;
      }
   }

   private static final class Single extends ViewMatcher {
      private static final long serialVersionUID = 1L;
      private final Class<?> _view;

      public Single(Class<?> v) {
         this._view = v;
      }

      public boolean isVisibleForView(Class<?> activeView) {
         return activeView == this._view || this._view.isAssignableFrom(activeView);
      }
   }
}
