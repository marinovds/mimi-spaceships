package com.dam.demo.model;

public record Boundary(
    boolean left,
    boolean right,
    boolean top,
    boolean bottom
) {
}