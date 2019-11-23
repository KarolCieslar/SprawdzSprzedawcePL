package pl.globoox.sprawdzsprzedawce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import pl.globoox.sprawdzsprzedawce.Utils.CustomDialogClass;
import pl.globoox.sprawdzsprzedawce.Utils.UserComments;
import pl.globoox.sprawdzsprzedawce.Utils.deleteComment;
import pl.globoox.sprawdzsprzedawce.Utils.reportComment;

public class UserAreaActivity extends AppCompatActivity {

    TextView textViewUserName;
    GifImageView loadingImage;
    GifImageView loadingImageUserInfo;
    RelativeLayout userInfoSpace;
    TextView textViewRegisterDate;
    TextView textViewPositiveCount;
    TextView textViewNeutralCount;
    TextView textViewNegativeCount;
    TextView textViewNoComments;
    Button buttonCheckUserOffer;
    Button buttonClaimAccount;
    String isBank;
    String userID;
    String fbID;
    boolean isLoggedIn;
    private AdView mAdView;

    ListView userComments;
    ArrayList<String> userComments_userList = new ArrayList();
    ArrayList<String> userComments_olxUserList = new ArrayList();
    ArrayList<String> userComments_dateList = new ArrayList();
    ArrayList<String> userComments_messageList = new ArrayList();
    ArrayList<String> userComments_statusList = new ArrayList();
    ArrayList<String> userComments_commentIDList = new ArrayList();
    ArrayList<String> userComments_senderID = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        isBank = i.getStringExtra("isBank");
        userID = i.getStringExtra("userID");

        if (isBank.equalsIgnoreCase("true")) {
            setContentView(R.layout.activity_user_area_bank);
        } else {
            setContentView(R.layout.activity_user_area_olx);
        }

        // LOADING GIF
        loadingImage = findViewById(R.id.loadingImage);
        loadingImageUserInfo = findViewById(R.id.loadingImageUserInfo);
        userInfoSpace = findViewById(R.id.userInfoSpace);
        loadingImage.setVisibility(View.VISIBLE);
        loadingImageUserInfo.setVisibility(View.VISIBLE);
        userInfoSpace.setVisibility(View.INVISIBLE);


        //ADS
        MobileAds.initialize(this, "ca-app-pub-3713322008461460/4589393881");


        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Opinie użytkownika");

        userComments = (ListView) findViewById(R.id.listViewUserComments);

        textViewNoComments = (TextView) findViewById(R.id.textViewNoComments);
        textViewUserName = (TextView) findViewById(R.id.textViewName);
        textViewPositiveCount = (TextView) findViewById(R.id.textViewPositiveCount);
        textViewNeutralCount = (TextView) findViewById(R.id.textViewNeutralCount);
        textViewNegativeCount = (TextView) findViewById(R.id.textViewNegativeCount);

        textViewNoComments.setVisibility(View.INVISIBLE);

        // GET INFO FB

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn == true) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            fbID = me.optString("id");
                        }

                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }


        // LAST COMMENTS SHOW
        Response.Listener<String> responseListenerUserComments = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
//                    int errorCode = jsonResponse.getInt("errorCode");
                    int count = jsonResponse.getInt("count");

                    if (count > 0) {
                        JSONArray commentsArray = jsonResponse.getJSONArray("comments");
                        for (int i = 0; i < count; i++) {
                            if (i == 3 || i == 7 || i == 11 || i == 15 || i == 19) {
                                userComments_userList.add("admob");
                                userComments_olxUserList.add("admob");
                                userComments_dateList.add("admob");
                                userComments_messageList.add("admob");
                                userComments_statusList.add("admob");
                                userComments_senderID.add("admob");
                                userComments_commentIDList.add("admob");
                            } else {
                                JSONObject jsonObject = commentsArray.getJSONObject(i);
                                userComments_userList.add(jsonObject.getString("user"));
                                userComments_olxUserList.add(jsonObject.getString("olxUser"));
                                userComments_dateList.add(jsonObject.getString("date"));
                                userComments_messageList.add(jsonObject.getString("message"));
                                userComments_statusList.add(jsonObject.getString("status"));
                                userComments_senderID.add(jsonObject.getString("senderID"));
                                userComments_commentIDList.add(jsonObject.getString("commentID"));
                            }
                        }

                        UserAreaActivity.CustomAdapter customAdapter = new CustomAdapter();
                        userComments.setAdapter(customAdapter);


                    } else {
                        textViewNoComments.setVisibility(View.VISIBLE);
                    }

                    // SET INFORMATION TO TEXVIEW

                    String registerDate = jsonResponse.getString("registerDate");
                    textViewRegisterDate = (TextView) findViewById(R.id.textViewSince);
                    if (registerDate.equalsIgnoreCase("BANK")) {
                        textViewRegisterDate.setText("Konto bankowe...");
                        textViewUserName.setText("ID konta: " + userID);
                    } else {
                        textViewRegisterDate.setText(registerDate);
                        textViewUserName.setText(jsonResponse.getString("userName"));
                    }
                    textViewPositiveCount.setText(jsonResponse.getString("positive"));
                    textViewNeutralCount.setText(jsonResponse.getString("neutral"));
                    textViewNegativeCount.setText(jsonResponse.getString("negative"));


                    // LOADING
                    loadingImage.setVisibility(View.INVISIBLE);
                    loadingImageUserInfo.setVisibility(View.INVISIBLE);
                    userInfoSpace.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        UserComments userComments = new UserComments(userID, responseListenerUserComments);
        RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
        queue.add(userComments);


        if (!isBank.equalsIgnoreCase("true")) {
            buttonCheckUserOffer = findViewById(R.id.buttonCheckOffer);
            buttonCheckUserOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.olx.pl/oferty/uzytkownik/" + userID));
                    startActivity(browserIntent);
                }
            });


            buttonClaimAccount = findViewById(R.id.buttonClaimAccount);
            buttonClaimAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(UserAreaActivity.this, "Ta funkcja wkrótce zostanie dodana!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // FLOATING ACTION BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddComment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn == true) {
                    Intent i = new Intent(getApplicationContext(), addCommentActivity.class);
                    i.putExtra("isBank", isBank);
                    i.putExtra("userID", userID);
                    i.putExtra("accessToken", accessToken.getToken());
                    finish();
                    startActivity(i);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                    builder.setMessage("Aby dodać komentarz musisz być zalogowany. Prosimy o przejście na główne okno aplikacji i w menu wybrać opcję logowania FaceBook.").setNegativeButton("Okej, rozumiem!", null).create().show();
                }
            }
        });

    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userComments_dateList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position == 3 || position == 7 || position == 11 || position == 15 || position == 19) {
                convertView = getLayoutInflater().inflate(R.layout.customlayout_usercommentsadmob, null);
                mAdView = convertView.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {

                convertView = getLayoutInflater().inflate(R.layout.customlayout_usercomments, null);
                TextView textViewUser = (TextView) convertView.findViewById(R.id.textViewUser);
                TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
                TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
                textViewUser.setText(userComments_userList.get(position));
                textViewDate.setText(userComments_dateList.get(position));
                textViewMessage.setText(userComments_messageList.get(position));
                Drawable d = convertView.getBackground();
                if (userComments_statusList.get(position).equalsIgnoreCase("positive")) {
                    d.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                } else if (userComments_statusList.get(position).equalsIgnoreCase("neutral")) {
                    d.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                } else if (userComments_statusList.get(position).equalsIgnoreCase("negative")) {
                    d.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }


                Button reportButton = convertView.findViewById(R.id.buttonReportComment);
                Button editButton = convertView.findViewById(R.id.buttonEditComment);
                Button deleteButton = convertView.findViewById(R.id.buttonDeleteComment);
                reportButton.setClickable(true);
                editButton.setClickable(true);
                deleteButton.setClickable(true);
                reportButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);

                if (isLoggedIn == true) {

                    // -------------------- //
                    //  REPORT BUTTON CLICK //
                    // -------------------- //
                    reportButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final CustomDialogClass cdd = new CustomDialogClass(UserAreaActivity.this);
                            cdd.show();
                            final Button buttonSendReport = cdd.findViewById(R.id.buttonSendReport);
                            buttonSendReport.setClickable(true);
                            buttonSendReport.getBackground().setAlpha(50);

                            final EditText editTextReportMessage = cdd.findViewById(R.id.editTextReportMessage);
                            editTextReportMessage.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if (charSequence.length() > 10) {
                                        buttonSendReport.setClickable(true);
                                        buttonSendReport.getBackground().setAlpha(255);
                                    } else {
                                        buttonSendReport.setClickable(false);
                                        buttonSendReport.getBackground().setAlpha(50);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                }
                            });


                            // CLICKED SEND REPORT
                            buttonSendReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Response.Listener<String> responseListenerAddUser = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                int errorCode = jsonResponse.getInt("errorCode");

                                                // CANT CONNECT TO DATABASE
                                                if (errorCode == 1 || errorCode == 3) {
                                                    Toast.makeText(UserAreaActivity.this, "Wystąpił błąd...", Toast.LENGTH_LONG).show();


                                                } else if (errorCode == 2) {
                                                    Toast.makeText(UserAreaActivity.this, "Zgłoszenie zostało wysłane! Dziękujemy.", Toast.LENGTH_LONG).show();
                                                    cdd.dismiss();


                                                } else if (errorCode == 4) {
                                                    Toast.makeText(UserAreaActivity.this, "Już zgłosiłeś nam ten komentarz.", Toast.LENGTH_LONG).show();
                                                    cdd.dismiss();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    reportComment reportComment = new reportComment(AccessToken.getCurrentAccessToken().getToken(), userComments_commentIDList.get(position), editTextReportMessage.getText().toString(), fbID, responseListenerAddUser);
                                    RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                                    queue.add(reportComment);
                                }
                            });


                            // CLICKED CANCEL REPORT
                            Button buttonCancelReport = cdd.findViewById(R.id.buttonCancelReport);
                            buttonCancelReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cdd.dismiss();
                                }
                            });
                        }
                    });


                    // -------------------- //
                    //  DELETE BUTTON CLICK //
                    // -------------------- //
                    if (userComments_senderID.get(position).equalsIgnoreCase(fbID)) {
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Response.Listener<String> responseListenerAddUser = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            int errorCode = jsonResponse.getInt("errorCode");

                                            // CANT CONNECT TO DATABASE
                                            if (errorCode == 1 || errorCode == 3) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                                builder.setMessage("Wystąpił błąd...").setNegativeButton("Spróbuj ponownie...", null).create().show();

                                            } else if (errorCode == 2) {
                                                finish();
                                                startActivity(getIntent());
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                deleteComment deleteComment = new deleteComment(AccessToken.getCurrentAccessToken().getToken(), userComments_commentIDList.get(position), responseListenerAddUser);
                                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                                queue.add(deleteComment);
                            }
                        });
                    } else {
                        deleteButton.setVisibility(View.INVISIBLE);
                        deleteButton.setClickable(false);
                    }


                    // -------------------- //
                    //   EDIT BUTTON CLICK  //
                    // -------------------- //
                    if (userComments_senderID.get(position).equalsIgnoreCase(fbID)) {
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getApplicationContext(), EditCommentActivity.class);
                                i.putExtra("commentID", userComments_commentIDList.get(position));
                                i.putExtra("comment", userComments_messageList.get(position));
                                i.putExtra("fbID", fbID);
                                finish();
                                startActivity(i);
                            }
                        });

                    } else {
                        editButton.setVisibility(View.INVISIBLE);
                        editButton.setClickable(false);
                    }


                } else {
                    reportButton.setVisibility(View.INVISIBLE);
                    editButton.setVisibility(View.INVISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                    reportButton.setClickable(false);
                    editButton.setClickable(false);
                    deleteButton.setClickable(false);
                }
            }

            return convertView;
        }

    }
}
