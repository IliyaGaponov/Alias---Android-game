package il.co.alias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static il.co.alias.ConstantsHolder.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by igapo on 22.09.2018.
 */

public class RoundResultsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView scoreTv;
    private List<String> usedWords;
    private List<Boolean> guessedOrSkippedList;
    private String participatingTeam;
    private String teamScore;
    private int intScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_results);

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadWords();
        setParticipatingTeam();

        TextView teamNameTv = findViewById(R.id.team_name);
        teamNameTv.setText(participatingTeam + ": ");

        scoreTv = findViewById(R.id.score_tv);
        teamScore = getTeamScore();
        scoreTv.setText(teamScore);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final WordResultAdapter wordResultAdapter = new WordResultAdapter(usedWords, guessedOrSkippedList);
        wordResultAdapter.setListener(new WordResultAdapter.IWordResultListener() {

            @Override
            public void onWordResultClicked(View view, int position) {
                String viewTag = (String) view.getTag();

                if(viewTag.equals("guessedIv"))
                {
                    if(guessedOrSkippedList.get(position) == null)
                    {
                        intScore++;
                    }
                    else if(guessedOrSkippedList.get(position) == false)
                    {
                        intScore += 2;
                    }

                    guessedOrSkippedList.set(position, true);
                }
                else if(viewTag.equals("skippedIv"))
                {
                    if(guessedOrSkippedList.get(position) == null)
                    {
                        intScore--;
                    }
                    else if(guessedOrSkippedList.get(position) == true)
                    {
                        intScore -= 2;
                    }

                    guessedOrSkippedList.set(position, false);
                }
                else if(viewTag.equals("elseIv"))
                {
                    if(guessedOrSkippedList.get(position) != null)
                    {
                        if (guessedOrSkippedList.get(position) == false)
                        {
                            intScore++;
                        }
                        else if (guessedOrSkippedList.get(position) == true)
                        {
                            intScore--;
                        }

                        guessedOrSkippedList.set(position, null);
                    }
                }

                scoreTv.setText(String.valueOf(intScore));
                wordResultAdapter.notifyItemChanged(position);
            }
        });

        recyclerView.setAdapter(wordResultAdapter);

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            Intent instructionIntent = new Intent(this, InstructionActivity.class);
            instructionIntent.putExtra(INSTRUCTION, getResources().getString(R.string.round_result_instructions));
            instructionIntent.putExtra(ACTION_BAR_TITLE, getSupportActionBar().getTitle());
            startActivity(instructionIntent);
        }
    }

    private String getTeamScore()
    {
        for(Boolean result : guessedOrSkippedList)
        {
            intScore = (result.equals(true)) ? ++intScore : --intScore;
        }

        return String.valueOf(intScore);
    }

    private void loadWords()
    {
        Gson gson = new Gson();

        String jsonWords = getIntent().getStringExtra(USED_WORDS);
        Type wordsType = new TypeToken<List<String>>() {}.getType();
        usedWords = gson.fromJson(jsonWords, wordsType);

        String jsonString = getIntent().getStringExtra(GUESSED_OR_SKIPPED_LIST);
        Type guessedOrSkippedType = new TypeToken<List<Boolean>>() { }.getType();
        guessedOrSkippedList = gson.fromJson(jsonString, guessedOrSkippedType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
            savePreferences();
            Intent teamsResultActivityIntent = new Intent(RoundResultsActivity.this, TeamsResultsActivity.class);
            startActivity(teamsResultActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePreferences()
    {
        String score = sharedPreferences.getString(participatingTeam + "Score", null);
        score = String.valueOf(Integer.valueOf(score) + intScore);
        editor.putString(participatingTeam + "Score", score);
        editor.apply();
    }

    // the team which was played now
    private void setParticipatingTeam()
    {
        int teamIndex = sharedPreferences.getInt(NEXT_TEAM, 0);
        List<String> teams = SharedPreferencesOperations.loadTeams(sharedPreferences);
        participatingTeam = teams.get(teamIndex);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_to_menu_question)
                .setMessage(R.string.progress_will_be_deleted)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePreferences();
                        Intent mainActivityIntent = new Intent(RoundResultsActivity.this, MainActivity.class);
                        startActivity(mainActivityIntent);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }
}
