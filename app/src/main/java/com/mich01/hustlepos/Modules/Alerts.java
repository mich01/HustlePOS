package com.mich01.hustlepos.Modules;

import android.app.AlertDialog;
import android.content.Context;

public class Alerts
{
    public void AlertUser(Context context, String Title, String AlertMessage)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(Title);
        alertDialog.setMessage(AlertMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

}
