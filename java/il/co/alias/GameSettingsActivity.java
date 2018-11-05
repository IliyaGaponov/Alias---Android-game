package il.co.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import static il.co.alias.ConstantsHolder.*;

/**
 * Created by igapo on 14.09.2018.
 */

public class GameSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView numberOfWordsTv;
    private TextView roundTimeTv;
    private CheckBox commonWordCheckBox;
    private  SharedPreferences sharedPreferences;
    private SeekBar numberOfWordsSeekBar;
    private SeekBar roundTimeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        //changing the direction of window according to the lanquage of app
        LayoutDirection.setLayoutDirection(sharedPreferences.getString(APP_LANGUAGE,""), getWindow());
        setTitle(getResources().getString(R.string.settings));

        numberOfWordsSeekBar = findViewById(R.id.seekBarNumOfWords);
        roundTimeSeekBar = findViewById(R.id.seekBarRoundTime);
        numberOfWordsTv = findViewById(R.id.intNumOfWords);
        roundTimeTv = findViewById(R.id.intRoundTime);
        commonWordCheckBox = findViewById(R.id.commonWordCheckBox);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            numberOfWordsSeekBar.setMin(10);
            roundTimeSeekBar.setMin(5);
        }
        numberOfWordsSeekBar.incrementProgressBy(5);
        numberOfWordsSeekBar.setOnSeekBarChangeListener(this);

        roundTimeSeekBar.incrementProgressBy(5);
        roundTimeSeekBar.setOnSeekBarChangeListener(this);

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            Intent instructionIntent = new Intent(this, InstructionActivity.class);
            instructionIntent.putExtra(INSTRUCTION, getResources().getString(R.string.game_setting_instructions));
            instructionIntent.putExtra(ACTION_BAR_TITLE, getSupportActionBar().getTitle());
            startActivity(instructionIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        savePreferences();
        Intent dictionaryIntent = new Intent(GameSettingsActivity.this, DictionariesActivity.class);
        startActivity(dictionaryIntent);
        return super.onOptionsItemSelected(item);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROUND_TIME, roundTimeTv.getText().toString());
        editor.putInt(NUM_OF_WORDS_IN_GAME, Integer.valueOf(numberOfWordsTv.getText().toString()));
        editor.putBoolean(IS_COMMON_WORD, commonWordCheckBox.isChecked());
        editor.commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = progress / 5;
        progress = progress * 5;
        if(seekBar.getId() == R.id.seekBarNumOfWords)
        {
            numberOfWordsTv.setText(String.valueOf(progress));
        }
        else if(seekBar.getId() == R.id.seekBarRoundTime)
        {
            roundTimeTv.setText(String.valueOf(progress));
        }
        // set minimum limit to seek bar
        int min = 10;
        if(progress < min) {
            seekBar.setProgress(min);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
