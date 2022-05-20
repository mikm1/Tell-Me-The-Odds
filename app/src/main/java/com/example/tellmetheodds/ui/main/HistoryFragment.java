package com.example.tellmetheodds.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tellmetheodds.HistoryViewModel;
import com.example.tellmetheodds.R;
import com.example.tellmetheodds.RoundRecord;
import com.example.tellmetheodds.ThrowRecord;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    HistoryViewModel historyViewModel;
    View root;
    private LinearLayout roundsLayout;

    private int[][] rolls;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_history, container, false);
        roundsLayout = root.findViewById(R.id.roundsLayout);
        roundsLayout.addView(createDetailsText(0,getString(R.string.empty_history)));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);


        historyViewModel.getRoundRecord().observe(getViewLifecycleOwner(), historyObserver);
    }
    final Observer<LinkedList<RoundRecord>> historyObserver = new Observer<LinkedList<RoundRecord>>() {
        @Override
        public void onChanged(LinkedList<RoundRecord> roundRecords) {
            roundsLayout.removeAllViewsInLayout();
            createLayout(roundRecords);
        }
    };
    public void createLayout (LinkedList<RoundRecord> roundRecords){
        
        // 1st row: 0 - unassigned; 1 - player 1, 2 - player 2
        // 2nd row: 0 - hit, 1 - crit, 2 - red focus, 3 - red blank, 4 - evade, 5 - green focus, 6 - green blank
        rolls = new int[3][7];

        // 0 - no of rounds with unassigned initiative; 1 - no of rounds with player 1 init; 2 - same for p2;
        int[] initiatives = new int[]{0, 0, 0};
        
        if (roundRecords == null){
            return;
        }
        for (int i = 0; i < roundRecords.size(); i++) {

            RoundRecord currentRecord = roundRecords.get(i);
            initiatives[currentRecord.getInitiative()]++; // count number of rounds with each player's initiative

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,10,0,10);
            roundsLayout.addView(createExpendableButton(currentRecord.getInitiative(),
                    String.format(getString(R.string.round_button_text),
                    currentRecord.getRoundNumber(),
                    (currentRecord.getPointsP1()[0] + currentRecord.getPointsP1()[1]),
                    (currentRecord.getPointsP2()[0] + currentRecord.getPointsP2()[1]))), params);

            LinearLayout detailsLayout = new LinearLayout(getContext());
            detailsLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            detailParams.setMargins(0,0,0,5);

            detailsLayout.addView(createDetailsText(0,getString(R.string.score_title)),detailParams);
            detailsLayout.addView(createDetailsText(1,String.format(getString(R.string.extendable_text), currentRecord.getPointsP1()[0], currentRecord.getPointsP1()[1])), detailParams);
            detailsLayout.addView(createDetailsText(2,String.format(getString(R.string.extendable_text), currentRecord.getPointsP2()[0], currentRecord.getPointsP2()[1])), detailParams);

            if (!currentRecord.getRolls().isEmpty()) {
                detailsLayout.addView(createDetailsText(0,getString(R.string.dice_text)),detailParams);
                for (ThrowRecord record : currentRecord.getRolls()) {
                    detailsLayout.addView(createDetailsText(record.getPlayer(),record.toString()),detailParams);
                    int offset = 0; // throwrecord has a int[4] or int[3] array depending on the type of the dice. in rolls[][] array all possible dice results are counted, first red then green
                    if (!record.isRedDieThrow()){
                        offset = 4;
                    }
                    for (int j = 0; j < record.getResults().length; j ++){
                        rolls[record.getPlayer()][j+offset] += record.getResults()[j];
                    }
                }
            }
            detailsLayout.addView(createDetailsText(0,"-"),detailParams);
            detailsLayout.setVisibility(View.GONE);
            roundsLayout.addView(detailsLayout);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,10,0,10);
        roundsLayout.addView(createExpendableButton(0,getString(R.string.stats)),params);

        LinearLayout detailsLayout = new LinearLayout(getContext());
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailParams.setMargins(0,0,0,5);

        detailsLayout.addView(createDetailsText(0,getString(R.string.game_stats_title)),detailParams);
        if (initiatives[0] > 0){
            detailsLayout.addView(createDetailsText(0,String.format(getString(R.string.initiative_count_unassigned),getResources().getQuantityString(R.plurals.round_plurals, initiatives[0], initiatives[0]))),detailParams);
        }
        for (int i = 1; i < 3; i++){
            String text;
            if (initiatives[i] == 0){
                text = getString(R.string.never_first);
            } else if (initiatives[i] == (initiatives[0]+ initiatives[1]+ initiatives[2])){
                text = getString(R.string.always_first);
            } else {
                text = String.format(getString(R.string.initiative_count),getResources().getQuantityString(R.plurals.round_plurals, initiatives[i], initiatives[i]));
            }
            detailsLayout.addView(createDetailsText(i,text),detailParams);
        }

        detailsLayout.addView(createDetailsText(0,getString(R.string.dice_roll_stats)),detailParams);
        createDiceStatsText(1,detailsLayout,detailParams);
        createDiceStatsText(2,detailsLayout,detailParams);
        createDiceStatsText(0,detailsLayout,detailParams);


        roundsLayout.addView(detailsLayout);
        detailsLayout.setVisibility(View.GONE);
    }

    private Button createExpendableButton (int player, String text){
        Button button = new Button(getContext());
        text = text + "  +";     // only works if  linefeed between them! "\n ";
        button.setText(text);
        button.setTextSize(48);
        switch (player){
            case 1:
                button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.button_p1, null));
                break;
            case 2:
                button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.button_p2, null));
                break;
            default:
                button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.util_button, null));
                break;
        }
        button.setOnClickListener(expandListener);
        return button;
    }

    private TextView createDetailsText (int player, String text){
        TextView textview = new TextView(getContext());
        textview.setText(text);
        textview.setTextSize(28);
        textview.setGravity(Gravity.CENTER_HORIZONTAL);
        textview.setTextColor(ResourcesCompat.getColor(getResources(),R.color.white,null));
        switch (player){
            case 1:
                textview.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.button_enabled,null));
                break;
            case 2:
                textview.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.button_enabled_2,null));
                break;
            default:
                textview.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.util_button_enabled,null));
                break;
        }
        return textview;
    }

    private void createDiceStatsText (int player, LinearLayout layout, LinearLayout.LayoutParams params){
        int redDiceRolled = rolls[player][0] + rolls[player][1] + rolls[player][2] + rolls[player][3];
        int greenDiceRolled = rolls[player][4] + rolls[player][5] + rolls[player][6];
        int playerType; // if player == 0 the text for total dice rolls changes from "x dice rolled in total" to "x dice unaccounted for". playerType is an ID number for the appropriate string resource

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(2);

        float[]expectedResults = {
                (float)redDiceRolled * 0.375f,
                (float)redDiceRolled * 0.125f,
                (float)redDiceRolled * 0.25f,
                (float)redDiceRolled * 0.25f,
                (float)greenDiceRolled * 0.375f,
                (float)greenDiceRolled * 0.25f,
                (float)greenDiceRolled * 0.375f
        };
        if (player == 0){
            playerType = R.string.rolled_total_dice_unassigned;
        } else {
            playerType = R.string.rolled_total_dice;
        }
        if (player > 0 || redDiceRolled > 0){ //don't display "unaccounted for" message if every die has been assigned to a player
            layout.addView(createDetailsText(player,String.format(getString(playerType),getResources().getQuantityString(R.plurals.red_dice_plurals,redDiceRolled,redDiceRolled))),params);
        }
        if(redDiceRolled > 0){
            for (int i = 0; i < 4; i++){
                int resultType;
                switch (i){
                    case 0:
                        resultType = R.plurals.hits;
                        break;
                    case 1:
                        resultType = R.plurals.crits;
                        break;
                    case 2:
                        resultType = R.plurals.focus;
                        break;
                    default:
                        resultType = R.plurals.blank;
                        break;
                }
                layout.addView(createDetailsText(player,String.format(getString(R.string.rolled_results),getResources().getQuantityString(resultType,rolls[player][i],rolls[player][i]),df.format(expectedResults[i]))),params);
            }
        }
        if (player > 0 || greenDiceRolled > 0) { //don't display "unaccounted for" message if every die has been assigned to a player
            layout.addView(createDetailsText(player, String.format(getString(playerType), getResources().getQuantityString(R.plurals.green_dice_plurals, greenDiceRolled, greenDiceRolled))), params);
        }
        if(greenDiceRolled > 0){
            for (int i = 4; i < 7; i++){
                int resultType;
                switch (i){
                    case 4:
                        resultType = R.plurals.evade;
                        break;
                    case 5:
                        resultType = R.plurals.focus;
                        break;
                    default:
                        resultType = R.plurals.blank;
                        break;
                }
                layout.addView(createDetailsText(player,String.format(getString(R.string.rolled_results),getResources().getQuantityString(resultType,rolls[player][i],rolls[player][i]),df.format(expectedResults[i]))),params);
            }
        }
        if (player > 0 || greenDiceRolled > 0) {
            layout.addView(createDetailsText(0,"-"),params);
        }
    }

    private final View.OnClickListener expandListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            LinearLayout parent = (LinearLayout) view.getParent();
            int index = parent.indexOfChild(view);
            View details = parent.getChildAt(index+1);
            CharSequence s = button.getText();
            if (parent.getChildAt(index+1).getVisibility() == View.GONE){
                details.setVisibility(View.VISIBLE);
                s = s.subSequence(0,s.length()-1) + "-";
            } else {
                details.setVisibility(View.GONE);
                s = s.subSequence(0,s.length()-1) + "+";
            }
            button.setText(s);
        }
    };

}