package com.example.s198541.s198611.savingted;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NewGameCategoryDialog extends DialogFragment {
    private DialogClickListener callback;

    public interface DialogClickListener {
        void onItemClick(int chosenItemIndex);
        void onCancelClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (DialogClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("The calling class must implement the interface DialogClickListener!");
        }
    }

    public static NewGameCategoryDialog newInstance(String title, String[] items) {
        NewGameCategoryDialog frag = new NewGameCategoryDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArray("items", items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(bundle.getString("title"))
                .setItems(bundle.getStringArray("items"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        // the user clicked on items[index]
                        callback.onItemClick(index);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onCancelClick();
                    }
                })
                .create();

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}