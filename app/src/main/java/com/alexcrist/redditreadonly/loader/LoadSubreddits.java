package com.alexcrist.redditreadonly.loader;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.activity.BrowseActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.UserSubredditsPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadSubreddits extends Load<List<String>> {

  Activity activity;
  UserSubredditsPaginator subredditPaginator;
  Menu menu;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public LoadSubreddits(PostExecute post, Activity activity, RedditClient redditClient,
                        Menu menu) {
    super(post);
    this.activity = activity;
    this.subredditPaginator = new UserSubredditsPaginator(redditClient, "subscriber");
    this.subredditPaginator.setLimit(100);
    this.menu = menu;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected List<String> onLoad(String... params) {
    if (subredditPaginator.hasNext()) {
      Listing<Subreddit> subreddits = subredditPaginator.next();
      List<String> subredditNames = onLoad(params);
      for (Subreddit subreddit : subreddits) {
        subredditNames.add(subreddit.getDisplayName().toLowerCase());
      }
      return subredditNames;
    } else {
      return new ArrayList<>();
    }
  }

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPost(List<String> subredditNames) {
    LoadSubreddits.setSubredditNames(menu, subredditNames);
    ((MyApplication) activity.getApplication()).setSubredditNames(subredditNames);
  }

  // Populate a menu with given list of subreddit names
  // -----------------------------------------------------------------------------------------------

  public static void setSubredditNames(Menu menu, List<String> subredditNames) {
    Collections.sort(subredditNames);
    Menu subMenu = menu.getItem(0).getSubMenu();
    subMenu.clear();
    for (int i = 0; i < subredditNames.size(); i++) {
      subMenu.addSubMenu(0, i, Menu.NONE, subredditNames.get(i));
    }
  }
}
