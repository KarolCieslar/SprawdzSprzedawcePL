package pl.globoox.sprawdzsprzedawce;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rules);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Regulamin");

    }

}
