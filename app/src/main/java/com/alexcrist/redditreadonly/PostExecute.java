package com.alexcrist.redditreadonly;

// can be given to Load class for simple callback after completing background task
public interface PostExecute {

  // do this after executing background task
  void onPostExecute();
}
