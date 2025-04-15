package com.dam.demo.model.menu;

@FunctionalInterface
public interface MenuAction {

  MenuAction NO_ON = () -> {};

  void select();

}