package com.alexcrist.redditreadonly.model;

import net.dean.jraw.models.Comment;

public class MyComment {

  public String body;
  public String author;
  public int score;
  public int depth;
  public boolean edited;

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public MyComment(String body, String author, int score, int depth, boolean edited) {
    this.body = body;
    this.author = author;
    this.score = score;
    this.depth = depth;
    this.edited = edited;
  }

  public MyComment(Comment comment, int depth) {
    this(comment.getBody(), comment.getAuthor(), comment.getScore(), depth,
        comment.hasBeenEdited());
  }
}
