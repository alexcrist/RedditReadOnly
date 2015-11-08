package com.alexcrist.redditreadonly.loader;

import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.adapter.SubmissionAdapter;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

public class LoadPage extends Load<Listing<Submission>> {

  private SubredditPaginator paginator;
  private SubmissionAdapter adapter;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public LoadPage(PostExecute post, SubredditPaginator paginator, SubmissionAdapter adapter) {
    super(post);
    this.paginator = paginator;
    this.adapter = adapter;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected Listing<Submission> onLoad(String... params) {
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
  protected void onPost(Listing<Submission> listing) {
    for (Submission submission : listing) {
      adapter.add(submission);
    }
  }
}
