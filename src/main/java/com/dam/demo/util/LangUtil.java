package com.dam.demo.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum LangUtil {
  ;

  public static <T, V> List<V> map(List<T> list, Function<T, V> f) {
    return list.stream().map(f).toList();
  }

  public static <V, T> T mapNull(V value, Function<V, T> f) {
    return value == null ? null : f.apply(value);
  }

  public static <T> List<T> addToList(List<T> list, T entry) {
    return Stream.concat(list.stream(), Stream.of(entry))
        .toList();
  }

  public static <T> List<T> replace(List<T> list, T entry, Predicate<T> f) {
    return Stream.concat(list.stream().filter(f), Stream.of(entry)).toList();

  }

  public static int clamp(int base, int min, int max) {
    return Math.min(Math.max(min, base), max);
  }
}
