package com.ilenlab.ilentt.twitterclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ilenlab.ilentt.twitterclient.R;
import com.ilenlab.ilentt.twitterclient.activities.ProfileActivity;
import com.ilenlab.ilentt.twitterclient.adapters.TweetAdapter;
import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.ilenlab.ilentt.twitterclient.models.User;
import com.ilenlab.ilentt.twitterclient.network.TwitterClient;
import com.ilenlab.ilentt.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 4/5/2016.
 */
public abstract class TweetsListFragment extends Fragment {
    RecyclerView rvTweets;
    ArrayList<Tweets> tweets;
    TweetAdapter adapter;
    TwitterClient client;
    SwipeRefreshLayout swipeContainer;
    User user;

    protected abstract void populateTimeline(long since_id, long max_id);
    protected abstract void fetchTimelineAsync();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        rvTweets.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                long max_id = tweets.get(tweets.size() - 1).getUid() - 1;
                populateTimeline(1, max_id);
            }
        });

        adapter.setOnItemClickListener(new TweetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (view instanceof ImageView) {
                    showUserProfile(view.getTag().toString());
                } else {
                    showTweetDetailDialog(position);
                }

            }
        });

        return v;
    }

    private void showTweetDetailDialog(int position) {
        // pass in the user's profile image and the tweet
        FragmentManager fm = getActivity().getSupportFragmentManager();
        String myProfileImageUrl = user.getProfileImageUrl();
        TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(myProfileImageUrl, tweets.get(position));
        tweetDetailFragment.show(fm, "fragment_tweet_detail");
    }

    public void addAll(List<Tweets> initialOrOlderTweets) {
        final int previousTweetsLength = tweets.size();
        tweets.addAll(initialOrOlderTweets);
        adapter.notifyItemRangeInserted(previousTweetsLength, initialOrOlderTweets.size());
    }

    public void insertAll(List<Tweets> newTweets) {
        tweets.addAll(0, newTweets);
        adapter.notifyItemRangeInserted(0, newTweets.size());
        goOnTop();
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

    private void goOnTop() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager = (LinearLayoutManager) rvTweets.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    public void postTheNewTweet(String myTweetText) {
        Log.d("TWEET", myTweetText);
        client.postTweet(myTweetText, new JsonHttpResponseHandler() {
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
                Toast toast = Toast.makeText(getActivity(), "Tweet posted!", Toast.LENGTH_SHORT);
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

    public void showUserProfile(String screenName) {
        client.getOtherUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                User user = User.fromJSON(json);
                // launch the profile view
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
