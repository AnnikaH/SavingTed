package com.example.s198541.s198611.savingted;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ResetWarningDialog extends DialogFragment {
    private DialogClickListener callback;

    public interface DialogClickListener {
        void onResetClick();
        void onCancelResetClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (DialogClickListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("The calling class must implement the interface DialogClickListener!");
        }
    }

    public static ResetWarningDialog newInstance(String title, String message) {
        ResetWarningDialog frag = new ResetWarningDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(bundle.getString("title"))
                .setMessage(bundle.getString("message"))
                .setPositiveButton(R.string.reset_games_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onResetClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onCancelResetClick();
                    }
                }).create();

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
