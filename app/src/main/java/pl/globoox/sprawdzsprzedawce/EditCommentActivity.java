package pl.globoox.sprawdzsprzedawce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import pl.globoox.sprawdzsprzedawce.Utils.editComment;

public class EditCommentActivity extends AppCompatActivity {

    TextView textViewCharLeft;
    EditText editTextComment;
    int editTextCharLeftCount;
    int editTextCharacterCount;
    Button buttonCancelEditComment;
    Button buttonEditComment;
    String commentID;
    String comment;
    String fbID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);

        Intent i = getIntent();
        comment = i.getStringExtra("comment");
        commentID = i.getStringExtra("commentID");
        fbID = i.getStringExtra("fbID");

        editTextComment = findViewById(R.id.editTextNewComment);
        editTextComment.setText(comment);
        textViewCharLeft = findViewById(R.id.textViewCharLeft);
        buttonCancelEditComment = findViewById(R.id.buttonCancelAddNewComment);
        buttonEditComment = findViewById(R.id.buttonAddNewComment);

        buttonEditComment.setClickable(true);
        buttonEditComment.setText("ZAPISZ KOMENTARZ");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Edytuj komentarz...");
        editTextCharLeftCount = editTextComment.length();

        buttonCancelEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("fbID", fbID);
                startActivity(i);
                finish();
            }
        });

        // LEFT CHAR FUNCTION
        editTextComment.addTextChangedListener(new TextWatcher() {
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
        buttonEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (editTextCharacterCount < 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditCommentActivity.this);
                    builder.setMessage("Hej! Musisz wpisać minimum 10 znaków!").setNegativeButton(R.string.tryAgain, null).create().show();
                    return;
                }

                Response.Listener<String> responseListenerEditComment = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        buttonEditComment.setClickable(true);
                        buttonEditComment.setText("SPRAWDZAM...");

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int errorCode = jsonResponse.getInt("errorCode");

                            // CANT CONNECT TO DATABASE
                            if (errorCode == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditCommentActivity.this);
                                builder.setMessage(R.string.cantConnectToDatabase).setNegativeButton(R.string.tryAgain, null).create().show();
                            }

                            // EMPTY OFFER VARIABLE
                            else if (errorCode == 2) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditCommentActivity.this);
                                builder.setMessage("Niestety cos poszlo nie tak! Zmienna jest pusta").setNegativeButton(R.string.tryAgain, null).create().show();
                            }

                            // OPEN USER AREA ACTIVITY
                            else if (errorCode == 3) {
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra("fbID", fbID);
                                Toast.makeText(EditCommentActivity.this, "Komentarz został pomyślnie edytowany!", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        buttonEditComment.setClickable(true);
                        buttonEditComment.setText("ZAPISZ KOMENTARZ");

                    }
                };

                editComment editComment = new editComment(AccessToken.getCurrentAccessToken().getToken(), fbID, editTextComment.getText().toString(), commentID, responseListenerEditComment);
                RequestQueue queue = Volley.newRequestQueue(EditCommentActivity.this);
                queue.add(editComment);


            }
        });


    }
}
