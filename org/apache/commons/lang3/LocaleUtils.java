package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocaleUtils {
   private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry = new ConcurrentHashMap();
   private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage = new ConcurrentHashMap();

   public static Locale toLocale(String str) {
      if (str == null) {
         return null;
      } else if (str.isEmpty()) {
         return new Locale("", "");
      } else if (str.contains("#")) {
         throw new IllegalArgumentException("Invalid locale format: " + str);
      } else {
         int len = str.length();
         if (len < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
         } else {
            char ch0 = str.charAt(0);
            if (ch0 == '_') {
               if (len < 3) {
                  throw new IllegalArgumentException("Invalid locale format: " + str);
               } else {
                  char ch1 = str.charAt(1);
                  char ch2 = str.charAt(2);
                  if (Character.isUpperCase(ch1) && Character.isUpperCase(ch2)) {
                     if (len == 3) {
                        return new Locale("", str.substring(1, 3));
                     } else if (len < 5) {
                        throw new IllegalArgumentException("Invalid locale format: " + str);
                     } else if (str.charAt(3) != '_') {
                        throw new IllegalArgumentException("Invalid locale format: " + str);
                     } else {
                        return new Locale("", str.substring(1, 3), str.substring(4));
                     }
                  } else {
                     throw new IllegalArgumentException("Invalid locale format: " + str);
                  }
               }
            } else {
               return parseLocale(str);
            }
         }
      }
   }

   private static Locale parseLocale(String str) {
      if (isISO639LanguageCode(str)) {
         return new Locale(str);
      } else {
         String[] segments = str.split("_", -1);
         String language = segments[0];
         String country;
         if (segments.length != 2) {
            if (segments.length == 3) {
               country = segments[1];
               String variant = segments[2];
               if (isISO639LanguageCode(language) && (country.isEmpty() || isISO3166CountryCode(country) || isNumericAreaCode(country)) && !variant.isEmpty()) {
                  return new Locale(language, country, variant);
               }
            }
         } else {
            country = segments[1];
            if (isISO639LanguageCode(language) && isISO3166CountryCode(country) || isNumericAreaCode(country)) {
               return new Locale(language, country);
            }
         }

         throw new IllegalArgumentException("Invalid locale format: " + str);
      }
   }

   private static boolean isISO639LanguageCode(String str) {
      return StringUtils.isAllLowerCase(str) && (str.length() == 2 || str.length() == 3);
   }

   private static boolean isISO3166CountryCode(String str) {
      return StringUtils.isAllUpperCase(str) && str.length() == 2;
   }

   private static boolean isNumericAreaCode(String str) {
      return StringUtils.isNumeric(str) && str.length() == 3;
   }

   public static List<Locale> localeLookupList(Locale locale) {
      return localeLookupList(locale, locale);
   }

   public static List<Locale> localeLookupList(Locale locale, Locale defaultLocale) {
      List<Locale> list = new ArrayList(4);
      if (locale != null) {
         list.add(locale);
         if (!locale.getVariant().isEmpty()) {
            list.add(new Locale(locale.getLanguage(), locale.getCountry()));
         }

         if (!locale.getCountry().isEmpty()) {
            list.add(new Locale(locale.getLanguage(), ""));
         }

         if (!list.contains(defaultLocale)) {
            list.add(defaultLocale);
         }
      }

      return Collections.unmodifiableList(list);
   }

   public static List<Locale> availableLocaleList() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_LIST;
   }

   public static Set<Locale> availableLocaleSet() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_SET;
   }

   public static boolean isAvailableLocale(Locale locale) {
      return availableLocaleList().contains(locale);
   }

   public static List<Locale> languagesByCountry(String countryCode) {
      if (countryCode == null) {
         return Collections.emptyList();
      } else {
         List<Locale> langs = (List)cLanguagesByCountry.get(countryCode);
         if (langs == null) {
            List<Locale> langs = new ArrayList();
            List<Locale> locales = availableLocaleList();
            Iterator var3 = locales.iterator();

            while(var3.hasNext()) {
               Locale locale = (Locale)var3.next();
               if (countryCode.equals(locale.getCountry()) && locale.getVariant().isEmpty()) {
                  langs.add(locale);
               }
            }

            langs = Collections.unmodifiableList(langs);
            cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = (List)cLanguagesByCountry.get(countryCode);
         }

         return langs;
      }
   }

   public static List<Locale> countriesByLanguage(String languageCode) {
      if (languageCode == null) {
         return Collections.emptyList();
      } else {
         List<Locale> countries = (List)cCountriesByLanguage.get(languageCode);
         if (countries == null) {
            List<Locale> countries = new ArrayList();
            List<Locale> locales = availableLocaleList();
            Iterator var3 = locales.iterator();

            while(var3.hasNext()) {
               Locale locale = (Locale)var3.next();
               if (languageCode.equals(locale.getLanguage()) && !locale.getCountry().isEmpty() && locale.getVariant().isEmpty()) {
                  countries.add(locale);
               }
            }

            countries = Collections.unmodifiableList(countries);
            cCountriesByLanguage.putIfAbsent(languageCode, countries);
            countries = (List)cCountriesByLanguage.get(languageCode);
         }

         return countries;
      }
   }

   static class SyncAvoid {
      private static final List<Locale> AVAILABLE_LOCALE_LIST;
      private static final Set<Locale> AVAILABLE_LOCALE_SET;

      static {
         List<Locale> list = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));
         AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(list);
         AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet(list));
      }
   }
}
