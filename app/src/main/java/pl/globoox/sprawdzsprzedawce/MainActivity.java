package pl.globoox.sprawdzsprzedawce;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import pl.globoox.sprawdzsprzedawce.Utils.LastCommentHome;
import pl.globoox.sprawdzsprzedawce.Utils.QueryCheck;

import static pl.globoox.sprawdzsprzedawce.R.id.buttonCheckButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ListView lastComments;
    LoginButton loginButton;
    private AccessTokenTracker fbTracker;
    GifImageView loadingImage;
    CallbackManager callbackManager;
    ArrayList<String> comments_userList = new ArrayList();
    ArrayList<String> comments_olxUserList = new ArrayList();
    ArrayList<String> comments_dateList = new ArrayList();
    ArrayList<String> comments_messageList = new ArrayList();
    ArrayList<String> comments_statusList = new ArrayList();

    ImageView imageViewFBUser;
    TextView textViewFBName;
    TextView textViewFBEmail;
    Button buttonCheck;

    String fbEMAIL;
    String fbNAME;
    String fbPICTURE;
    String fbID;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeControls();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loginWithFb();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // LOADING GIF
        loadingImage = findViewById(R.id.loadingImage);

        //ADS
        MobileAds.initialize(this, "ca-app-pub-3713322008461460/4589393881");


        // MENU DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        textViewFBName = headerView.findViewById(R.id.textViewFBName);
        textViewFBEmail = headerView.findViewById(R.id.textViewFBEmail);
        //imageViewFBUser = headerView.findViewById(R.id.imageViewFBUser);

        //imageViewFBUser.setVisibility(View.INVISIBLE);
        textViewFBName.setText("");
        textViewFBEmail.setText("");

        // LOGIN
        GraphRequest graphRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            fbEMAIL = me.optString("email");
                            fbNAME = me.optString("name");
                            fbID = me.optString("id");
                            //imageViewFBUser.setVisibility(View.VISIBLE);
                            textViewFBName.setText(fbNAME);
                            textViewFBEmail.setText(fbEMAIL);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();


        // MAIN CONTENT APP
        final SearchView editTextOfferLink = (SearchView) findViewById(R.id.editTextOfferLink);
        buttonCheck = (Button) findViewById(buttonCheckButton);
        Button buttonPaste = (Button) findViewById(R.id.buttonPaste);
        lastComments = (ListView) findViewById(R.id.listViewLastComments);


        buttonPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToPaste = null;

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard.hasPrimaryClip()) {
                    ClipData clip = clipboard.getPrimaryClip();
                    textToPaste = clip.getItemAt(0).coerceToText(getApplicationContext()).toString();
                }

                if (!TextUtils.isEmpty(textToPaste)) {
                    editTextOfferLink.clearFocus();
                    editTextOfferLink.setQuery(textToPaste, true);
                    editTextOfferLink.clearFocus();
                    editTextOfferLink.requestFocus();
                }
            }
        });


        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String offerLink = editTextOfferLink.getQuery().toString();
                boolean error = false;

                // CHECK FORM EMPTY
                if (offerLink.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.formEmpty).setNegativeButton(R.string.tryAgain, null).create().show();
                    error = true;
                    return;
                }

                if (error == false) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int errorCode = jsonResponse.getInt("errorCode");
                                String userID = jsonResponse.getString("userID");

                                // CANT CONNECT TO DATABASE
                                if (errorCode == 1) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage(R.string.cantConnectToDatabase).setNegativeButton(R.string.tryAgain, null).create().show();
                                }

                                // USER NOT FOUND
                                else if (errorCode == 2) {
                                    Boolean isBank = jsonResponse.getBoolean("isBank");
                                    Intent i;
                                    i = new Intent(getApplicationContext(), AddNewUserActivity.class);
                                    if (isBank == true) {
                                        i.putExtra("isBank", "true");
                                    } else {
                                        i.putExtra("isBank", "false");
                                    }
                                    String userName = jsonResponse.getString("userName");
                                    String registerDate = jsonResponse.getString("registerDate");
                                    i.putExtra("userID", userID);
                                    i.putExtra("userName", userName);
                                    i.putExtra("registerDate", registerDate);
                                    startActivity(i);
                                }

                                // EMPTY OFFER VARIABLE
                                else if (errorCode == 3) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage(R.string.omptyOfferVariable).setNegativeButton(R.string.tryAgain, null).create().show();
                                }

                                // BAD LINK
                                else if (errorCode == 4) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage(R.string.badOfferLink).setNegativeButton(R.string.tryAgain, null).create().show();
                                }

                                // USER FOUND!
                                else if (errorCode == 5) {
                                    Boolean isBank = jsonResponse.getBoolean("isBank");
                                    Intent i;
                                    i = new Intent(getApplicationContext(), UserAreaActivity.class);
                                    i.putExtra("userID", userID);
                                    i.putExtra("isBank", isBank.toString());
                                    startActivity(i);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            buttonCheck.setText("WYSZUKAJ KOMENTARZE");
                            buttonCheck.setClickable(true);
                        }

                    };

                    QueryCheck offerQueryRequest = new QueryCheck(offerLink, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(offerQueryRequest);
                    buttonCheck.setText("Przeszukuję bazę...");
                    buttonCheck.setClickable(false);
                }

            }
        });

        // LAST COMMENTS SHOW
        Response.Listener<String> responseListenerLastComments = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int errorCode = jsonResponse.getInt("errorCode");
                    int count = jsonResponse.getInt("count");
                    Log.d("Tag", String.valueOf(errorCode));

                    JSONArray commentsArray = jsonResponse.getJSONArray("comments");
                    Log.d("TAG", String.valueOf(commentsArray));

                    for (int i = 0; i < count; i++) {
                        if (i == 3 || i == 7 || i == 11){
                            comments_userList.add("admobadd");
                            comments_olxUserList.add("admobadd");
                            comments_dateList.add("admobadd");
                            comments_messageList.add("admobadd");
                            comments_statusList.add("admobadd");
                        } else {
                            JSONObject jsonObject = commentsArray.getJSONObject(i);
                            comments_userList.add(jsonObject.getString("user"));
                            comments_olxUserList.add(jsonObject.getString("olxUser"));
                            comments_dateList.add(jsonObject.getString("date"));
                            comments_messageList.add(jsonObject.getString("message"));
                            comments_statusList.add(jsonObject.getString("status"));
                        }
                    }

                    MainActivity.CustomAdapter customAdapter = new MainActivity.CustomAdapter();
                    customAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                        @Override
                        public void onListItemClicked(View view, int position) {
                        }
                    });
                    lastComments.setAdapter(customAdapter);
                    loadingImage.setVisibility(View.INVISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        LastCommentHome lastCommentHome = new LastCommentHome("13", responseListenerLastComments);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(lastCommentHome);


    }


    private void loginWithFb() {
        // LOGIN MANAGER FACEBOOK
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    fbEMAIL = me.optString("email");
                                    fbNAME = me.optString("name");
                                    //fbPICTURE = me.getJSONObject("picture").getJSONObject("data").getJSONObject("url");
                                    fbID = me.optString("id");
                                    //imageViewFBUser.setVisibility(View.VISIBLE);
                                    //imageViewFBUser.setImageURI(Uri.parse(fbPICTURE));
                                    textViewFBName.setText(fbNAME);
                                    textViewFBEmail.setText(fbEMAIL);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture{url}");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                textViewFBName.setText("Logowanie zostało anulowane!");
            }

            @Override
            public void onError(FacebookException error) {
                textViewFBName.setText("Błąd logowania: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    // FACEBOOK INITALZIZE LOGIN
    private void initializeControls() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent i;
            i = new Intent(getApplicationContext(), ProfileActivity.class);
            i.putExtra("fbID", fbID);
            startActivity(i);

        } else if (id == R.id.nav_best) {
            Intent i;
            i = new Intent(getApplicationContext(), RankActivity.class);
            i.putExtra("type", "best");
            startActivity(i);

        } else if (id == R.id.nav_worst) {
            Intent i;
            i = new Intent(getApplicationContext(), RankActivity.class);
            i.putExtra("type", "worst");
            startActivity(i);

        } else if (id == R.id.nav_comments) {
            Intent i;
            i = new Intent(getApplicationContext(), RankActivity.class);
            i.putExtra("type", "comment");
            startActivity(i);

        } else if (id == R.id.nav_policy) {
            Intent i = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_rules) {
            Intent i = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_contact) {
            Intent i = new Intent(getApplicationContext(), ContactActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private interface OnListItemClickListener {
        void onListItemClicked(View view, int position);
    }


    class CustomAdapter extends BaseAdapter {


        //create an instance
        private OnListItemClickListener onListItemClickListener;

        //define the object setter
        void setOnListItemClickListener(OnListItemClickListener listener) {
            this.onListItemClickListener = listener;
        }


        @Override
        public int getCount() {
            return comments_dateList.size();
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position == 3 || position == 7 || position == 11){
                convertView = getLayoutInflater().inflate(R.layout.customlayout_lastcomment_homepageadmob, null);
                mAdView = convertView.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            } else {

                convertView = getLayoutInflater().inflate(R.layout.customlayout_lastcomment_homepage, null);
                TextView textViewUser = (TextView) convertView.findViewById(R.id.textViewUser);
                TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
                TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
                textViewUser.setText(comments_userList.get(position));
                textViewDate.setText(comments_dateList.get(position));
                textViewMessage.setText(comments_messageList.get(position));

                Drawable d = convertView.getBackground();
                if (comments_statusList.get(position).equalsIgnoreCase("positive")) {
                    d.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                } else if (comments_statusList.get(position).equalsIgnoreCase("neutral")) {
                    d.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                } else if (comments_statusList.get(position).equalsIgnoreCase("negative")) {
                    d.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }


                //Then here trigger
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onListItemClickListener.onListItemClicked(v, position);
                        String userID = comments_olxUserList.get(position);
                        Intent i = new Intent(getApplicationContext(), UserAreaActivity.class);
                        if (userID.length() == 8) {
                            i.putExtra("isBank", "true");
                        } else {
                            i.putExtra("isBank", "false");
                        }
                        i.putExtra("userID", userID);
                        startActivity(i);

                    }
                });

            }


            return convertView;
        }


    }


}