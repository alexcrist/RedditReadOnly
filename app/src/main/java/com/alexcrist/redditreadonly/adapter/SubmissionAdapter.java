package com.alexcrist.redditreadonly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexcrist.redditreadonly.R;
import com.alexcrist.redditreadonly.util.Text;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.dean.jraw.models.Submission;

import java.util.List;

public class SubmissionAdapter extends ArrayAdapter<Submission> {

  private final Context context;
  private final int resource;
  private final List<Submission> page;
  private ImageLoader imageLoader;
  private DisplayImageOptions options;
  private ViewHolder holder;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public SubmissionAdapter(Context context, int resource, List<Submission> page) {
    super(context, resource, page);
    this.context = context;
    this.resource = resource;
    this.page = page;
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
    this.imageLoader = ImageLoader.getInstance();
    this.imageLoader.init(config);
    this.options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .build();
  }

  // Return view of each ListView entry
  // -----------------------------------------------------------------------------------------------

  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(resource, parent, false);

      holder = new ViewHolder();
      holder.topText = (TextView) convertView.findViewById(R.id.topText);
      holder.bodyText = (TextView) convertView.findViewById(R.id.body);
      holder.bottomText = (TextView) convertView.findViewById(R.id.bottomText);
      holder.nsfw = (TextView) convertView.findViewById(R.id.nsfw);
      holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Submission submission = page.get(pos);
    if (submission != null) {
      String scoreAndSubreddit = Integer.toString(submission.getScore()) + " - " +
          submission.getSubredditName().toLowerCase();
      String title = Text.format(submission.getTitle());
      String numComments = Integer.toString(submission.getCommentCount()) + " comments";

      if (submission.isNsfw()) {
        scoreAndSubreddit += " - ";
        holder.nsfw.setVisibility(View.VISIBLE);
      } else {
        holder.nsfw.setVisibility(View.GONE);
      }

      holder.topText.setText(scoreAndSubreddit);
      holder.bodyText.setText(title);
      holder.bottomText.setText(numComments);

      ImageAware imageAware = new ImageViewAware(holder.thumbnail, false);
      imageLoader.displayImage(submission.getThumbnail(), imageAware, options,
          new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) { }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
              holder.thumbnail.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
              if (loadedImage == null) {
                holder.thumbnail.setVisibility(View.GONE);
              } else {
                holder.thumbnail.setVisibility(View.VISIBLE);
              }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) { }
          });
    }
    return convertView;
  }

  // Reusable view holder to reduce memory cost
  // -----------------------------------------------------------------------------------------------

  private static class ViewHolder {
    TextView topText;
    TextView bodyText;
    TextView bottomText;
    TextView nsfw;
    ImageView thumbnail;
  }
}
