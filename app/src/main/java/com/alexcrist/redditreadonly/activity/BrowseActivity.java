package com.alexcrist.redditreadonly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.R;
import com.alexcrist.redditreadonly.adapter.SubmissionAdapter;
import com.alexcrist.redditreadonly.loader.LoadPage;
import com.alexcrist.redditreadonly.loader.LoadSubreddits;
import com.alexcrist.redditreadonly.loader.RevokeToken;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
  ListView.OnScrollListener, SwipeMenuListView.OnMenuItemClickListener, PostExecute {

  private RedditClient redditClient;
  private SubredditPaginator paginator;
  private SubmissionAdapter adapter;
  private ProgressBarIndeterminate progressBar;
  private boolean loading;

  // Initialization
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browse);

    redditClient = ((MyApplication) this.getApplication()).getRedditClient();
    paginator = ((MyApplication) this.getApplication()).getPaginator();
    adapter = new SubmissionAdapter(this, R.layout.submission_layout, new ArrayList<Submission>());
    initListView();

    progressBar = (ProgressBarIndeterminate) findViewById(R.id.progressBar);
    progressBar.setVisibility(ProgressBar.VISIBLE);
    loading = false;
    loadPage();

    if (paginator.getSubreddit() == null) {
      setTitle("Front Page");
    } else {
      setTitle(paginator.getSubreddit());
    }
  }

  private void initListView() {
    SwipeMenuCreator creator = new SwipeMenuCreator() {
      @Override
      public void create(SwipeMenu menu) {
        SwipeMenuItem comment = new SwipeMenuItem(getApplicationContext());
        comment.setBackground(R.drawable.comment_bg);
        comment.setWidth(310);
        menu.addMenuItem(comment);
      }
    };

    SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.submissionListView);
    listView.setMenuCreator(creator);
    listView.setOnItemClickListener(this);
    listView.setOnMenuItemClickListener(this);
    listView.setOnScrollListener(this);
    listView.setAdapter(adapter);
  }

  // Menu
  // -----------------------------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.browse_menu, menu);

    List<String> subredditNames = ((MyApplication) getApplication()).getSubredditNames();
    if (subredditNames == null) {
      new LoadSubreddits(null, this, redditClient, menu).execute();
    } else {
      LoadSubreddits.setSubredditNames(menu, subredditNames);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.refresh) {
      refresh();
    } else if (item.getItemId() == R.id.logout) {
      logout();
    } else if (0 <= item.getItemId()) {
      List<String> subredditNames = ((MyApplication) getApplication()).getSubredditNames();
      if (subredditNames != null) {
        if (item.getItemId() < subredditNames.size()) {
          final String subreddit = item.getTitle().toString();

          paginator.setSubreddit(subreddit);
          paginator.reset();
          refresh();
        }
      }
    }
    return super.onOptionsItemSelected(item);
  }

  // Load a page of submissions
  // -----------------------------------------------------------------------------------------------

  private void loadPage() {
    loadPage(this);
  }

  private void loadPage(PostExecute post) {
    if (((MyApplication) this.getApplication()).getRedditClient().isAuthenticated()) {
      if (!loading) {
        loading = true;
        new LoadPage(post, paginator, adapter).execute();
      }
    } else {
      Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onPostExecute() {
    loading = false;
    progressBar.setVisibility(ProgressBar.GONE);
  }

  // On user click
  // -----------------------------------------------------------------------------------------------

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
    Submission submission = adapter.getItem(index);
    if (submission.isSelfPost()) {
      gotoComments(index);
    } else {
      Intent intent = new Intent(this, ViewingActivity.class);
      intent.putExtra("url", submission.getUrl());
      startActivity(intent);
    }
  }

  @Override
  public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
    switch (index) {
      // go to comments
      case 0:
        gotoComments(position);
        return true;
    }
    return false;
  }

  // On user scroll
  // -----------------------------------------------------------------------------------------------

  @Override
  public void onScrollStateChanged(AbsListView absListView, int i) { }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                       int totalItemCount) {
    switch (view.getId()) {
      // load the next page when you're 15 items away from the bottom
      case R.id.submissionListView:
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem >= totalItemCount - 15) {
          loadPage();
        }
    }
  }

  // On user presses back button
  // -----------------------------------------------------------------------------------------------

  @Override
  public void onBackPressed() {
    if (paginator.getSubreddit() != null) {
      paginator.setSubreddit(null);
      paginator.reset();
      refresh();
    }
  }

  // Go to comments
  // -----------------------------------------------------------------------------------------------

  private void gotoComments(final int index) {
    if (((MyApplication) this.getApplication()).getRedditClient().isAuthenticated()) {
      Intent intent = new Intent(this, CommentActivity.class);
      intent.putExtra("submissionId", adapter.getItem(index).getId());
      startActivity(intent);
    } else {
      ((MyApplication) this.getApplication()).reauthenticate(new PostExecute() {
        @Override
        public void onPostExecute() {
          gotoComments(index);
        }
      });
    }
  }

  // Refresh
  // -----------------------------------------------------------------------------------------------

  private void refresh() {
    finish();
    startActivity(getIntent());
  }

  // Refresh
  // -----------------------------------------------------------------------------------------------

  private void logout() {
    final Intent intent = new Intent(this, LoginActivity.class);
    finish();
    ((MyApplication) getApplication()).deauthenticate(new PostExecute() {
      @Override
      public void onPostExecute() {
        finish();
        startActivity(intent);
      }
    });
  }
}
