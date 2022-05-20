package com.example.tellmetheodds.ui.main;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tellmetheodds.HistoryViewModel;
import com.example.tellmetheodds.R;
import com.example.tellmetheodds.RoundRecord;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements GameTimeSetupDialogFragment.GameTimeDialogListener {


    private static final long TIME_PLANNING_TIMER_STARTS_BLINKING = 15000;
    private static final long TIME_GAME_TIMER_STARTS_BLINKING = 5 * 60000; //5 minutes

    View root;
    private HistoryViewModel historyViewModel;

    private int currentRound;

    private LinearLayout planningButton;
    private Chronometer planningTimer;
    private boolean planningRunning;
    private long planningTimerOffset;
    private int planningTime;
    private int currentPlanningButtonAnim;

    private Chronometer gameTimer;
    private LinearLayout gameTimerButton;
    private boolean gameRunning;
    private long gameTimerOffset;
    private long gameTime;
    private long timeElapsed;
    private long gameTimeBase;
    private int currentGameTimeButtonAnim;

    private ObjectAnimator planningBtnAnimator;
    private ObjectAnimator gameTimeBtnAnimator;

    private Button scoreBtnPlayer1;
    private Button scoreBtnPlayer2;
    private Button scoreBtnObjectivePlayer1;
    private Button scoreBtnObjectivePlayer2;
    private Button scoreBtnCombatPlayer1;
    private Button scoreBtnCombatPlayer2;
    private int scorePlayer1;
    private int scorePlayer2;
    private int scoreCombatPlayer1;
    private int scoreCombatPlayer2;
    private int scoreObjectivesPlayer1;
    private int scoreObjectivesPlayer2;

    private LinearLayout player1Initiative;
    private LinearLayout player2Initiative;
    private TextView player1InitiativeText;
    private TextView player2InitiativeText;

    private LinearLayout roundButton;
    private TextView roundText;

    private int initiative; //0: start; 1: player 1 has initiative; 2: player 2 has initiative

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance() {

        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRound = 0;
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        scoreCombatPlayer1 = 0;
        scoreCombatPlayer2 = 0;
        scoreObjectivesPlayer1 = 0;
        scoreObjectivesPlayer2 = 0;
        planningRunning = false;
        gameRunning = false;
        planningTime = 120000;
        planningTimerOffset = 0;
        currentPlanningButtonAnim = 0;
        currentGameTimeButtonAnim = 0;
        initiative = 0;
        gameTimeBase = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_game, container, false);
        planningTimer = root.findViewById(R.id.planning_phase_timer);
        planningButton = root.findViewById(R.id.planning_phase_timer_layout);
        planningButton.setOnClickListener(planningListener);
        planningButton.setOnLongClickListener(planningResetListener);
        planningTimer.setOnChronometerTickListener(planningTimeListener);

        gameTimer = root.findViewById(R.id.game_timer);
        gameTimerButton = root.findViewById(R.id.game_timer_layout);
        gameTimerButton.setOnClickListener(gameTimePlayPauseListener);
        gameTimerButton.setOnLongClickListener(gameTimeSetupListener);
        gameTimer.setOnChronometerTickListener(gameTimeListener);

        scoreBtnPlayer1 = root.findViewById(R.id.score_p1);
        scoreBtnPlayer2 = root.findViewById(R.id.score_p2);
        scoreBtnObjectivePlayer1 = root.findViewById(R.id.score_objectives_p1);
        scoreBtnObjectivePlayer2 = root.findViewById(R.id.score_objectives_p2);
        scoreBtnCombatPlayer1 = root.findViewById(R.id.score_ships_p1);
        scoreBtnCombatPlayer2 = root.findViewById(R.id.score_ships_p2);

        scoreBtnPlayer1.setOnClickListener(addPointListener);
        scoreBtnPlayer2.setOnClickListener(addPointListener);
        scoreBtnObjectivePlayer1.setOnClickListener(addPointListener);
        scoreBtnObjectivePlayer2.setOnClickListener(addPointListener);
        scoreBtnCombatPlayer1.setOnClickListener(addPointListener);
        scoreBtnCombatPlayer2.setOnClickListener(addPointListener);

        scoreBtnPlayer1.setOnLongClickListener(subtractPointListener);
        scoreBtnPlayer2.setOnLongClickListener(subtractPointListener);
        scoreBtnObjectivePlayer1.setOnLongClickListener(subtractPointListener);
        scoreBtnObjectivePlayer2.setOnLongClickListener(subtractPointListener);
        scoreBtnCombatPlayer1.setOnLongClickListener(subtractPointListener);
        scoreBtnCombatPlayer2.setOnLongClickListener(subtractPointListener);

        roundButton = root.findViewById(R.id.round_counter_layout);
        roundText = root.findViewById(R.id.round_counter);
        roundButton.setOnClickListener(addRound);
        //roundButton.setOnLongClickListener(subtractRound);
        roundText.setText(getString(R.string.rounds,currentRound));

        player1Initiative = root.findViewById(R.id.initiative_p1_layout);
        player2Initiative = root.findViewById(R.id.initiative_p2_layout);
        player1Initiative.setOnClickListener(initiativeListener);
        player2Initiative.setOnClickListener(initiativeListener);
        player1InitiativeText = root.findViewById(R.id.initiative_p1);
        player2InitiativeText = root.findViewById(R.id.initiative_p2);
        player1InitiativeText.setText(getString(R.string.initiative));
        player2InitiativeText.setText(getString(R.string.initiative));

        //if (savedInstanceState != null){
            gameTimer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
            if (gameRunning){
                gameTimer.start();
            }
       // }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        resetPlanningTimer();
        updateScore();
        updateInitiative();


    }

    private final View.OnClickListener planningListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            startStopPlanningTimer();
        }
    };
    private final View.OnLongClickListener planningResetListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v){
            resetPlanningTimer();
            return true;
        }
    };

    private final View.OnClickListener addPointListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == scoreBtnCombatPlayer1){
                scoreCombatPlayer1++;
            } else if (view == scoreBtnObjectivePlayer1 || view == scoreBtnPlayer1){
                scoreObjectivesPlayer1++;
            } else if (view == scoreBtnCombatPlayer2){
                scoreCombatPlayer2++;
            } else if (view == scoreBtnObjectivePlayer2 || view == scoreBtnPlayer2){
                scoreObjectivesPlayer2++;
            }
            updateScore();
        }
    };

    private final View.OnLongClickListener subtractPointListener = new View.OnLongClickListener(){
      @Override
      public boolean onLongClick(View view){
          if (view == scoreBtnCombatPlayer1){
              scoreCombatPlayer1--;
          } else if (view == scoreBtnObjectivePlayer1 || view == scoreBtnPlayer1){
              scoreObjectivesPlayer1--;
          } else if (view == scoreBtnCombatPlayer2){
              scoreCombatPlayer2--;
          } else if (view == scoreBtnObjectivePlayer2 || view == scoreBtnPlayer2){
              scoreObjectivesPlayer2--;
          }
          updateScore();
          return true;
      }
    };

    private final View.OnClickListener addRound = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            historyViewModel.getRoundRecord().getValue().get(currentRound).setTimeElapsed(timeElapsed);
            currentRound++;
            roundText.setText(getString(R.string.rounds,currentRound));
            historyViewModel.addRoundRecord(new RoundRecord(currentRound));
            initiative = 0;
            updateInitiative();
            historyViewModel.updateList();
        }
    };




    private final View.OnLongClickListener subtractRound = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(currentRound > 0){
                currentRound--;
                roundText.setText(getString(R.string.rounds,currentRound));
            }
            return true;
        }
    };

    private final View.OnClickListener gameTimePlayPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            starStopGameTimer();
        }
    };

    private final View.OnLongClickListener gameTimeSetupListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            FragmentManager fm = getFragmentManager();
            GameTimeSetupDialogFragment dialog = new GameTimeSetupDialogFragment();
            dialog.setTargetFragment(GameFragment.this, 300);
            dialog.show(fm,GameTimeSetupDialogFragment.TAG);
            return true;
        }
    };

    private final View.OnClickListener initiativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == player1Initiative){
                initiative = 1;
            }
            else {
                initiative = 2;
            }
            updateInitiative();
        }
    };

    private final Chronometer.OnChronometerTickListener planningTimeListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long timeLeft =  chronometer.getBase() - SystemClock.elapsedRealtime();
            animatePlanningTimer(timeLeft);
        }
    };

    private void animatePlanningTimer(long timeLeft) {
        if (timeLeft <= TIME_PLANNING_TIMER_STARTS_BLINKING && timeLeft > 0) {
            if (currentPlanningButtonAnim == 0) {
                planningBtnAnimator = ObjectAnimator.ofInt(planningButton, "backgroundColor",
                        ResourcesCompat.getColor(getResources(), R.color.gray, null),
                        ResourcesCompat.getColor(getResources(), R.color.red, null));
                planningBtnAnimator.setDuration(500); //duration of flash
                planningBtnAnimator.setEvaluator(new ArgbEvaluator());
                planningBtnAnimator.setRepeatCount(ValueAnimator.INFINITE);
                planningBtnAnimator.setRepeatMode(ValueAnimator.REVERSE);
                planningBtnAnimator.start();

                currentPlanningButtonAnim = 1;
            }
        } else if (timeLeft <= 0) {
            planningBtnAnimator.end();
            currentPlanningButtonAnim = 2;
            planningTimer.stop();
            planningRunning = false;
        } else{
            if (currentPlanningButtonAnim > 0){
                planningBtnAnimator.end();
                planningBtnAnimator.cancel();
            }
            planningButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.util_button,null));
            currentPlanningButtonAnim = 0;
        }
    }
    private final Chronometer.OnChronometerTickListener gameTimeListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
            long timeLeft =  gameTime - timeElapsed;
            Log.i("TIME LEFT", String.valueOf(timeLeft));
            Log.i("TIME ELAPSED", String.valueOf(timeElapsed));
            if (gameTimeBase > 0){
                animateGameTimer(timeLeft, timeElapsed);
            }
        }
    };

    private void animateGameTimer(long timeLeft, long timeElapsed){
        if (timeLeft <= 0){
            gameTimer.stop();
            gameTimeBtnAnimator.end();
            gameTimerButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
            currentGameTimeButtonAnim = 2;
            gameRunning = false;
        } else if (timeElapsed >= gameTimeBase - TIME_GAME_TIMER_STARTS_BLINKING) {
            if (currentGameTimeButtonAnim == 0) {
                gameTimeBtnAnimator = ObjectAnimator.ofInt(gameTimerButton, "backgroundColor",
                        ResourcesCompat.getColor(getResources(), R.color.gray, null),
                        ResourcesCompat.getColor(getResources(), R.color.red, null));
                gameTimeBtnAnimator.setDuration(1000); //duration of flash
                gameTimeBtnAnimator.setEvaluator(new ArgbEvaluator());
                gameTimeBtnAnimator.setRepeatCount(ValueAnimator.INFINITE);
                gameTimeBtnAnimator.setRepeatMode(ValueAnimator.REVERSE);
                gameTimeBtnAnimator.start();

                currentGameTimeButtonAnim = 1;
            }
        }
        else {
            if (currentGameTimeButtonAnim != 0){
                gameTimeBtnAnimator.end();
            }
            gameTimerButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.util_button,null));
            currentGameTimeButtonAnim = 0;
        }

    }

    public void updateScore(){
        scorePlayer1 = scoreCombatPlayer1 + scoreObjectivesPlayer1;
        scorePlayer2 = scoreCombatPlayer2 + scoreObjectivesPlayer2;
        scoreBtnPlayer1.setText(String.valueOf(scorePlayer1));
        scoreBtnPlayer2.setText(String.valueOf(scorePlayer2));
        scoreBtnObjectivePlayer1.setText(String.format(getString(R.string.score_objectives), scoreObjectivesPlayer1));
        scoreBtnObjectivePlayer2.setText(String.format(getString(R.string.score_objectives), scoreObjectivesPlayer2));
        scoreBtnCombatPlayer1.setText(String.format(getString(R.string.score_combat), scoreCombatPlayer1));
        scoreBtnCombatPlayer2.setText(String.format(getString(R.string.score_combat), scoreCombatPlayer2));
        int[] scoreArrPlayer1 = new int[]{scoreCombatPlayer1,scoreObjectivesPlayer1};
        int[] scoreArrPlayer2 = new int[]{scoreCombatPlayer2,scoreObjectivesPlayer2};
        historyViewModel.getRoundRecord().getValue().get(currentRound).setPoints(scoreArrPlayer1,scoreArrPlayer2);
        historyViewModel.updateList();
    }

    public void startStopPlanningTimer(){
        if(!planningRunning) {
            planningTimer.setBase(SystemClock.elapsedRealtime()  + planningTime + planningTimerOffset);
            planningTimer.start();
            planningRunning = true;
        }else {
            planningTimer.stop();
            planningTimerOffset = planningTimer.getBase() - SystemClock.elapsedRealtime() - planningTime;
            planningRunning = false;
        }
    }

    public void resetPlanningTimer(){
        planningTimer.stop();
        planningTimerOffset = 0;
        planningRunning = false;
        planningTimer.setBase(SystemClock.elapsedRealtime()  + planningTime + planningTimerOffset);
        if(currentPlanningButtonAnim > 0){
            planningBtnAnimator.end();
            planningBtnAnimator.cancel();
            planningButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.util_button,null));
        }
    }

    public void starStopGameTimer(){
        if(!gameRunning) {
            gameTimer.setBase(SystemClock.elapsedRealtime()  - gameTimerOffset);
            gameTimer.start();
            gameRunning = true;
        }else {
            gameTimer.stop();
            gameTimerOffset = SystemClock.elapsedRealtime() - gameTimer.getBase();
            gameRunning = false;
        }
    }

    void updateInitiative(){
        switch (initiative){
            case 1:
                player1Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pressed,null));
                player2Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_enabled_2,null));
                player1InitiativeText.setText(R.string.initiative_first_player);
                player2InitiativeText.setText(R.string.initiative_second_player);
                break;
            case 2:
                player1Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_enabled,null));
                player2Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_pressed_2,null));
                player1InitiativeText.setText(R.string.initiative_second_player);
                player2InitiativeText.setText(R.string.initiative_first_player);
                break;
            default:
                player1Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_enabled,null));
                player2Initiative.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_enabled_2,null));
                player1InitiativeText.setText(getString(R.string.initiative));
                player2InitiativeText.setText(getString(R.string.initiative));
        }
        historyViewModel.getRoundRecord().getValue().get(currentRound).setInitiative(initiative);
        historyViewModel.updateList();
    }


    @Override
    public void onDialogPositiveClick(int rolledTime, int baseTime) {
        Log.i("GAME TIME", String.valueOf(rolledTime));
        gameTime = rolledTime * 60000L;
        gameTimeBase = baseTime * 60000L;

        gameTimer.stop();
        gameTimerOffset = 0;
        gameTimer.setBase(SystemClock.elapsedRealtime());
        gameRunning = false;
    }
}