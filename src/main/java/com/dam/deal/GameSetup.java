package com.dam.deal;

import java.util.List;

public record GameSetup(
    Player player,
    List<Box> boxes,
    Double accepted) {
}
