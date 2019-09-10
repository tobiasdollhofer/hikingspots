package de.ur.hikingspots;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {

    public static DeleteDialogFragment newInstance(int spotPosition, String spotName) {
        DeleteDialogFragment frag = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.DIALOG_KEY_POSITION, spotPosition);
        args.putString(Constants.DIALOG_KEY_NAME, spotName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String name = getArguments().getString(Constants.DIALOG_KEY_NAME);
        final int position = getArguments().getInt(Constants.DIALOG_KEY_POSITION);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.dialog_fragment_message) + " " + name)
                .setPositiveButton(R.string.dialog_fragment_positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(position);
                    }
                })
                .setNegativeButton(R.string.dialog_fragment_negative_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public interface DeleteDialogFragmentListener{
        public void onDialogPositiveClick(int position);
    }

    DeleteDialogFragmentListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (DeleteDialogFragmentListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DeleteDialogFragmentListener");
        }
    }
}
