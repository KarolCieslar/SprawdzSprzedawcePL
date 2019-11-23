package pl.globoox.sprawdzsprzedawce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import pl.globoox.sprawdzsprzedawce.Utils.AccountComments;
import pl.globoox.sprawdzsprzedawce.Utils.deleteComment;

public class ProfileActivity extends AppCompatActivity {

    ListView accountComments;
    String fbID;
    TextView textViewNoComments;
    GifImageView loadingImage;
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

        setContentView(R.layout.activity_profile);



        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn == true) {

            Intent i = getIntent();
            fbID = i.getExtras().getString("fbID");


            textViewNoComments = (TextView) findViewById(R.id.textViewNoComments);
            textViewNoComments.setVisibility(View.INVISIBLE);
            accountComments = (ListView) findViewById(R.id.listViewAccountComments);
            loadingImage = findViewById(R.id.loadingImage);

            // LAST COMMENTS SHOW
            Response.Listener<String> responseListenerAccountComments = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int count = jsonResponse.getInt("count");

                        if (count > 0) {
                            JSONArray commentsArray = jsonResponse.getJSONArray("comments");
                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject = commentsArray.getJSONObject(i);
                                userComments_userList.add(jsonObject.getString("user"));
                                userComments_olxUserList.add(jsonObject.getString("olxUser"));
                                userComments_dateList.add(jsonObject.getString("date"));
                                userComments_messageList.add(jsonObject.getString("message"));
                                userComments_statusList.add(jsonObject.getString("status"));
                                userComments_senderID.add(jsonObject.getString("senderID"));
                                userComments_commentIDList.add(jsonObject.getString("commentID"));
                            }

                            ProfileActivity.CustomAdapter customAdapter = new ProfileActivity.CustomAdapter();
                            customAdapter.setOnListItemClickListener(new ProfileActivity.OnListItemClickListener() {
                                @Override
                                public void onListItemClicked(View view, int position) {
                                }
                            });
                            accountComments.setAdapter(customAdapter);


                        } else {
                            textViewNoComments.setVisibility(View.VISIBLE);
                        }

                        loadingImage.setVisibility(View.INVISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };


            AccountComments accountComments = new AccountComments(fbID, responseListenerAccountComments);
            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
            queue.add(accountComments);
        } else {
            textViewNoComments = (TextView) findViewById(R.id.textViewNoComments);
            textViewNoComments.setText("Nie jesteś zalogowany!");
            loadingImage = findViewById(R.id.loadingImage);
            loadingImage.setVisibility(View.INVISIBLE);
        }
    }

    private interface OnListItemClickListener {
        void onListItemClicked(View view, int position);
    }


    class CustomAdapter extends BaseAdapter {

        //create an instance
        private ProfileActivity.OnListItemClickListener onListItemClickListener;

        //define the object setter
        void setOnListItemClickListener(ProfileActivity.OnListItemClickListener listener) {
            this.onListItemClickListener = listener;
        }


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
            convertView = getLayoutInflater().inflate(R.layout.customlayout_accountcmments, null);
            TextView textViewUser = (TextView) convertView.findViewById(R.id.textViewUser);
            TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            textViewUser.setText(userComments_userList.get(position));
            textViewDate.setText(userComments_dateList.get(position));
            textViewMessage.setText(userComments_messageList.get(position));
            Drawable d = convertView.getBackground();
            if (userComments_statusList.get(position).equalsIgnoreCase("positive")) {
                d.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_ATOP);
            } else if (userComments_statusList.get(position).equalsIgnoreCase("neutral")) {
                d.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_ATOP);
            } else if (userComments_statusList.get(position).equalsIgnoreCase("negative")) {
                d.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_ATOP);
            }


            //Then here trigger
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListItemClickListener.onListItemClicked(v, position);
                    String userID = userComments_olxUserList.get(position);
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


            Button editButton = convertView.findViewById(R.id.buttonEditComment);
            Button deleteButton = convertView.findViewById(R.id.buttonDeleteComment);
            editButton.setClickable(true);
            deleteButton.setClickable(true);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
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

            return convertView;
        }

    }

}
