package pl.globoox.sprawdzsprzedawce;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class PolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_policy);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Polityka Prywatności");

    }

}
