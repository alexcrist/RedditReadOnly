package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.activity.CommentActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;

public class LoadSubmission extends AsyncTask<String, Void, Submission> {

  private RedditClient redditClient;
  private String id;
  private CommentActivity activity;
  private PostExecute post;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public LoadSubmission(RedditClient redditClient, String id, CommentActivity activity,
                        PostExecute post) {
    this.redditClient = redditClient;
    this.id = id;
    this.activity = activity;
    this.post = post;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected Submission doInBackground(String... params) {
    return redditClient.getSubmission(id);
  }

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPostExecute(Submission submission) {
    activity.setSubmission(submission);
    post.onPostExecute();
  }
}
