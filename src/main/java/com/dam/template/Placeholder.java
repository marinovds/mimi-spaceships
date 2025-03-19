package com.dam.template;

public record Placeholder(String name, Object value) {

  public static Placeholder placeholder(String name, Object value) {
    return new Placeholder(name, value);
  }
}
