package com.dam.deal;

public record Box(int number, Double amount, boolean opened) {

  public Box open() {
    return new Box(number, amount, true);
  }
}
