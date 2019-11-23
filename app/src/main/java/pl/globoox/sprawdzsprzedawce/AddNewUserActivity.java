package pl.globoox.sprawdzsprzedawce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import pl.globoox.sprawdzsprzedawce.Utils.AddUser;

public class AddNewUserActivity extends AppCompatActivity {

    String isBank;
    String userID;
    String registerDate;
    String userName;
    Button buttonAddNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        isBank = i.getStringExtra("isBank");
        userID = i.getStringExtra("userID");
        registerDate = i.getStringExtra("registerDate");
        userName = i.getStringExtra("userName");

        if (isBank.equalsIgnoreCase("true")) {
            setContentView(R.layout.activity_addnewuser_bank);
            buttonAddNewUser = findViewById(R.id.buttonAddNewUserBANK);
        } else {
            setContentView(R.layout.activity_addnewuser_olx);
            buttonAddNewUser = findViewById(R.id.buttonAddNewUserOLX);
        }
        Button buttonCancelAddUser = findViewById(R.id.buttonCancelAddUser);

        buttonCancelAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(i);
            }
        });



        buttonAddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // ADD NEW USER
                Response.Listener<String> responseListenerAddUser = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int errorCode = jsonResponse.getInt("errorCode");
                            Log.d("TAG", String.valueOf(errorCode));

                            // CANT CONNECT TO DATABASE
                            if (errorCode == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewUserActivity.this);
                                builder.setMessage(R.string.cantConnectToDatabase).setNegativeButton(R.string.tryAgain, null).create().show();
                            }

                            // OPEN USER AREA ACTIVITY
                            else if (errorCode == 2) {
                                Intent i = new Intent(getApplicationContext(), UserAreaActivity.class);
                                i.putExtra("isBank", isBank);
                                i.putExtra("userID", userID);
                                finish();
                                startActivity(i);
                            }

                            // EMPTY OFFER VARIABLE
                            else if (errorCode == 3) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewUserActivity.this);
                                builder.setMessage("Niestety cos poszlo nie tak! Zmienna jest pusta").setNegativeButton(R.string.tryAgain, null).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };


                AddUser addUser = new AddUser(isBank, userID, registerDate, userName, responseListenerAddUser);
                RequestQueue queue = Volley.newRequestQueue(AddNewUserActivity.this);
                queue.add(addUser);


            }
        });



    }
}
