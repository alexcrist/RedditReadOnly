package com.alexcrist.redditreadonly.loader;

import android.os.AsyncTask;

import com.alexcrist.redditreadonly.PostExecute;

public abstract class Load<T> extends AsyncTask<String, Void, T> {

  private PostExecute post;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public Load(PostExecute post) {
    this.post = post;
  }

  // Do this task on background thread
  // -----------------------------------------------------------------------------------------------

  @Override
  protected T doInBackground(String... params) {
    return onLoad(params);
  }

  protected abstract T onLoad(String... params);

  // Do this after executing task
  // -----------------------------------------------------------------------------------------------

  @Override
  protected void onPostExecute(T t) {
    if (t != null) {
      onPost(t);
      if (post != null) {
        post.onPostExecute();
      }
    }
  }

  protected abstract void onPost(T t);
}
