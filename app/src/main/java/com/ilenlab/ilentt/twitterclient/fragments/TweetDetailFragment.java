package com.ilenlab.ilentt.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ilenlab.ilentt.twitterclient.R;
import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.ilenlab.ilentt.twitterclient.utils.TimeFormat;

/**
 * Created by ADMIN on 4/5/2016.
 */
public class TweetDetailFragment extends android.support.v4.app.DialogFragment {
    private String avatarImageUrl;
    private Tweets tweet;

    public TweetDetailFragment() {

    }

    public static TweetDetailFragment newInstance(String avatarImageUrl, Tweets tweet) {
        // get the tweet to display and the logged-in user's profile image URL
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putString("profileImageUrl", avatarImageUrl);
        args.putSerializable("tweet", tweet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweet_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Get fields from view
        ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ImageView avatarImageUrl = (ImageView) view.findViewById(R.id.ivAvatarImageUrl);
        ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        TextView tvTweetBody = (TextView) view.findViewById(R.id.tvTweetBody);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        // Fetch arguments from bundle
        // display the logged-in user's profile image
        this.avatarImageUrl = getArguments().getString("profileImageUrl");
        //Picasso.with(view.getContext()).load(this.avatarImageUrl).fit().centerCrop().into(avatarImageUrl);
        Glide.with(view.getContext()).load(this.avatarImageUrl).fitCenter().centerCrop().into(avatarImageUrl);

        // and display the tweet
        tweet = (Tweets) getArguments().getSerializable("tweet");
        //Picasso.with(view.getContext()).load(tweet.getUser().getProfileImageUrl()).fit().centerCrop().into(ivProfileImage);
        Glide.with(view.getContext()).load(tweet.getUser().getProfileImageUrl()).fitCenter().centerCrop().into(avatarImageUrl);


        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTweetBody.setText(tweet.getBody());
        String tweetTimeStamp = TimeFormat.tweetDetailDateFormat(tweet.getCreatedAt());
        tvTimeStamp.setText(tweetTimeStamp);

        // the X in the top-left corner closes the fragment
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
