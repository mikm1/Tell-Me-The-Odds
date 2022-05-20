package com.example.tellmetheodds.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tellmetheodds.HistoryViewModel;
import com.example.tellmetheodds.R;
import com.example.tellmetheodds.ThrowRecord;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiceFragment extends Fragment {

    View root;

    private HistoryViewModel historyViewModel;

    private boolean isRedThrow;
    private boolean isLogEmpty;

    private int[] results;

    private int player;

    private Button hitBtn;
    private Button critBtn;
    private Button redFocusBtn;
    private Button redBlankBtn;
    private Button evadeBtn;
    private Button greenFocusBtn;
    private Button greenBlankBtn;
    private Button backspaceBtn;
    private Button saveBtn;
    private Button player1btn;
    private Button player2btn;
    private TextView throwLog;

    public DiceFragment() {
        // Required empty public constructor
    }

    public static DiceFragment newInstance() {
        return new DiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dice, container, false);
        hitBtn = root.findViewById(R.id.hit_btn);
        critBtn = root.findViewById(R.id.crit_btn);
        redFocusBtn= root.findViewById(R.id.red_eye_btn);
        redBlankBtn = root.findViewById(R.id.red_blank_btn);
        evadeBtn = root.findViewById(R.id.evade_btn);
        greenBlankBtn = root.findViewById(R.id.green_blank_btn);
        greenFocusBtn = root.findViewById(R.id.green_eye_btn);
        backspaceBtn = root.findViewById(R.id.backspace_btn);
        saveBtn = root.findViewById(R.id.save_btn);
        player1btn = root.findViewById(R.id.p1_btn);
        player2btn = root.findViewById(R.id.p2_btn);
        throwLog = root.findViewById(R.id.log_text);

        hitBtn.setText(getString(R.string.hit));
        critBtn.setText(getString(R.string.crit));
        redFocusBtn.setText(getString(R.string.eye));
        redBlankBtn.setText(getString(R.string.blank));
        evadeBtn.setText(getString(R.string.evade));
        greenBlankBtn.setText(getString(R.string.blank));
        greenFocusBtn.setText(getString(R.string.eye));
        backspaceBtn.setText(getString(R.string.clear));
        saveBtn.setText(getString(R.string.save));
        player1btn.setText(getString(R.string.player_1));
        player2btn.setText(getString(R.string.player_2));

        hitBtn.setOnClickListener(addRedResultListener);
        critBtn.setOnClickListener(addRedResultListener);
        redFocusBtn.setOnClickListener(addRedResultListener);
        redBlankBtn.setOnClickListener(addRedResultListener);

        evadeBtn.setOnClickListener(addGreenResultListener);
        greenFocusBtn.setOnClickListener(addGreenResultListener);
        greenBlankBtn.setOnClickListener(addGreenResultListener);

        backspaceBtn.setOnClickListener(clearListener);

        player1btn.setOnClickListener(playerChoiceListener);
        player2btn.setOnClickListener(playerChoiceListener);

        saveBtn.setOnClickListener(saveResultsListener);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        resetResults();
        player = 0;
    }

    View.OnClickListener addRedResultListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isRedThrow){
                switchColor();
            }
            if (view == hitBtn){
                results[0]++;
                appendLog(getString(R.string.hit));
            } else if (view == critBtn){
                results[1]++;
                appendLog(getString(R.string.crit));
            } else if (view == redFocusBtn){
                results[2]++;
                appendLog(getString(R.string.eye));
            } else{
                results[3]++;
                appendLog(getString(R.string.blank));
            }
        }
    };

    View.OnClickListener addGreenResultListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isRedThrow){
                switchColor();
            }
            if (view == evadeBtn){
                results[0]++;
                appendLog(getString(R.string.evade));
            } else if (view == greenFocusBtn){
                results[1]++;
                appendLog(getString(R.string.eye));
            } else{
                results[2]++;
                appendLog(getString(R.string.blank));
            }
        }
    };

    View.OnClickListener clearListener = view -> resetResults();

    View.OnClickListener playerChoiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == player1btn){
                player = 1;
                player1btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pressed, null));
                player2btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_p2, null));
            } else {
                player = 2;
                player1btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_p1, null));
                player2btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pressed_2, null));
            }
        }
    };

    View.OnClickListener saveResultsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isLogEmpty) {
                historyViewModel.addThrowRecord(new ThrowRecord(player, results, isRedThrow));
                resetResults();
            }
        }
    };

    void appendLog (String result){
        if (isLogEmpty){
            throwLog.setText(result);
            isLogEmpty = false;
        } else {
            throwLog.append(" + " + result);
        }
    }

    void resetResults(){
        if (isRedThrow){
            results = new int[]{0,0,0,0};
        } else {
            results = new int[]{0,0,0};
        }
        throwLog.setText("");
        isLogEmpty = true;
    }

    void switchColor(){
        isRedThrow = !isRedThrow;
        resetResults();
    }
}