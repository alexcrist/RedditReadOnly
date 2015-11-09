package com.alexcrist.redditreadonly.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.alexcrist.redditreadonly.PostExecute;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;

// this can not extend Load because it returns Void (which would break on
public class RevokeToken extends AsyncTask<String, Void, Void> {

  PostExecute post;
  RedditClient redditClient;
  Credentials creds;
  Context context;

  public RevokeToken(PostExecute post, RedditClient redditClient, Credentials creds,
                     Context context) {
    this.post = post;
    this.redditClient = redditClient;
    this.creds = creds;
    this.context = context;
  }

  @Override
  protected Void doInBackground(String... strings) {
    redditClient.getOAuthHelper().revokeAccessToken(creds);
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    redditClient.deauthenticate();
    SharedPreferences prefs = context.getSharedPreferences("main", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.remove("token");
    editor.apply();
    post.onPostExecute();
  }
}
