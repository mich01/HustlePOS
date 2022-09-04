package com.mich01.hustlepos.Modules;

import static com.mich01.hustlepos.Prefs.PublicStrings.su;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.mich01.hustlepos.DB.DBManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

public class BackendFunctions {
    Context context;
    public static final int REQUEST_CODE_PERMISSIONS = 2;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public BackendFunctions(Context context) {
        this.context = context;
    }

    //check permissions.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_PERMISSIONS
            );
        }
    }

    public String GenerateTransactionID()
    {
        int n = 6;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);
        SecureRandom random = new SecureRandom(); // Compliant for security-sensitive use cases
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = AlphaNumericString.length() * random.nextInt();

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void LocalBackup(final DBManager db, final String outFileName, Context context) {
        File folder = new File(context.getFilesDir() + File.separator + "MyBiz");
        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {
            String out = folder.getAbsolutePath()+File.separator+"HustlePOS-"+ new Date().getTime() + ".db";
            try {
                db.backup(out);
            } catch (IOException | NullPointerException ignored) {}
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
    }
        public static String toHexString(byte[] hash)
        {
            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, hash);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 32)
            {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        }
        public static boolean CheckRoot()
        {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(su);
            } catch (Exception ignored)
            {
                return false;
            }
            finally {
                if(process !=null)
                {
                    try {
                        process.destroy();
                    }
                    catch (Exception ignored){}
                }
            }
            return true;
        }
}
