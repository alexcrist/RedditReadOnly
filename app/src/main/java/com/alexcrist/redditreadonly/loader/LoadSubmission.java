package com.alexcrist.redditreadonly.loader;

import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.activity.CommentActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;

public class LoadSubmission extends Load<Submission> {

  private RedditClient redditClient;
  private String id;
  private CommentActivity activity;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public LoadSubmission(PostExecute post, RedditClient redditClient, String id,
                        CommentActivity activity) {
    super(post);
    this.redditClient = redditClient;
    this.id = id;
    this.activity = activity;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected Submission onLoad(String... params) {
    return redditClient.getSubmission(id);
  }

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPost(Submission submission) {
    activity.setSubmission(submission);
  }
}
