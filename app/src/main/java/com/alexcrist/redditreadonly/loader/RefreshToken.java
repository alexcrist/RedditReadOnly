package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

public class RefreshToken extends AsyncTask<String, Void, OAuthData> {

  private Credentials creds;
  private RedditClient redditClient;
  private MyApplication application;
  private PostExecute post;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public RefreshToken(Credentials creds, RedditClient redditClient, MyApplication application,
                      PostExecute post) {
    this.creds = creds;
    this.redditClient = redditClient;
    this.application = application;
    this.post = post;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected OAuthData doInBackground(String... params) {
    try {
      return redditClient.getOAuthHelper().refreshToken(creds);
    } catch (NetworkException | OAuthException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPostExecute(OAuthData oAuthData) {
    redditClient.authenticate(oAuthData);
    application.saveToken();
    post.onPostExecute();
  }
}
