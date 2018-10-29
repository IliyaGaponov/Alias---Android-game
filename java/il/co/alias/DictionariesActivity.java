package il.co.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import static il.co.alias.ConstantsHolder.ACTION_BAR_TITLE;
import static il.co.alias.ConstantsHolder.APP_LANGUAGE;
import static il.co.alias.ConstantsHolder.DICTIONARY;
import static il.co.alias.ConstantsHolder.GAME_SETTINGS;
import static il.co.alias.ConstantsHolder.INSTRUCTION;
import static il.co.alias.ConstantsHolder.IS_FIRST_APP_USING;
import static il.co.alias.ConstantsHolder.IS_OLD_GAME_EXISTS;

/**
 * Created by igapo on 26.10.2018.
 */

public class DictionariesActivity extends AppCompatActivity{
    private SharedPreferences sharedPreferences;
    private Button enBtn;
    private Button heBtn;
    private RecyclerView recyclerView;
    private DictionaryAdapter dictionaryAdapter;
    private List<Dictionary> enDictionaryList;
    private List<Dictionary> heDictionaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionaries);

        sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
        String appLanguage = sharedPreferences.getString(APP_LANGUAGE, "en");
        LayoutDirection.setLayoutDirection(appLanguage, getWindow());//changing the direction of window according to the language of app
        setTitle(getResources().getString(R.string.difficulty));

        enBtn = findViewById(R.id.btn_en);
        heBtn = findViewById(R.id.btn_he);
        recyclerView = findViewById(R.id.dictionaries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enDictionaryList = new ArrayList<>();
        heDictionaryList = new ArrayList<>();

        createDictionaries();
        dictionaryAdapter = new DictionaryAdapter();
        showDictionaries(appLanguage);

        dictionaryAdapter.setOnItemClickListener(new DictionaryAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(DICTIONARY, dictionaryAdapter.getDictionariesList().get(position).getName());
                editor.putBoolean(IS_OLD_GAME_EXISTS, true);
                editor.commit();

                Intent teamsResultsIntent = new Intent(DictionariesActivity.this, GameActivity.class);
                startActivity(teamsResultsIntent);
            }
        });

        enBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDictionaries("en");
                setBtnBackground(enBtn, heBtn);
            }
        });

        heBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDictionaries("iw");
                setBtnBackground(heBtn, enBtn);
            }
        });

        if(sharedPreferences.getBoolean(IS_FIRST_APP_USING, true))
        {
            Intent instructionIntent = new Intent(this, InstructionActivity.class);
            instructionIntent.putExtra(INSTRUCTION, getResources().getString(R.string.dictionary_setting_instructions));
            instructionIntent.putExtra(ACTION_BAR_TITLE, getSupportActionBar().getTitle());
            startActivity(instructionIntent);
        }
    }

    private void setBtnBackground(Button clickedBtn, Button secondBtn) {

        clickedBtn.setBackground(getResources().getDrawable(R.drawable.dict_btn_on_press_background));
        clickedBtn.setTextColor(Color.WHITE);
        secondBtn.setTextColor(getResources().getColorStateList(R.color.white));
        secondBtn.setBackground(getResources().getDrawable(R.drawable.transparent_background));
    }

    private void createDictionaries() {
        createDictionariesEn();
        createDictionariesHe();
    }

    private void showDictionaries(String language)
    {
        if(language.equals("en"))
        {
            dictionaryAdapter.setDictionariesList(enDictionaryList);
            setBtnBackground(enBtn, heBtn);
        }
        else if(language.equals("iw"))
        {
            dictionaryAdapter.setDictionariesList(heDictionaryList);
            setBtnBackground(heBtn, enBtn);
        }

        recyclerView.setAdapter(dictionaryAdapter);
    }

    private void createDictionariesEn() {
        String[] easyDictTexts = getResources().getStringArray(R.array.easyDictEnName);
        String[] easyWords = getResources().getStringArray(R.array.easyDictEn);
        String easyDictName = getResources().getResourceEntryName(R.array.easyDictEn);
        String[] hardDictTexts = getResources().getStringArray(R.array.hardDictEnName);
        String[] hardWords = getResources().getStringArray(R.array.hardDictEn);
        String hardDictName = getResources().getResourceEntryName(R.array.hardDictEn);

        enDictionaryList.add(new Dictionary(easyDictTexts[0], easyDictTexts[1], easyDictTexts[2], Integer.toString(easyWords.length), easyDictName));
        enDictionaryList.add(new Dictionary(hardDictTexts[0], hardDictTexts[1], hardDictTexts[2], Integer.toString(hardWords.length), hardDictName));
    }

    private void createDictionariesHe() {
        String[] easyDictTexts = getResources().getStringArray(R.array.easyDictHeName);
        String[] easyWords = getResources().getStringArray(R.array.easyDictHe);
        String easyDictName = getResources().getResourceEntryName(R.array.easyDictHe);
        String[] hardDictTexts = getResources().getStringArray(R.array.hardDictHeName);
        String[] hardWords = getResources().getStringArray(R.array.hardDictHe);
        String hardDictName = getResources().getResourceEntryName(R.array.hardDictHe);

        heDictionaryList.add(new Dictionary(easyDictTexts[0], easyDictTexts[1], easyDictTexts[2], Integer.toString(easyWords.length), easyDictName));
        heDictionaryList.add(new Dictionary(hardDictTexts[0], hardDictTexts[1], hardDictTexts[2], Integer.toString(hardWords.length), hardDictName));
    }
}
