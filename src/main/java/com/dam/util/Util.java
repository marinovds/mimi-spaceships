package com.dam.util;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Util {

  private static final DecimalFormat NUMBER_FORMATTER = numberFormatter();

  private Util() {
    throw new UnsupportedOperationException("Utility classes initialization");
  }

  private static DecimalFormat numberFormatter() {
    var symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator(' ');

    return new DecimalFormat("###,###,##0.##", symbols);
  }

  public static String format(List<String> list) {
    var result = new StringBuilder();
    var padding = 4 + list.stream()
        .map(String::length)
        .max(Comparator.naturalOrder())
        .orElseThrow(() -> new IllegalStateException("Cannot format empty list"));

    for (int i = 0; i < list.size() - 1; i = i + 2) {
      result.append(list.get(i))
          .append(" ".repeat(padding - list.get(i).length()))
          .append(list.get(i + 1))
          .append('\n');
    }
    if (list.size() % 2 != 0) {
      result.append(list.get(list.size() - 1))
          .append('\n');
    }

    return result.toString();
  }

  public static void pause(int seconds) {
    try {
      Thread.sleep(seconds * 1_000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static List<String> readResource(String resourceName) {
    var resource = Util.class.getResourceAsStream("/" + resourceName);
    if (resource == null) {
      throw new IllegalStateException("Resource '" + resourceName + "' does not exist");
    }
    var result = new ArrayList<String>();
    try (Scanner scanner = new Scanner(resource, StandardCharsets.UTF_8)) {
      while (scanner.hasNextLine()) {
        result.add(scanner.nextLine());

      }
      return result;
    }
  }

  public static String numberFormat(Number num) {
    return NUMBER_FORMATTER.format(num);
  }

}
