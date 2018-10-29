package il.co.alias;

import android.content.Context;
import android.content.SharedPreferences;
import static il.co.alias.ConstantsHolder.*;

import java.util.Locale;

public class LocaleManager {
    public static void changeLanguage(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void loadLocale(Context context, SharedPreferences prefs) {
        String language = prefs.getString(APP_LANGUAGE, "en");
        changeLanguage(context, language);
    }

    public static void saveLocale(SharedPreferences prefs, String lang) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_STRING, lang);
        editor.commit();
    }

    public static String getCurrentLocale()
    {
        return Locale.getDefault().getLanguage();
    }
}
