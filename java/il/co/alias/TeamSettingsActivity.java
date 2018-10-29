package il.co.alias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static il.co.alias.ConstantsHolder.*;

/**
 * Created by igapo on 01.10.2018.
 */

public class TeamSettingsActivity extends AppCompatActivity implements Animation.AnimationListener {

    private SharedPreferences sharedPreferences;
    private List<String> allPossibleTeamsNames;
    private List<String> teamsInGame;
    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private int positionOfItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_settings);

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        //changing the direction of window according to the language of app
        LayoutDirection.setLayoutDirection(sharedPreferences.getString(APP_LANGUAGE, "en"), getWindow());
        setTitle(getResources().getString(R.string.teams));

        recyclerView = findViewById(R.id.teams_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        // hidden the keyboard
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
        }});


        Button addBtn = findViewById(R.id.addTeam);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTeam();
                closeKeyboard();
            }
        });

        teamsInGame = new ArrayList<>();
        allPossibleTeamsNames = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.teamsNames)));
        chooseRandomTeamName();
        chooseRandomTeamName();

        teamAdapter = new TeamAdapter(teamsInGame, TeamCardTypeEnum.NEW_TEAM_CARD);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.delete_slide);
        animation.setAnimationListener(this);
        teamAdapter.setListener(new TeamHolderFactory.NewTeamHolder.IAddTeamListener() {
            @Override
            public void onButtonRemoveClicked(int position, View view) {
                positionOfItem = position;
                View v = (View) view.getParent(); // getting the new_team_card layout
                v.startAnimation(animation);
            }

            @Override
            public void onButtonEditClicked(int adapterPosition, View view) {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        });

        recyclerView.setAdapter(teamAdapter);

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            Intent instructionIntent = new Intent(this, InstructionActivity.class);
            instructionIntent.putExtra(INSTRUCTION, getResources().getString(R.string.teams_setting_instructions));
            instructionIntent.putExtra(ACTION_BAR_TITLE, getSupportActionBar().getTitle());
            startActivity(instructionIntent);
        }
    }

    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewTeam() {
        chooseRandomTeamName();
        teamAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(teamsInGame.size() - 1); //scroll going to the end of recycler
    }

    private void chooseRandomTeamName() {
        Random random = new Random();
        int randomNumber;

        if (allPossibleTeamsNames.size() == 0) {
            String defaultTeamName = getResources().getString(R.string.team);
            Toast.makeText(this, getResources().getText(R.string.noMoreTeamsMessage).toString(), Toast.LENGTH_LONG).show();
            teamsInGame.add(defaultTeamName);
            return;
        }

        randomNumber = random.nextInt(allPossibleTeamsNames.size());
        teamsInGame.add(allPossibleTeamsNames.get(randomNumber));
        allPossibleTeamsNames.remove(randomNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
            if (teamsInGame.size() > 1) {
                savePreferences();
                Intent gameSettingsIntent = new Intent(TeamSettingsActivity.this, GameSettingsActivity.class);
                startActivity(gameSettingsIntent);
            } else {
                Toast.makeText(TeamSettingsActivity.this, getResources().getText(R.string.lessThanTwoTeamsMessage).toString(), Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //reading the final values of teams from textviews
        for(int i = 0; i < teamsInGame.size(); i++)
        {
            String title = ((TextView) recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.team_name)).getText().toString();
            teamsInGame.set(i, title);
        }

        SharedPreferencesOperations.saveParticipatingTeams(sharedPreferences, teamsInGame);
        for (String teamName : teamsInGame) {
            editor.putString(teamName + "Score", "0");
        }

        editor.apply();
    }

    @Override
    public void onAnimationStart(Animation animation) { }

    @Override
    public void onAnimationEnd(Animation animation) {
        allPossibleTeamsNames.add(teamsInGame.get(positionOfItem));
        teamsInGame.remove(positionOfItem);
        teamAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAnimationRepeat(Animation animation) { }

//    @Override
//    public void onBackPressed() {
//        Intent mainActivityIntent = new Intent(this, MainActivity.class);
//        startActivity(mainActivityIntent);
//    }
}
