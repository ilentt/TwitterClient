package com.ilenlab.ilentt.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ilenlab.ilentt.twitterclient.R;
import com.ilenlab.ilentt.twitterclient.models.Tweets;
import com.ilenlab.ilentt.twitterclient.utils.TimeFormat;

import java.util.List;

/**
 * Created by ADMIN on 3/28/2016.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private OnItemClickListener listener;
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
        //tvTime.setText(createTime(tweet.getCreatedAt()));
        tvTime.setText(TimeFormat.createTime(tweet.getCreatedAt()));
        tvBody.setText(tweet.getBody());

        ImageView ivAvatar = holder.ivAvatar;
        ivAvatar.setTag(tweet.getUser().getScreenName());
        //Picasso.with(holder.ivAvatar.getContext()).load(tweet.getUser().getProfileImageUrl()).fit().centerCrop().into(ivAvatar);
        Glide.with(holder.ivAvatar.getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivAvatar);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

            this.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(v, getLayoutPosition());
                    }
                }
            });

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

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
