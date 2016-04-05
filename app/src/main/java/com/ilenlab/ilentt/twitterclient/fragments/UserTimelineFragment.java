package com.ilenlab.ilentt.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.ilenlab.ilentt.twitterclient.models.User;
import com.ilenlab.ilentt.twitterclient.network.TwitterApplication;
import com.ilenlab.ilentt.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ADMIN on 4/5/2016.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(1, 0);
    }

    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        userFragment.setArguments(args);
        return userFragment;
    }

    @Override
    protected void populateTimeline(long since_id, long max_id) {
        User user = (User) getArguments().getSerializable("user");
        client.getUserTimeline(since_id, max_id, user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                addAll(Tweets.fromJsonArray(json));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    protected void fetchTimelineAsync() {
        long since_id;
        long max_id = 0;
        // get tweets newer than the current newest tweet
        Tweets newestDisplayedTweet = tweets.get(0);
        since_id = newestDisplayedTweet.getUid();
        client.getUserTimeline(since_id, max_id, user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                insertAll(Tweets.fromJsonArray(json));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
