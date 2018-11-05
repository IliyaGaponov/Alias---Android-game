package il.co.alias;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import static il.co.alias.ConstantsHolder.*;
import static il.co.alias.ConstantsHolder.TeamCardTypeEnum.TEAM_RESULT_CARD;


/**
 * Created by igapo on 01.10.2018.
 */

public class TeamsResultsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout mainLayout;
    private RecyclerView recyclerView;
    private int nextTeamIndex;
    private List<String> teamsInGame;
    private List<String> scoresList;
    private MediaPlayer applause;
    private TeamAdapter teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_results);

        Button startBtn = findViewById(R.id.start_btn);
        mainLayout = findViewById(R.id.main_results_layout);
        recyclerView = findViewById(R.id.teams_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        teamsInGame = SharedPreferencesOperations.loadTeams(sharedPreferences);
        scoresList = SharedPreferencesOperations.loadScores(sharedPreferences, teamsInGame);
        teamAdapter = new TeamAdapter(teamsInGame, scoresList, TEAM_RESULT_CARD);
        recyclerView.setAdapter(teamAdapter);
        setNextTeamToTv();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGameIntent = new Intent(TeamsResultsActivity.this, GameActivity.class);
                startActivity(startGameIntent);
            }
        });

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            editor.putBoolean(IS_FIRST_APP_USING, false).apply();
        }

        //checking if the game is finished
        if(nextTeamIndex == 0 && sharedPreferences.getBoolean(END_OF_GAME, false) == true)
        {
            checkTheWinner();
        }

    }

    private void checkTheWinner()
    {
        List<String> teams = new ArrayList<>(teamsInGame);
        int maxScore = 0;

        for(String team : teams)
        {
            String strScore = sharedPreferences.getString(team + "Score", null);
            int score = Integer.parseInt(strScore);
            if(score > maxScore)
            {
                maxScore = score;
                int i = 0;
                while(teamsInGame.indexOf(team) != 0)
                {
                    teamsInGame.remove(i);
                    scoresList.remove(i);
                    teamAdapter.notifyItemRemoved(i);
                }

            }
            else if(score < maxScore)
            {
                int indexOfTeam = teamsInGame.indexOf(team);
                teamsInGame.remove(indexOfTeam);
                scoresList.remove(indexOfTeam);
                teamAdapter.notifyItemRemoved(indexOfTeam);
            }
        }

        if(teamsInGame.size() == 1)
        {
            showWinnerDialog(teamsInGame.get(0));
        }
        // if there is more than one winner
        else
        {
            SharedPreferencesOperations.saveParticipatingTeams(sharedPreferences, teamsInGame); // check if need
        }
    }

    private void setNextTeamToTv()
    {
        nextTeamIndex = sharedPreferences.getInt(NEXT_TEAM, 0);
        nextTeamIndex = (nextTeamIndex < teamsInGame.size() - 1) ? ++nextTeamIndex : 0; // is nextTeamIndex is last member
        TextView nextTeam = findViewById(R.id.next_team_name);
        nextTeam.setText(teamsInGame.get(nextTeamIndex)); //set the next team for playing
        editor.putInt(NEXT_TEAM, nextTeamIndex);
        editor.putString(CURRENT_TEAM_NAME, teamsInGame.get(nextTeamIndex));
        editor.apply();
    }

    private void showWinnerDialog(String winner)
    {
        SharedPreferencesOperations.clearPreferences(sharedPreferences);
        applause = MediaPlayer.create(getApplicationContext(), R.raw.applause);
        applause.start();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.cup_image, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(winner + " " + getResources().getString(R.string.congratulations))
                .setNegativeButton(getResources().getString(R.string.close), new EndGameDialogListener())
                .setView(dialogLayout).show();
    }

    private class EndGameDialogListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == DialogInterface.BUTTON_NEGATIVE)
            {
                applause.stop();
                finish();
                callMainActivity();
            }
        }
    }

    private void callMainActivity() {
        nextTeamIndex--;
        editor.putInt(NEXT_TEAM, nextTeamIndex).apply();
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callMainActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            callMainActivity();
        }

        return true;
    }
}
