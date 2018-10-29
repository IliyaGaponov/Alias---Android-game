package il.co.alias;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import static il.co.alias.ConstantsHolder.*;

/**
 * Created by igapo on 26.10.2018.
 */

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intructions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Button okBtn = findViewById(R.id.ok_btn);
        Button cancelInstructBtn = findViewById(R.id.cancel_instruct_btn);
        TextView instructionsTv = findViewById(R.id.instructions_tv);

        instructionsTv.setText(getIntent().getStringExtra(INSTRUCTION));
        setTitle(getIntent().getStringExtra(ACTION_BAR_TITLE));

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelInstructBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(GAME_SETTINGS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_FIRST_APP_USING, false).apply();
                finish();
            }
        });
    }
}
