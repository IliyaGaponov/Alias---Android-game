package il.co.alias;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static il.co.alias.ConstantsHolder.*;

/**
 * Created by igapo on 19.09.2018.
 */

public class GameActivity extends AppCompatActivity implements Animation.AnimationListener {

    private Menu menu;
    private ProgressBar progressBar;
    private TextView progressBarText;
    private String roundTime;
    private int intNumOfGuesses = 0, intNumOfSkipped = 0;
    private TextView numOfGuessesTv, card, numOfSkippedTv;
    private CountDownTimer mCountDownTimer;
    private Long timeToFinish;
    private FrameLayout mainLayout;
    private boolean lastWord, mGameIsStarted = false, isCommonWordGuessed = true, gameOnPause = true;
    private String[] teams;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout.LayoutParams cardParams;
    private MediaPlayer passSound;
    private MediaPlayer guessSound;
    private Animation cardTopAnim, cardBottomAnim;
    private List<String> allDictionaryWords, usedWords;
    private List<Boolean> guessedOrSkippedList;
    private View view;
    private float mainLayoutCenterY, cardCenterY;
    private int startTopMargin, startLeftMargin;
    private MPCompleteListener mpListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        TextView guessedTv = findViewById(R.id.guessed_tv);
        TextView skippedTv = findViewById(R.id.skipped_tv);
        progressBar = findViewById(R.id.circularProgressBar);
        progressBarText = findViewById(R.id.progressBarText);
        card = findViewById(R.id.card);
        numOfGuessesTv = findViewById(R.id.numOfGuessesTv);
        numOfSkippedTv = findViewById(R.id.numOfSkipped);
        mainLayout = findViewById(R.id.mainLayout);

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setTitle(sharedPreferences.getString(CURRENT_TEAM_NAME,""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mpListener = new MPCompleteListener();

        // set round time
        roundTime = sharedPreferences.getString(ROUND_TIME, "");
        progressBar.setMax(Integer.parseInt(roundTime));
        progressBarText.setText(roundTime);

        // load words from dictionary
        String dictionaryName = sharedPreferences.getString(DICTIONARY, "");
        int dictResId = getResources().getIdentifier(dictionaryName, "array", getPackageName());
        allDictionaryWords = new LinkedList<>(Arrays.asList(getResources().getStringArray(dictResId)));

        usedWords = new ArrayList<>();
        guessedOrSkippedList = new ArrayList<>();

        // set start card params
        cardParams = (FrameLayout.LayoutParams) card.getLayoutParams();
        startTopMargin = cardParams.topMargin;
        startLeftMargin = cardParams.leftMargin;

        // load animations
        cardTopAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.card_anim_to_top);
        cardTopAnim.setAnimationListener(GameActivity.this);
        cardBottomAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.card_anim_to_bottom);
        cardBottomAnim.setAnimationListener(GameActivity.this);


        card.setOnTouchListener(new View.OnTouchListener()
        {
            int delayX = 0, delayY = 0;

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                view = v;
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(gameOnPause)
                        {
                            continueTimer();
                            gameOnPause = false;
                        }

                        delayX = x - cardParams.leftMargin;
                        delayY = y - cardParams.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        cardParams.leftMargin = x - delayX;
                        cardParams.topMargin = y - delayY;
                        v.setLayoutParams(cardParams);
                        break;

                    case MotionEvent.ACTION_UP:
                        // first card with text start
                        if (!mGameIsStarted)
                        {
                            MediaPlayer gong = MediaPlayer.create(getApplicationContext(), R.raw.gong);
                            gong.start();
                            card.startAnimation(cardTopAnim);
                            mGameIsStarted = true;
                            runTimer(Long.parseLong(roundTime));
                        }
                        else if (!lastWord)
                        {
                            countGuessesAndPasses();
                        }

                        break;
                }

                return true;
            }
        });

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            Intent instructionIntent = new Intent(this, InstructionActivity.class);
            instructionIntent.putExtra(INSTRUCTION, getResources().getString(R.string.game_instructions));
            instructionIntent.putExtra(ACTION_BAR_TITLE, getSupportActionBar().getTitle());
            startActivity(instructionIntent);
        }

        guessedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increasePoints();
            }
        });

        skippedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reducePoints();
            }
        });
    }

    private void countGuessesAndPasses()
    {
        mainLayoutCenterY = mainLayout.getHeight() / 2;
        cardCenterY = card.getHeight() / 2 + card.getY();
        // card is above the center
        if (mainLayoutCenterY > cardCenterY)
        {
            increasePoints();
        }
        // card is under the center
        else if (mainLayoutCenterY < cardCenterY)
        {
            reducePoints();
        }
    }


    private void increasePoints()
    {
        guessSound = MediaPlayer.create(this, R.raw.guessed);
        guessSound.setOnCompletionListener(mpListener);
        guessSound.start();
        guessedOrSkippedList.add(true);
        card.startAnimation(cardTopAnim);
        intNumOfGuesses++;
        numOfGuessesTv.setText(Integer.toString(intNumOfGuesses));
        usedWords.add(card.getText().toString());
    }
    private void reducePoints() {

        passSound = MediaPlayer.create(this, R.raw.pass);
        passSound.setOnCompletionListener(mpListener);
        passSound.start();
        guessedOrSkippedList.add(false);
        card.startAnimation(cardBottomAnim);
        intNumOfSkipped++;
        numOfSkippedTv.setText(Integer.toString(intNumOfSkipped));
        usedWords.add(card.getText().toString());
    }

//    private void increasePoints()
//    {
//        usedWords.add(card.getText().toString());
//        guessedOrSkippedList.add(true);
//        card.startAnimation(cardTopAnim);
//        intNumOfGuesses++;
//        numOfGuessesTv.setText(Integer.toString(intNumOfGuesses));
//        guessSound.start();
//    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mCountDownTimer != null) {
            stopTimer();
        }
    }

    private String getRandWord() {
        String randWord = "";
        if (allDictionaryWords.size() > 0) {
            Random random = new Random();
            int randNum = random.nextInt(allDictionaryWords.size());
            randWord = allDictionaryWords.get(randNum);
            allDictionaryWords.remove(randNum);
        }

        return randWord;
    }

    private void loadResultsActivity() {
        Intent roundResultsIntent = new Intent(GameActivity.this, RoundResultsActivity.class);
        // nobody guessed the last word
        if(!isCommonWordGuessed) {
            roundResultsIntent.putExtra(IS_COMMON_WORD_GUESSED, isCommonWordGuessed);
        }

        Gson gson = new Gson();
        String jsonUsedWords = gson.toJson(usedWords);
        roundResultsIntent.putExtra(USED_WORDS, jsonUsedWords);
        String jsonGuessedOrSkipped = gson.toJson(guessedOrSkippedList);
        roundResultsIntent.putExtra(GUESSED_OR_SKIPPED_LIST, jsonGuessedOrSkipped);
        startActivity(roundResultsIntent);
    }

    private void loadCommonDialog() {
        teams = SharedPreferencesOperations.loadTeams(sharedPreferences).toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(
                getResources().getString(R.string.commonFinalWord) + " " + card.getText() +
                        " " + getResources().getString(R.string.guessedBy)).
                setItems(teams, new DialogLastWordListener())
                .setNegativeButton("Nobody", new DialogLastWordListener()).show();
    }

    @Override
    public void onAnimationStart(Animation animation) { }

    @Override
    public void onAnimationEnd(Animation animation) {
        cardParams.topMargin = startTopMargin;
        cardParams.leftMargin = startLeftMargin;
        view.setLayoutParams(cardParams);
        card.setText(getRandWord());
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    private class DialogLastWordListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int index) {
            // if nobody guessed
            if(index == DialogInterface.BUTTON_NEGATIVE)
            {
                isCommonWordGuessed = false;
            }
            else
            {
                String guessedTeamScore = sharedPreferences.getString(teams[index] + "Score", null);
                int numOfGuesses = Integer.valueOf(guessedTeamScore) + 1;
                editor.putString(teams[index] + "Score", String.valueOf(numOfGuesses));
                editor.apply();
            }

            loadResultsActivity();
        }
    }

    private void runTimer(Long timerStartFrom) {
        mCountDownTimer = new CountDownTimer(timerStartFrom * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeToFinish = millisUntilFinished;
                int roundTimeInt = Integer.parseInt(roundTime);
                long seconds = millisUntilFinished / 1000;
                int barVal = (roundTimeInt) - ((int) (seconds / roundTimeInt * 100) + (int) (seconds % roundTimeInt));
                progressBar.setProgress(barVal);
                progressBarText.setText(String.format("%02d", seconds / roundTimeInt) + ":" + String.format("%02d", seconds % roundTimeInt));
            }

            @Override
            public void onFinish()
            {
                progressBar.setProgress(Integer.parseInt(roundTime));
                // will be shown dialog
                if (sharedPreferences.getBoolean(IS_COMMON_WORD, false))
                {
                    loadCommonDialog();
                }
                // the game will automatically stopped
                else
                {
                    loadResultsActivity();
                }
            }
        };

        mCountDownTimer.start();
    }

    private class OnDialogClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == DialogInterface.BUTTON_POSITIVE)
            {
                int nextTeamIndex = sharedPreferences.getInt(NEXT_TEAM, 0);// in order to this team will play next time
                nextTeamIndex--;
                editor.putInt(NEXT_TEAM, nextTeamIndex).apply();
                Intent mainActivityIntent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
            }
            if(which == DialogInterface.BUTTON_NEGATIVE)
            {
                continueTimer();
            }
        }
    }

    private void continueTimer() {
        menu.findItem(R.id.start).setVisible(false);
        menu.findItem(R.id.pause).setVisible(true);
        gameOnPause = false;
        if(mCountDownTimer != null) {
            runTimer(timeToFinish / 1000);
        }
    }

    private void stopTimer() {
        menu.findItem(R.id.start).setVisible(true);
        menu.findItem(R.id.pause).setVisible(false);
        gameOnPause = true;
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_to_menu_question)
                .setMessage(R.string.progress_will_be_deleted)
                .setPositiveButton(R.string.yes, new OnDialogClickListener()).setNegativeButton(R.string.cancel, new OnDialogClickListener())
                .show();
        stopTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.game_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.pause)
        {
            if(mCountDownTimer != null) {
                stopTimer();
                item.setVisible(false);
                menu.findItem(R.id.start).setVisible(true);
            }
        }
        else if(item.getItemId() == R.id.start)
        {
            continueTimer();
            item.setVisible(false);
            menu.findItem(R.id.pause).setVisible(true);
        }

        return super.onOptionsItemSelected(item);
    }

    private class MPCompleteListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
        }
    }
}

