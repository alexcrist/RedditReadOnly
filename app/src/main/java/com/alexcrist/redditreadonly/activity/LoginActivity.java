package com.alexcrist.redditreadonly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.alexcrist.redditreadonly.MyApplication;
import com.alexcrist.redditreadonly.PostExecute;
import com.alexcrist.redditreadonly.R;

public class LoginActivity extends AppCompatActivity implements PostExecute {

  // Initialization
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    String token = ((MyApplication) this.getApplication()).getToken();
    if (token == null) {
      Log.i("test", "1");
      WebView webView = (WebView) findViewById(R.id.webView);
      ((MyApplication) this.getApplication()).authenticate(webView, this);
    } else {
      Log.i("test", "2");
      ((MyApplication) this.getApplication()).reauthenticate(this);
    }
  }

  @Override
  public void onPostExecute() {
    Intent intent = new Intent(getApplicationContext(), BrowseActivity.class);
    startActivity(intent);
  }
}
