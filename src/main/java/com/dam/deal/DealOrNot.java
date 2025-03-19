package com.dam.deal;

import static com.dam.deal.Offers.offer;
import static com.dam.deal.Rules.AMOUNTS;
import static com.dam.deal.Rules.ROUNDS;
import static com.dam.template.Placeholder.placeholder;
import static com.dam.util.BoxUtil.chooseBox;
import static com.dam.util.BoxUtil.findBox;
import static com.dam.util.BoxUtil.nonOpened;
import static com.dam.util.RandomUtil.Option.option;
import static com.dam.util.RandomUtil.RANDOM;
import static com.dam.util.RandomUtil.weighted;
import static com.dam.util.Util.format;
import static com.dam.util.Util.numberFormat;
import static com.dam.util.Util.pause;
import static com.dam.util.Util.readResource;
import static com.dam.util.Visualizer.message;
import static com.dam.util.Visualizer.requestInt;
import static com.dam.util.Visualizer.requestString;

import com.dam.template.LanguageLoader;
import com.dam.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class DealOrNot {

  public static void play() {
    var locale = chooseLanguage();
    LanguageLoader.load(locale);
    var game = gameSetup(AMOUNTS);
    play(game);
  }

  private static String chooseLanguage() {
    var available = readResource("languages")
        .stream()
        .map(x -> x.replace(".ltp", ""))
        .toList();
    System.out.println(format(available));
    while (true) {
      var choice = requestInt();
      if (choice <= available.size() && choice > 0) {
        return available.get(choice - 1);
      }
      System.out.println("Language does not exist...");
    }
  }

  private static void play(GameSetup game) {
    var gameState = game;
    for (int i = 0; i < ROUNDS.size(); i++) {
      gameState = playRound(gameState, i);
    }

    var player = gameState.player();
    var amount = gameState.accepted() == null
        ? player.box().amount()
        : gameState.accepted();
    var otherBox = findBox(gameState.boxes(),
        x -> !x.opened() && x.number() != player.box().number());
    message("game.exit", placeholder("player.name", player.name()));
    pause(2);
    message("game.exit.otherBox", placeholder("box.number", otherBox.number()));
    pause(2);
    message("game.exit.otherBox.amount", placeholder("box.amount", otherBox.amount()));
    pause(1);
    message("game.exit.amount", placeholder("game.amount", amount));
    pause(1);
    message("game.exit.city", placeholder("player.city", player.city()));
    pause(1);
    message("game.exit.finale");
  }

  private static Player intro(Box box) {
    message("game.intro");
    var name = requestString();
    message("player.city");
    var city = requestString();
    message("player.box", placeholder("player.box", box.number()));
    pause(2);
    return new Player(name, city, box);
  }

  private static GameSetup playRound(GameSetup game, int round) {
    int numberOfBoxesToOpen = ROUNDS.get(round);
    var current = game.boxes();
    for (int i = 0; i < numberOfBoxesToOpen; i++) {
      current = openBox(current, game.player().box(), numberOfBoxesToOpen - i);
    }

    return offer(game, current, numberOfBoxesToOpen);
  }

  private static List<Box> openBox(List<Box> boxes, Box playerBox, int boxesLeft) {
    printAvailableAmounts(boxes);
    pause(1);
    weighted(
        option(7, () -> null),
        option(4, () -> {
          message("box.left", placeholder("box.left", boxesLeft));
          pause(1);

          return null;
        })
    );
    var box = chooseBox(boxes, playerBox);
    message("box.chosen", placeholder("box.number", box.number()),
        placeholder("box.amount", numberFormat(box.amount())));
    pause(3);
    return replace(box.open(), boxes, x -> x.number() == box.number());
  }

  private static <T> List<T> replace(T replacement, List<T> list, Predicate<T> f) {
    return list.stream()
        .map(x -> f.test(x) ? replacement : x)
        .toList();
  }

  private static void printAvailableAmounts(List<Box> boxes) {
    var amounts = nonOpened(boxes, Box::amount)
        .stream()
        .map(Util::numberFormat)
        .toList();

    message("available.amounts", placeholder("available", format(amounts)));
  }

  private static GameSetup gameSetup(List<Double> amounts) {
    var shuffled = new ArrayList<>(amounts);
    Collections.shuffle(shuffled);

    var boxNumber = RANDOM.nextInt(shuffled.size());
    var boxes = IntStream.range(0, shuffled.size())
        .mapToObj(x -> new Box(x + 1, shuffled.get(x), false))
        .toList();
    var playerBox = findBox(boxes, x -> x.number() == boxNumber + 1);

    var player = intro(playerBox);
    return new GameSetup(
        player,
        boxes,
        null
    );
  }

}
