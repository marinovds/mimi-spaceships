package com.dam.demo.enemies;

import com.dam.demo.controls.behaviour.movement.MovementBehaviour;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.SpaceshipDefinition;
import java.util.function.Function;

public record EnemyDef(
    SpaceshipDefinition spaceship,
    SpawnCriteria spawn,
    Function<Spaceship, MovementBehaviour> movement) {

}
