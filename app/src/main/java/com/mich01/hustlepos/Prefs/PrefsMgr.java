package com.mich01.hustlepos.Prefs;

import static com.mich01.hustlepos.Prefs.PublicStrings.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PrefsMgr
{
    public static SharedPreferences MyPrefs;
    public static SharedPreferences.Editor MyPrefsEditor;
    Context _context;
    public PrefsMgr(Context context)
    {
        this._context = context;
        MyPrefs = getPrefs(context);
        MyPrefsEditor = MyPrefs.edit();
        MyPrefsEditor.putInt("DBExists", 0);
        MyPrefs.getLong("InstalledTimestamp", 0);
        MyPrefs.getString("AppPassword",null);
        MyPrefs.getInt("SetupComplete",0);
        MyPrefs.getString("PhoneNumber",null);
        MyPrefsEditor.apply();
        MyPrefsEditor.commit();
    }
    public static EncryptedSharedPreferences getPrefs(Context context)
    {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        }catch (GeneralSecurityException | IOException | NullPointerException ignored) {}
        return null;
    }
}
