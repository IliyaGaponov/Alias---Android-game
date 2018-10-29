package il.co.alias;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static il.co.alias.ConstantsHolder.*;

/**
 * Created by igapo on 01.10.2018.
 */

public class SharedPreferencesOperations
{
    public static void clearPreferences(SharedPreferences preferences)
    {
        SharedPreferences.Editor editor = preferences.edit();
        Map<String,?> prefs = preferences.getAll();
        for(Map.Entry<String,?> prefToReset : prefs.entrySet()){
            if(!prefToReset.getKey().equals(APP_LANGUAGE) && !prefToReset.getKey().equals(IS_FIRST_APP_USING))
                editor.remove(prefToReset.getKey()).commit();
        }
    }

    public static List<String> loadTeams(SharedPreferences preferences)
    {
        List<String> teams;
        String jsonTeams = preferences.getString(PARTICIPATING_TEAMS, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        teams = gson.fromJson(jsonTeams, type);

        return teams;
    }

    public static void saveParticipatingTeams(SharedPreferences preferences, List<String> teamsInGame)
    {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonTeams = gson.toJson(teamsInGame);
        editor.putString(PARTICIPATING_TEAMS, jsonTeams);
        editor.putString(CURRENT_TEAM_NAME, teamsInGame.get(0));
        editor.putInt(NUM_OF_TEAMS, teamsInGame.size());
        editor.apply();
    }

    public static List<String> loadScores(SharedPreferences preferences, List<String> teamsInGame)
    {
        List<String> scoresList = new ArrayList<>();
        for(String team : teamsInGame)
        {
            String score = preferences.getString(team + "Score", "0");
            scoresList.add(score);
            if(Integer.parseInt(score) >= preferences.getInt(NUM_OF_WORDS_IN_GAME, 200))
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(END_OF_GAME, true).apply();
            }
        }

        return scoresList;
    }
}
