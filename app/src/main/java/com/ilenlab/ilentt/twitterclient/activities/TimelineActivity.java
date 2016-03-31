package com.ilenlab.ilentt.twitterclient.activities;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ilenlab.ilentt.twitterclient.R;
import com.ilenlab.ilentt.twitterclient.adapters.TweetAdapter;
import com.ilenlab.ilentt.twitterclient.fragments.ComposeFragment;
import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.ilenlab.ilentt.twitterclient.models.User;
import com.ilenlab.ilentt.twitterclient.network.TwitterApplication;
import com.ilenlab.ilentt.twitterclient.network.TwitterClient;
import com.ilenlab.ilentt.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    ArrayList<Tweets> tweets;
    TweetAdapter adapter;
    User userAccount;
    RecyclerView rvTweet;

    private TwitterClient twitterClient;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweet = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        rvTweet.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweet.setLayoutManager(linearLayoutManager);
        rvTweet.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });


        adapter.setOnItemClickListener(new TweetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                showTweetDetailDialog(position);
            }
        });

        twitterClient = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void showTweetDetailDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();
        String avataUrl = userAccount.getProfileImageUrl();
        ComposeFragment composeFragment = ComposeFragment.newInstance(avataUrl);
        composeFragment.show(fm, "fragment_compose");
    }

    public void fetchTimelineAsync(int page) {
        long since_id;
        long max_id = 0;

        Tweets newestTweet = tweets.get(0);
        since_id = newestTweet.getUid();
        twitterClient.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                tweets.addAll(0, Tweets.fromJsonArray(jsonArray));
                adapter.notifyItemRangeInserted(0, jsonArray.length());
                goOnTop();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }


    private void goOnTop() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TimelineActivity.this);
        linearLayoutManager = (LinearLayoutManager) rvTweet.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    // send an API request to get the timeline json
    // fill the list view by createing the tweet object from the json
    private void populateTimeline() {
        final int previousTweetLength = tweets.size();
        long max_id = 0;
        long since_id = 1;

        if (previousTweetLength > 0) {
            max_id = tweets.get(previousTweetLength - 1).getUid() + 1;
        }
        twitterClient.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweets.addAll(Tweets.fromJsonArray(response));
                adapter.notifyItemRangeInserted(previousTweetLength, response.length());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void onTweetButtonClicked(String myTweetText) {
        // when the user composes a new tweet and taps the Tweet button, post it
        twitterClient.postTweet(myTweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                // get the new tweet and add it to the ArrayList
                Tweets myNewTweet = Tweets.fromJSON(json);
                tweets.add(0, myNewTweet);
                // notify the adapter
                adapter.notifyItemInserted(0);
                // scroll back to display the new tweet
                goOnTop();
                // display a success Toast
                Toast toast = Toast.makeText(TimelineActivity.this, "Tweet posted!", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(0xC055ACEE);
                TextView textView = (TextView) view.findViewById(android.R.id.message);
                textView.setTextColor(0xFFFFFFFF);
                toast.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
