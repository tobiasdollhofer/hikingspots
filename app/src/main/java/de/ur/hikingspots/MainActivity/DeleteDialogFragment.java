package de.ur.hikingspots.MainActivity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import de.ur.hikingspots.R;

public class DeleteDialogFragment extends DialogFragment {

    DeleteDialogFragmentListener listener;

    public static DeleteDialogFragment newInstance(int spotPosition, String spotName, Context context) {
        DeleteDialogFragment frag = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.dialog_key_position), spotPosition);
        args.putString(context.getString(R.string.dialog_key_name), spotName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String name = getArguments().getString(getString(R.string.dialog_key_name));
        final int position = getArguments().getInt(getString(R.string.dialog_key_position));
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

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (DeleteDialogFragmentListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + getString(R.string.dialog_fragment_class_exception_text));
        }
    }
}
