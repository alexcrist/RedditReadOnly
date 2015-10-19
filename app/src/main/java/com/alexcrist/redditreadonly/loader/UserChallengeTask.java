package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

public class UserChallengeTask extends AsyncTask<String, Void, OAuthData> {

  private OAuthHelper helper;
  private Credentials creds;
  private RedditClient redditClient;
  private MyApplication application;
  private PostExecute post;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public UserChallengeTask(OAuthHelper helper, Credentials creds, RedditClient redditClient,
                           MyApplication application, PostExecute post) {
    this.helper = helper;
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
      return helper.onUserChallenge(params[0], creds);
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
