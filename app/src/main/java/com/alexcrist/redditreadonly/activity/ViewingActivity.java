package com.alexcrist.redditreadonly.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alexcrist.redditreadonly.R;
import com.gc.materialdesign.views.ProgressBarDeterminate;

@SuppressLint("SetJavaScriptEnabled")
public class ViewingActivity extends Activity {

  private WebView mWebView;

  // Initialization
  // ----------------------------------------------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewing);

    final ProgressBarDeterminate progressBar =
        (ProgressBarDeterminate) findViewById(R.id.progressBar);

    mWebView = (WebView) findViewById(R.id.webView);
    mWebView.setWebChromeClient(new WebChromeClient() {
      public void onProgressChanged(WebView view, int progress) {
        if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
          progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        progressBar.setProgress(progress);
        if (progress == 100) {
          progressBar.setVisibility(ProgressBar.GONE);
        }
      }
    });

    mWebView.getSettings().setLoadWithOverviewMode(true);
    mWebView.getSettings().setUseWideViewPort(true);
    mWebView.getSettings().setSupportZoom(true);
    mWebView.getSettings().setBuiltInZoomControls(true);
    mWebView.getSettings().setDisplayZoomControls(false);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
      }
    });

    String url = getIntent().getStringExtra("url");
    Log.i("url", url);
    mWebView.loadUrl(url);
  }

  // Pause webView (videos and such) when pausing activity
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPause() {
    super.onPause();
    mWebView.onPause();
  }
}
