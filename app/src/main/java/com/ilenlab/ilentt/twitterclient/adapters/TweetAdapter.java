package com.ilenlab.ilentt.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilenlab.ilentt.twitterclient.R;
import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by ADMIN on 3/28/2016.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private static OnItemClickListener listener;
    private List<Tweets> mTweets;

    public TweetAdapter(List<Tweets> tweets) {
        mTweets = tweets;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweets, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweets tweet = mTweets.get(position);

        TextView tvUserName = holder.tvUserName;
        TextView tvScreenName = holder.tvScreenName;
        TextView tvTime = holder.tvTime;
        TextView tvBody = holder.tvBody;

        tvUserName.setText(tweet.getUser().getName() + " ");
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTime.setText(createTime(tweet.getCreatedAt()));
        tvBody.setText(tweet.getBody());

        ImageView ivAvatar = holder.ivAvatar;
        Picasso.with(holder.ivAvatar.getContext()).load(tweet.getUser().getProfileImageUrl()).fit().centerCrop().into(ivAvatar);
    }

    private String createTime(String createTime) {
        String tweetTime = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tweetTime, Locale.ENGLISH);
        simpleDateFormat.setLenient(true);

        String timeString;
        long msDate = 0;

        try {
            msDate = simpleDateFormat.parse(createTime).getTime();
        }catch(ParseException e) {
            e.printStackTrace();
        }

        long dateString = Math.max(System.currentTimeMillis() - msDate, 0);
        if(dateString > 604800000L) {
            timeString = (dateString / 604800000L) + "w";
        } else if(dateString > 86400000L) {
            timeString = (dateString / 86400000L) + "d";
        } else if(dateString > 3600000L) {
            timeString = (dateString / 3600000L) + "h";
        } else if(dateString > 60000) {
            timeString = (dateString / 60000) + "m";
        } else {
            timeString = (dateString / 1000) + "s";
        }
        return timeString;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivAvatar;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvTime;
        public TextView tvBody;


        public ViewHolder(final View itemView) {
            super(itemView);
            this.ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.tvBody = (TextView) itemView.findViewById(R.id.tvBody);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}
