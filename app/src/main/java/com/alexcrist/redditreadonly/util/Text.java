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
    return str;
  }

  public static List<String> extractLinks(String str) {
    List<String> links = new ArrayList<>();
    for (int i = 0; i < str.length() - 4; i++) {
      if (str.substring(i, i + 4).equals("http")) {
        for (int j = i + 4; j < str.length(); j++) {
          if (str.charAt(j) == ' ' || str.charAt(j) == ')') {
            links.add(str.substring(i, j));
            break;
          } else if (j < str.length() - 1) {
            if (str.substring(j, j + 2).equals("\n")) {
              links.add(str.substring(i, j));
              break;
            }
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
