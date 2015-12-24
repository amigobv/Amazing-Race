package moc5.amazingrace;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Blade on 24.12.2015.
 */
//helper class that wraps dialog builder
public class DialogHelper {
    public static void showAlert(Context ctx, int messageId, int titleId, DialogInterface.OnClickListener onOk) {
        new AlertDialog.Builder(ctx)
                .setTitle(titleId)
                .setNeutralButton(R.string.dialogOk, onOk)
                .create()
                .show();
    }
}
