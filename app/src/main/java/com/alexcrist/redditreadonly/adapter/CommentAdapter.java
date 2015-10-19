package com.alexcrist.redditreadonly.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alexcrist.redditreadonly.R;
import com.alexcrist.redditreadonly.model.MyComment;
import com.alexcrist.redditreadonly.util.Text;

import net.dean.jraw.models.Submission;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<MyComment> {

  private final Context context;
  private final int resource;
  private final List<MyComment> comments;
  private final Submission submission;
  private String[] depthColors = { "#880e4f", "#4a148c", "#1a237e", "#1b5e20" };

  // Constructors
  // -----------------------------------------------------------------------------------------------

  public CommentAdapter(Context context, int resource, List<MyComment> comments,
                        Submission submission) {
    super(context, resource, comments);

    this.context = context;
    this.resource = resource;
    this.comments = comments;
    this.submission = submission;
  }

  // Return view of each ListView entry
  // -----------------------------------------------------------------------------------------------

  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(resource, parent, false);

      holder = new ViewHolder();
      holder.score = (TextView) convertView.findViewById(R.id.score);
      holder.author = (TextView) convertView.findViewById(R.id.author);
      holder.body = (TextView) convertView.findViewById(R.id.body);
      holder.spacer = convertView.findViewById(R.id.spacer);
      holder.marker = convertView.findViewById(R.id.marker);
      holder.depthWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
          context.getResources().getDisplayMetrics());

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    MyComment comment = comments.get(pos);
    if (comment != null) {
      String score = Integer.toString(comment.score) + " - ";
      score = comment.edited ? "*" + score : score;

      holder.score.setText(score);
      holder.author.setText(comment.author);
      holder.body.setText(Text.format(comment.body));

      if (comment.author.equals(submission.getAuthor())) {
        holder.author.setTextColor(Color.parseColor("#004593"));
      } else {
        holder.author.setTextColor(Color.parseColor("#666666"));
      }

      holder.spacer.getLayoutParams().width = (int) (comment.depth * holder.depthWidth);
      holder.marker.setBackgroundColor(Color.parseColor(depthColors[comment.depth]));
    }

    return convertView;
  }

  // Reusable view holder to reduce memory cost
  // -----------------------------------------------------------------------------------------------

  private static class ViewHolder {
    TextView score;
    TextView author;
    TextView body;
    View spacer;
    View marker;

    float depthWidth;
  }
}
