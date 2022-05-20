package com.example.tellmetheodds.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tellmetheodds.R;

public class GameTimeSetupDialogFragment extends DialogFragment {

    private int time;
    private int rolledTime;
    private EditText inputTime;
    private CheckBox randomizeBox;
    public static String TAG = "GameTimeSetupDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputTime = view.findViewById(R.id.game_time_input);
                        randomizeBox = view.findViewById(R.id.randomize_checkBox);
                        if (!inputTime.getText().toString().equals("") && Integer.parseInt(inputTime.getText().toString()) > 0){
                            time = Integer.parseInt(inputTime.getText().toString());
                            if(randomizeBox.isChecked()){
                                rolledTime = AMG_roll(time);
                            } else rolledTime = time;
                            GameTimeDialogListener listener = (GameTimeDialogListener) getTargetFragment();
                            listener.onDialogPositiveClick(rolledTime,time);
                            dismiss();
                        }

                    }
                });
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    public interface GameTimeDialogListener{
        void onDialogPositiveClick(int rolledTime, int baseTime);
    }

    private int AMG_roll(int baseTime){
        int max = 8;
        int min = 1;
        int timeToAdd = 0;
        int attackDie = (int) (Math.random()*(max - min) + min);
        for (int i = 0; i < 3; i++){
            if ((int) (Math.random()*(max - min) + min) > 3){
                timeToAdd++;
            }
        }
        if (attackDie > 4){
            return baseTime + timeToAdd;
        } else if (attackDie > 2){
            return  baseTime - timeToAdd;
        } else {
            return baseTime;
        }
    }
}
