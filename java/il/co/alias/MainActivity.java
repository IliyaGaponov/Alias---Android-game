package il.co.alias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static il.co.alias.ConstantsHolder.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button continueBtn;
    private LinearLayout languageIconsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        LocaleManager.loadLocale(this, sharedPreferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        ImageView englishLanguage = findViewById(R.id.english_image_view);
        ImageView hebrewLanguage = findViewById(R.id.israel_image_view);
        continueBtn = findViewById(R.id.continue_btn);
        Button newGameBtn = findViewById(R.id.new_game_btn);
        Button tutorialBtn = findViewById(R.id.tutorial_btn);
        languageIconsLinearLayout = findViewById(R.id.language_icons_layout);
        ImageView logoIv = findViewById(R.id.logo_iv);

        Animation alphaAndRotateAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        logoIv.startAnimation(alphaAndRotateAnim);

        englishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putLanguageToPreferences("en");
                setAppLanguage("en");

            }
        });

        hebrewLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putLanguageToPreferences("iw");
                setAppLanguage("iw");
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent continueGameIntent = new Intent(MainActivity.this, TeamsResultsActivity.class);
                startActivity(continueGameIntent);
            }
        });


        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean(IS_OLD_GAME_EXISTS, false)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getResources().getString(R.string.newGameQuestion))
                                .setMessage(getResources().getString(R.string.currentGame))
                                .setNegativeButton(getResources().getString(R.string.cancel), new CreateNewGameListener())
                                .setPositiveButton(getResources().getString(R.string.start), new CreateNewGameListener()).show();
                }
                else
                {
                    loadNextActivity();
                }
            }
        });


        tutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(tutorialIntent);
            }
        });


        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageIconsLinearLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setIconLanguage(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        languageIconsLinearLayout.setVisibility(View.VISIBLE);
        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfOldGameExists();
    }

    private void checkIfOldGameExists() {
        if(sharedPreferences.getBoolean(IS_OLD_GAME_EXISTS, false))
        {
            continueBtn.setVisibility(View.VISIBLE);
            continueBtn.setBackground(getResources().getDrawable(R.drawable.main_button_background));
        }
        else
        {
            continueBtn.setVisibility(View.INVISIBLE);
            // lift buttons up if continue button invisible
            LinearLayout buttonsLayout = findViewById(R.id.buttons_layout);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonsLayout.getLayoutParams();
            layoutParams.setMargins(0, 0, 0,140);
            buttonsLayout.setLayoutParams(layoutParams);
        }
    }

    private void setIconLanguage(Menu menu)
    {
        String languageString = sharedPreferences.getString(APP_LANGUAGE, "en");
        switch (languageString)
        {
            case "en":
                menu.findItem(R.id.lanquage_icon).setIcon(R.drawable.english_icon);
                break;
            case "iw":
                menu.findItem(R.id.lanquage_icon).setIcon(R.drawable.israel_icon);
                break;
        }
    }

    private void putLanguageToPreferences(String language)
    {
        editor.putString(APP_LANGUAGE, language).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocaleManager.saveLocale(sharedPreferences, sharedPreferences.getString(APP_LANGUAGE, "en"));
    }

    private void setAppLanguage(String languageId)
    {
        LocaleManager.changeLanguage(MainActivity.this, languageId);
        recreate();
    }

    private void loadNextActivity()
    {
        Intent newGameIntent = new Intent(MainActivity.this, TeamSettingsActivity.class);
        startActivity(newGameIntent);
    }

    private class CreateNewGameListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == DialogInterface.BUTTON_POSITIVE)
            {
                SharedPreferencesOperations.clearPreferences(sharedPreferences);
                loadNextActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
