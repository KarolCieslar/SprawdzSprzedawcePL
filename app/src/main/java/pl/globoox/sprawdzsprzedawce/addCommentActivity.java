package pl.globoox.sprawdzsprzedawce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import pl.globoox.sprawdzsprzedawce.Utils.addComment;

public class addCommentActivity extends AppCompatActivity {

    TextView textViewCharLeft;
    EditText editTextNewComment;
    int editTextCharLeftCount;
    int editTextCharacterCount;
    Button buttonCancelAddNewComment;
    Button buttonAddNewComment;
    RadioButton pickOnePositive;
    RadioButton pickOneNeutral;
    RadioButton pickOneNegative;
    String isBank;
    String userID;
    String status = "null";
    String fbNAME;
    String fbID;
    String firstName;
    String accessToken;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);


        Intent i = getIntent();
        isBank = i.getStringExtra("isBank");
        userID = i.getStringExtra("userID");
        accessToken = i.getStringExtra("accessToken");

        editTextNewComment = findViewById(R.id.editTextNewComment);
        textViewCharLeft = findViewById(R.id.textViewCharLeft);
        buttonCancelAddNewComment = findViewById(R.id.buttonCancelAddNewComment);
        buttonAddNewComment = findViewById(R.id.buttonAddNewComment);
        pickOnePositive = findViewById(R.id.pickOnePositive);
        pickOneNeutral = findViewById(R.id.pickOneNeutral);
        pickOneNegative = findViewById(R.id.pickOneNegative);

        buttonAddNewComment.setClickable(true);
        buttonAddNewComment.setText("DODAJ KOMENTARZ");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Dodaj własny komentarz...");
        editTextCharLeftCount = editTextNewComment.length();

        pickOnePositive.getBackground().setAlpha(50);
        pickOneNeutral.getBackground().setAlpha(50);
        pickOneNegative.getBackground().setAlpha(50);


        //ADS
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // GET INFO FB
        GraphRequest graphRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        fbNAME = me.optString("name");
                        fbID = me.optString("id");
                        String[] fbImieINazwisko = fbNAME.split(" ");
                        firstName = fbImieINazwisko[0];
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();


        // LEFT CHAR FUNCTION
        editTextNewComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewCharLeft.setText("Pozostała ilość znaków: " + (1000 - charSequence.length()));
                editTextCharacterCount = charSequence.length();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // ADD COMMENT BUTTON CLICK
        buttonAddNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (editTextCharacterCount < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                    builder.setMessage("Hej! Musisz wpisać minimum 10 znaków!").setNegativeButton(R.string.tryAgain, null).create().show();
                    return;
                }

                if (status.equalsIgnoreCase("null")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                    builder.setMessage("Hej! Musisz wybrać opinię!").setNegativeButton(R.string.tryAgain, null).create().show();
                    return;
                }


                Response.Listener<String> responseListenerAddUser = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        buttonAddNewComment.setClickable(true);
                        buttonAddNewComment.setText("SPRAWDZAM...");

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int errorCode = jsonResponse.getInt("errorCode");

                            // CANT CONNECT TO DATABASE
                            if (errorCode == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                                builder.setMessage(R.string.cantConnectToDatabase).setNegativeButton(R.string.tryAgain, null).create().show();
                            }

                            // EMPTY OFFER VARIABLE
                            else if (errorCode == 2) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                                builder.setMessage("Niestety cos poszlo nie tak! Zmienna jest pusta").setNegativeButton(R.string.tryAgain, null).create().show();
                            }


                            // COMMENT ALREDY ADDED
                            else if (errorCode == 3) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                                builder.setMessage("Przykro mi ale w systemie jest już Twój komentarz przypisane do tego sprzedawcy lub konta bankowego.").setNegativeButton("Rozumiem", null).create().show();
                            }

                            // OPEN USER AREA ACTIVITY
                            else if (errorCode == 4) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                                builder.setMessage("Dodajesz komentarz jako: " + firstName + ". Pamiętaj aby zamieszczać treści zgodne z naszym regulaminem gdyż w przeciwnym razie zostaną one usunięte.").setPositiveButton("ROZUMIEM!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getApplicationContext(), UserAreaActivity.class);
                                        intent.putExtra("isBank", isBank);
                                        intent.putExtra("userID", userID);
                                        finish();
                                        startActivity(intent);
                                    }
                                }).create().show();

                            }

                            // TO FAST ADD COMMENT
                            else if (errorCode == 5) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addCommentActivity.this);
                                builder.setMessage("Z Twojego konta został dodany niedawno komentarz... odczekaj troszkę zanim dodasz kolejny!").setNegativeButton("Rozumiem", null).create().show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        buttonAddNewComment.setClickable(true);
                        buttonAddNewComment.setText("DODAJ KOMENTARZ");

                    }
                };


                addComment addComment = new addComment(accessToken, userID, firstName, editTextNewComment.getText().toString(), status, fbID, responseListenerAddUser);
                RequestQueue queue = Volley.newRequestQueue(addCommentActivity.this);
                queue.add(addComment);


            }
        });


    }

    public void backToUserAreaActivity(View view) {
        finish();
        super.onBackPressed();
    }


    // PICKONE RADIO BUTTOn
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.pickOnePositive:
                if (checked) {
                    pickOnePositive.getBackground().setAlpha(255);
                    pickOneNeutral.getBackground().setAlpha(50);
                    pickOneNegative.getBackground().setAlpha(50);
                    status = "positive";
                    Log.d("asdsad", "1");
                    break;
                }
            case R.id.pickOneNeutral:
                if (checked) {
                    pickOnePositive.getBackground().setAlpha(50);
                    pickOneNeutral.getBackground().setAlpha(255);
                    pickOneNegative.getBackground().setAlpha(50);
                    status = "neutral";
                    Log.d("asdsad", "2");
                    break;
                }
            case R.id.pickOneNegative:
                if (checked) {
                    pickOnePositive.getBackground().setAlpha(50);
                    pickOneNeutral.getBackground().setAlpha(50);
                    pickOneNegative.getBackground().setAlpha(255);
                    status = "negative";
                    Log.d("asdsad", "3");
                    break;
                }
        }
    }

}
