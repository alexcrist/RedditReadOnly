package com.alexcrist.redditreadonly;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alexcrist.redditreadonly.loader.RefreshToken;
import com.alexcrist.redditreadonly.loader.UserChallengeTask;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthHelper;

import java.net.URL;

public class MyApplication extends Application {

  private RedditClient redditClient;
  private final Credentials creds = Credentials.installedApp("Ro5hyBovsBCewA", "http://blank.org");
  private final UserAgent userAgent = UserAgent.of("android", "com.alexcrist.redditreadonly", "0.1",
      "RedditReadOnly");

  // Initialization
  // -----------------------------------------------------------------------------------------------

  @Override
  public void onCreate() {
    super.onCreate();
    redditClient = new RedditClient(userAgent);
  }

  // Authentication
  // -----------------------------------------------------------------------------------------------

  public void authenticate(WebView webView, final PostExecute post) {
    final OAuthHelper helper = redditClient.getOAuthHelper();
    String[] scopes = { "identity", "read" };
    URL authUrl = helper.getAuthorizationUrl(creds, true, true, scopes);
    webView.setVisibility(View.VISIBLE);
    webView.loadUrl(authUrl.toExternalForm());
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (url.contains("code=")) {
          new UserChallengeTask(post, helper, creds, redditClient, MyApplication.this).execute(url);
        }
      }
    });
  }

  public void reauthenticate(final PostExecute post) {
    try {
      redditClient.getOAuthHelper().setRefreshToken(getToken());
      new RefreshToken(post, creds, redditClient, this).execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void saveToken() {
    SharedPreferences prefs = getSharedPreferences("main", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("token", redditClient.getOAuthHelper().getRefreshToken());
    editor.apply();
  }

  // Getters and setters
  // -----------------------------------------------------------------------------------------------

  public RedditClient getRedditClient() {
    return redditClient;
  }

  public String getToken() {
    SharedPreferences prefs = getSharedPreferences("main", MODE_PRIVATE);
    return prefs.getString("token", null);
  }
}
