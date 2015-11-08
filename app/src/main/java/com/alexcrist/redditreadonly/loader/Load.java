package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.PostExecute;

public abstract class Load<T> extends AsyncTask<String, Void, T> {

  private PostExecute post;
  private int attempts;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public Load(PostExecute post) {
    this.post = post;
    this.attempts = 0;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected T doInBackground(String... params) {
    return onLoad();
  }

  protected abstract T onLoad(String... params);

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPostExecute(T t) {
    if (t == null) {
      if (attempts < 3) {
        attempts++;
        execute();
      }
    } else {
      onPost(t);
      post.onPostExecute();
    }
  }

  protected abstract void onPost(T t);
}
