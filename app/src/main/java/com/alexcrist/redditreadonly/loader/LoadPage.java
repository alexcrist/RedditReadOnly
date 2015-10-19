package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.adapter.SubmissionAdapter;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

public class LoadPage extends AsyncTask<String, Void, Listing<Submission>> {

  private SubredditPaginator paginator;
  private SubmissionAdapter adapter;
  private PostExecute post;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public LoadPage(SubredditPaginator paginator, SubmissionAdapter adapter, PostExecute post) {
    this.paginator = paginator;
    this.adapter = adapter;
    this.post = post;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected Listing<Submission> doInBackground(String... params) {
    try {
      return paginator.next();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPostExecute(Listing<Submission> listing) {
    if (listing != null) {
      for (Submission submission : listing) {
        adapter.add(submission);
      }
    }
    post.onPostExecute();
  }
}
