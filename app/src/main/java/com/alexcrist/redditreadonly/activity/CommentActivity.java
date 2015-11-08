package com.alexcrist.redditreadonly.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.R;
import com.alexcrist.redditreadonly.adapter.CommentAdapter;
import com.alexcrist.redditreadonly.loader.LoadSubmission;
import com.alexcrist.redditreadonly.model.MyComment;
import com.alexcrist.redditreadonly.util.Text;
import com.gc.materialdesign.views.ProgressBarIndeterminate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements PostExecute,
    AdapterView.OnItemClickListener {

  private Submission submission;
  private List<MyComment> list;
  private ListView listView;
  private CommentAdapter adapter;
  private ProgressBarIndeterminate progressBar;
  private ImageView thumbnail;

  // Initialization
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);

    listView = (ListView) findViewById(R.id.commentListView);
    progressBar = (ProgressBarIndeterminate) findViewById(R.id.progressBar);

    list = new ArrayList<>();
    RedditClient redditClient = ((MyApplication) this.getApplication()).getRedditClient();
    String id = getIntent().getStringExtra("submissionId");
    new LoadSubmission(this, redditClient, id, this).execute();
  }

  @Override
  public void onPostExecute() {
    if (submission != null) {
      progressBar.setVisibility(ProgressBar.GONE);
      initSubmission();
      initCommentTree();
    } else {
      finish();
    }
  }

  private void initSubmission() {
    LayoutInflater inflater = getLayoutInflater();
    LinearLayout header = (LinearLayout) inflater.inflate(R.layout.submission_layout, listView,
        false);

    String topStr = Integer.toString(submission.getScore()) + " - " + submission.getSubredditName();
    String bodyStr = submission.getTitle();
    String bottomStr = submission.getCommentCount() + " comments";

    ((TextView) header.findViewById(R.id.topText)).setText(topStr);
    ((TextView) header.findViewById(R.id.body)).setText(bodyStr);
    ((TextView) header.findViewById(R.id.bottomText)).setText(bottomStr);

    thumbnail = ((ImageView) header.findViewById(R.id.thumbnail));
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
    ImageLoader imageLoader = ImageLoader.getInstance();
    imageLoader.init(config);
    DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .build();
    ImageAware imageAware = new ImageViewAware(thumbnail, false);
    imageLoader.displayImage(submission.getThumbnail(), imageAware, options,
        new SimpleImageLoadingListener() {
          @Override
          public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage == null) {
              thumbnail.setVisibility(View.GONE);
            } else {
              thumbnail.setVisibility(View.VISIBLE);
            }
          }
        });

    listView.addHeaderView(header);

    if (submission.isSelfPost()) {
      initSelfPost();
    } else {
      header.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(getApplicationContext(), ViewingActivity.class);
          intent.putExtra("url", submission.getUrl());
          startActivity(intent);
        }
      });
    }
  }

  private void initSelfPost() {
    if (submission.getSelftext().length() > 0) {
      LayoutInflater inflater = getLayoutInflater();
      LinearLayout header = (LinearLayout) inflater.inflate(R.layout.self_text_layout,
          listView, false);
      TextView body = (TextView) header.findViewById(R.id.body);
      body.setText(Text.format(submission.getSelftext()));
      listView.addHeaderView(header);
    }
  }

  private void initCommentTree() {
    CommentNode rootNode = submission.getComments();
    explodeNode(rootNode, 0);
    adapter = new CommentAdapter(this, R.layout.comment_layout, list,
        submission);
    listView.setOnItemClickListener(this);
    listView.setAdapter(adapter);
  }

  private void explodeNode(CommentNode parent, int depth) {
    List<CommentNode> children = parent.getChildren();
    int count = 0;
    for (CommentNode child : children) {
      if ((count == 3 && depth > 0) || count == 50) { // load 50 top level comments
        break;
      }
      MyComment comment = new MyComment(child.getComment(), depth);
      list.add(comment);
      if (depth < 3) {
        explodeNode(child, depth + 1);
      }
      count++;
    }
  }

  // On user click
  // -----------------------------------------------------------------------------------------------

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
    MyComment comment = adapter.getItem(index - 1); // I honestly don't know why it's minus 1
    List<String> linkList = Text.extractLinks(comment.body);
    if (linkList.size() == 1) {
      Intent intent = new Intent(getApplicationContext(), ViewingActivity.class);
      intent.putExtra("url", linkList.get(0));
      startActivity(intent);
    } else if (linkList.size() > 1) {
      linkPickerDialog(linkList);
    }
  }

  private void linkPickerDialog(final List<String> linkList) {
    String[] linkArray = new String[linkList.size()];
    linkList.toArray(linkArray);
    AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("Links")
        .setItems(linkArray, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int index) {
            Intent intent = new Intent(getApplicationContext(), ViewingActivity.class);
            intent.putExtra("url", linkList.get(index));
            startActivity(intent);
          }
        });
    builder.show();
  }

  // Getters and setters
  // -----------------------------------------------------------------------------------------------

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }
}
