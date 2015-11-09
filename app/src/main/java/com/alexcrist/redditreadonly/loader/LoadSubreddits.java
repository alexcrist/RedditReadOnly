package com.alexcrist.redditreadonly.loader;

import android.util.Log;
import android.view.Menu;

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

  BrowseActivity activity;
  UserSubredditsPaginator subredditPaginator;
  Menu menu;

  public LoadSubreddits(PostExecute post, BrowseActivity activity, RedditClient redditClient,
                        Menu menu) {
    super(post);
    this.activity = activity;
    this.subredditPaginator = new UserSubredditsPaginator(redditClient, "subscriber");
    this.subredditPaginator.setLimit(100);
    this.menu = menu;
  }

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

  @Override
  protected void onPost(List<String> subredditNames) {
    Collections.sort(subredditNames);
    Menu subMenu = menu.getItem(0).getSubMenu();
    subMenu.clear();
    int i;
    for (i = 0; i < subredditNames.size(); i++) {
      subMenu.addSubMenu(0, i, Menu.NONE, subredditNames.get(i));
    }
    activity.subredditCount = i + 1;
  }
}
