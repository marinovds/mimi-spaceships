package com.dam.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.dam.template.Language;
import com.dam.template.Placeholder;
import java.io.PrintWriter;
import java.util.Scanner;

public class Visualizer {

  private static final PrintWriter OUT = new PrintWriter(System.out, true, UTF_8);
  private static final Scanner IN = new Scanner(System.in);

  public static Language language;

  public static void message(String msg, Placeholder... params) {
    var translation = language.get(msg, params);
    OUT.println(translation);
  }

  public static String requestString() {
    return IN.nextLine();
  }

  public static int requestInt() {
    var value = IN.nextInt();
    IN.nextLine();
    return value;
  }
}
