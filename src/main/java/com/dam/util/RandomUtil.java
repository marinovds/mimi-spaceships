package com.dam.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public enum RandomUtil {
  ;

  public static final Random RANDOM = new Random();

  public static <T> T formallyDistributed(List<T> items) {
    var pick = RANDOM.nextInt(items.size());

    return items.get(pick);
  }

  public static <T extends Weighted> T weighted(List<T> items) {
    var sum = items.stream()
        .mapToInt(Weighted::getWeight)
        .sum();
    var sorted = items.stream()
        .sorted(Comparator.comparingInt(Weighted::getWeight))
        .toList();
    var pick = RANDOM.nextInt(sum);

    var accumulation = 0;
    for (T item : sorted) {
      if (pick >= accumulation && pick < accumulation + item.getWeight()) {
        return item;
      }
      accumulation += item.getWeight();
    }

    throw new IllegalStateException("Nonexistent entity");
  }

  public static <T> T weighted(Option<T>... options) {
    var opts = Arrays.asList(options);
    var pick = weighted(opts);

    return pick.function().get();
  }

  public interface Weighted {

    int getWeight();
  }

  public record Option<T>(int weight, Supplier<T> function) implements Weighted {

    public static <T> Option<T> option(int weight, Supplier<T> f) {
      return new Option<>(weight, f);
    }

    @Override
    public int getWeight() {
      return weight;
    }
  }
}
