package pl.globoox.sprawdzsprzedawce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import pl.globoox.sprawdzsprzedawce.Utils.sendMail;


public class ContactActivity extends AppCompatActivity {

    boolean isEmpty = false;
    EditText editTextNick;
    EditText editTextMail;
    EditText editTextMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Kontakt z nami");

        editTextNick = findViewById(R.id.editTextNick);
        editTextMail = findViewById(R.id.editTextMail);
        editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSendMail);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isEmpty = false;
                if (editTextNick.getText().toString().matches("")) {
                    isEmpty = true;
                }
                if (editTextMail.getText().toString().matches("")) {
                    isEmpty = true;
                }
                if (editTextMessage.getText().toString().matches("")) {
                    isEmpty = true;
                }

                Log.d("TAG", String.valueOf(isEmpty));
                if (isEmpty == true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                    builder.setMessage("Musisz uzupełnić wszystkie pola w formularzu!").setNegativeButton(R.string.tryAgain, null).create().show();
                } else {

                    // LAST COMMENTS SHOW
                    Response.Listener<String> responseListenerSendMail = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int errorCode = jsonResponse.getInt("errorCode");

                                // EMPTY VARIABLE
                                if (errorCode == 1) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                                    builder.setMessage("Wystąpił błąd servera...").setNegativeButton(R.string.tryAgain, null).create().show();
                                }

                                // SENDED
                                else if (errorCode == 2) {
                                    Toast.makeText(ContactActivity.this, "Sukces! Wiadomość została wysłana!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }

                                // ERROR
                                else if (errorCode == 3) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                                    builder.setMessage("Wystąpił błąd podczas wysyłania wiadomości...").setNegativeButton(R.string.tryAgain, null).create().show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };



                    sendMail sendMail = new sendMail(editTextNick.getText().toString(), editTextMail.getText().toString(), editTextMessage.getText().toString(), responseListenerSendMail);
                    RequestQueue queue = Volley.newRequestQueue(ContactActivity.this);
                    queue.add(sendMail);

                }
            }
        });



    }

}
