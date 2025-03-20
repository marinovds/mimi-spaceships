package com.dam.demo.model.attack;

import java.time.Duration;

public record Shot(Damage damage, int speed, Duration cooldown) { }