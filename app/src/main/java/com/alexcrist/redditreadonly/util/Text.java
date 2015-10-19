package com.alexcrist.redditreadonly.util;

import java.util.ArrayList;
import java.util.List;

public class Text {

  // Static utility methods
  // -----------------------------------------------------------------------------------------------

  public static String format(String str) {
    str = str.replace("&amp;", "&");
    str = str.replace("&gt;", ">");
    str = str.replace("&lt;", "<");
    str = str.replace("&nbsp", " ");
    return str;
  }

  public static List<String> extractLinks(String str) {
    List<String> links = new ArrayList<>();
    for (int i = 0; i < str.length() - 4; i++) {
      if (str.substring(i, i + 4).equals("http")) {
        for (int j = i + 4; j < str.length(); j++) {
          if (" )\n\r\t".contains(str.charAt(j) + "")) {
            links.add(str.substring(i, j));
            break;
          } else if (j == str.length() - 1) {
            links.add(str.substring(i, j + 1));
            break;
          }
        }
      }
    }
    return links;
  }
}
