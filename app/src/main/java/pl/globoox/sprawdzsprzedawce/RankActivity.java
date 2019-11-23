package pl.globoox.sprawdzsprzedawce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import pl.globoox.sprawdzsprzedawce.Utils.ranksDisplay;

public class RankActivity extends AppCompatActivity {

    ListView listViewRanks;
    String rankType;
    GifImageView loadingImage;
    ArrayList<String> ranks_dateList = new ArrayList();
    ArrayList<String> ranks_userList = new ArrayList();
    ArrayList<String> ranks_userIDList = new ArrayList();
    ArrayList<String> ranks_positive = new ArrayList();
    ArrayList<String> ranks_negative = new ArrayList();
    ArrayList<String> ranks_neutral = new ArrayList();
    ArrayList<String> ranks_comments = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank);

        Intent i = getIntent();
        rankType = i.getExtras().getString("type");
        ActionBar actionbar = getSupportActionBar();

        loadingImage = findViewById(R.id.loadingImage);
        loadingImage.setVisibility(View.VISIBLE);

        if (rankType.equalsIgnoreCase("best")) {
            actionbar.setTitle("Najlepsi sprzedawcy");
        } else if (rankType.equalsIgnoreCase("worst")) {
            actionbar.setTitle("Najgorsi sprzedawcy");
        } else if (rankType.equalsIgnoreCase("comment")) {
            actionbar.setTitle("Najczęściej komentowani");
        }


        listViewRanks = (ListView) findViewById(R.id.listViewRanks);
        // LAST COMMENTS SHOW
        Response.Listener<String> responseListenerRanksDisplay = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int errorCode = jsonResponse.getInt("errorCode");
                    int count = jsonResponse.getInt("count");

                    JSONArray commentsArray = jsonResponse.getJSONArray("comments");

                    for (int i = 0; i < count; i++) {
                        JSONObject jsonObject = commentsArray.getJSONObject(i);
                        ranks_userList.add(jsonObject.getString("user"));
                        ranks_userIDList.add(jsonObject.getString("userID"));
                        ranks_dateList.add(jsonObject.getString("registerDate"));
                        ranks_neutral.add(jsonObject.getString("neutral"));
                        ranks_positive.add(jsonObject.getString("positive"));
                        ranks_negative.add(jsonObject.getString("negative"));
                        ranks_comments.add(jsonObject.getString("comments"));
                    }


                    RankActivity.CustomAdapter customAdapter = new RankActivity.CustomAdapter();
                    customAdapter.setOnListItemClickListener(new RankActivity.OnListItemClickListener() {
                        @Override
                        public void onListItemClicked(View view, int position) {
                        }
                    });
                    listViewRanks.setAdapter(customAdapter);
                    loadingImage.setVisibility(View.INVISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        ranksDisplay ranksDisplay = new ranksDisplay("10", rankType, responseListenerRanksDisplay);
        RequestQueue queue = Volley.newRequestQueue(RankActivity.this);
        queue.add(ranksDisplay);

    }

    private interface OnListItemClickListener {
        void onListItemClicked(View view, int position);
    }

    class CustomAdapter extends BaseAdapter {


        //create an instance
        private RankActivity.OnListItemClickListener onListItemClickListener;

        //define the object setter
        void setOnListItemClickListener(RankActivity.OnListItemClickListener listener) {
            this.onListItemClickListener = listener;
        }


        @Override
        public int getCount() {
            return ranks_dateList.size();
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
            convertView = getLayoutInflater().inflate(R.layout.customlayout_tablerank, null);
            TextView textViewPosition = (TextView) convertView.findViewById(R.id.textViewPosition);
            TextView textViewUser = (TextView) convertView.findViewById(R.id.textViewRankUser);
            TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewRankSince);
            TextView textViewRankCommentsCount = (TextView) convertView.findViewById(R.id.textViewRankCommentsCount);
            TextView textViewRankPositiveCount = (TextView) convertView.findViewById(R.id.textViewRankPositiveCount);
            TextView textViewRankNegativeCount = (TextView) convertView.findViewById(R.id.textViewRankNegativeCount);
            TextView textViewRankNeutralCount = (TextView) convertView.findViewById(R.id.textViewRankNeutralCount);
            textViewUser.setText(ranks_userList.get(position));
            textViewDate.setText(ranks_dateList.get(position));

            // POSITION AND USERNAME TEXT
            int positionShow = position + 1;
            textViewPosition.setText("#" + positionShow);

            Log.d("TAG", ranks_positive.get(position));
            textViewRankPositiveCount.setText(ranks_positive.get(position));
            textViewRankNeutralCount.setText(ranks_neutral.get(position));
            textViewRankNegativeCount.setText(ranks_negative.get(position));
            textViewRankCommentsCount.setText(ranks_comments.get(position));

            // DATE SINCE
            if (ranks_userIDList.get(position).length() == 8) {
                textViewDate.setText("Konto bankowe...");
                textViewUser.setText("ID konta: " + ranks_userIDList.get(position));
            } else {
                textViewDate.setText(ranks_dateList.get(position));
                textViewUser.setText(ranks_userList.get(position));
            }


            // CLICK BUTTON GO TO USERACTIVITY
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListItemClickListener.onListItemClicked(v, position);
                    Intent i = new Intent(getApplicationContext(), UserAreaActivity.class);
                    if (ranks_userIDList.get(position).length() == 8) {
                        i.putExtra("isBank", "true");
                    } else {
                        i.putExtra("isBank", "false");
                    }
                    i.putExtra("userID", ranks_userIDList.get(position));
                    startActivity(i);
                }
            });

            return convertView;
        }

    }
}
