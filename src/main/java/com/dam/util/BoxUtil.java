package com.dam.util;

import static com.dam.template.Placeholder.placeholder;
import static com.dam.util.RandomUtil.Option.option;
import static com.dam.util.RandomUtil.RANDOM;
import static com.dam.util.RandomUtil.weighted;
import static com.dam.util.Util.format;
import static com.dam.util.Visualizer.message;
import static com.dam.util.Visualizer.requestInt;

import com.dam.deal.Box;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum BoxUtil {
  ;

  public static <T> List<T> nonOpened(List<Box> boxes, Function<Box, T> f) {
    return boxes.stream()
        .filter(x -> !x.opened())
        .map(f)
        .sorted()
        .toList();
  }

  public static Box findBox(List<Box> boxes, Predicate<Box> f) {
    return boxes.stream()
        .filter(f)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Box not found"));
  }

  public static Box chooseBox(List<Box> boxes, Box playerBox) {
    var numbers = nonOpened(boxes, Box::number)
        .stream()
        .filter(x -> x != playerBox.number())
        .map(String::valueOf)
        .toList();

    while (true) {
      message("available.numbers", placeholder("available", format(numbers)));
      var choice = requestInt();
      if (numbers.contains(String.valueOf(choice))) {
        if (!keepBox(boxes)) {
          continue;
        }

        return findBox(boxes, x -> x.number() == choice);
      }
      message("available.error");
    }
  }

  private static boolean keepBox(List<Box> boxes) {
    var notOpened = nonOpened(boxes, Box::amount);
    if (notOpened.size() == 2) {
      // this is the last round. These flow does not mean much...
      return false;
    }

    return weighted(
        option(3, chooseOther(() -> message("support.high"))),
        option(2, chooseOther(() -> message("support.low"))),
        option(3, chooseOther(() -> message("support.neutral"))),
        option(3, chooseOther(() -> {
          var pick = RANDOM.nextInt(notOpened.size());
          message("support.exact", placeholder("box.amount", notOpened.get(pick)));
        })),
        option(20, () -> true)
    );
  }

  private static Supplier<Boolean> chooseOther(Effect e) {
    return () -> {
      e.operation();
      while (true) {
        message("support.other");
        var choice = requestInt();
        if (choice == 1 || choice == 2) {

          return choice == 1;
        }
        message("offer.deal.error");
      }
    };
  }

  @FunctionalInterface
  private interface Effect {

    void operation();
  }
}
