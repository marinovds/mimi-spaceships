package com.dam.demo.util;

import java.util.List;
import java.util.function.Function;

public enum LangUtil {
  ;

  public static <T, V> List<V> map(List<T> list, Function<T, V> f) {
    return list.stream().map(f).toList();
  }

  public static <V, T> T mapNull(V value, Function<V, T> f) {
    return value == null ? null : f.apply(value);
  }

  public static int clamp(int base, int min, int max) {
    return Math.min(Math.max(min, base), max);
  }
}
