package com.dam.deal;

import static com.dam.deal.Rules.ROUNDS;
import static com.dam.template.Placeholder.placeholder;
import static com.dam.util.BoxUtil.chooseBox;
import static com.dam.util.BoxUtil.nonOpened;
import static com.dam.util.RandomUtil.Option.option;
import static com.dam.util.Util.pause;
import static com.dam.util.Visualizer.message;
import static com.dam.util.Visualizer.requestInt;

import com.dam.util.RandomUtil;
import java.util.Comparator;
import java.util.List;

public enum Offers {
  ;

  public static GameSetup offer(GameSetup game, List<Box> available, int round) {
    var result = RandomUtil.weighted(
        option(3, () -> average(game, available, -30, round)),
        option(4, () -> average(game, available, 0, round)),
        option(1, () -> average(game, available, 20, round)),
        option(2, () -> swap(game, available))
    );
    pause(1);

    return result;
  }

  private static GameSetup average(GameSetup game, List<Box> available, int delta, int round) {
    var average = nonOpened(available, Box::amount)
        .stream()
        .sorted(Comparator.reverseOrder())
        .mapToDouble(x -> x)
        .average()
        .orElse(0);

    var offer = average + (delta / 100D * average);
    message("offer.amount", placeholder("banker.offer", offer));
    pause(1);
    RandomUtil.weighted(
        option(3, () -> null),
        option(2, () -> {
          if (ROUNDS.size() - 1 == round) {
            return null;
          }
          message("offer.boxes.next", placeholder("box.next", ROUNDS.get(round)));
          return null;
        })
    );
    pause(2);

    if (game.accepted() != null) {
      message("offer.accepted.amount", placeholder("player.accepted", game.accepted()));
      pause(1);
      message("offer.accepted.re");
      var message = dealTaken() ? "offer.accepted.re.taken" : "offer.accepted.re.rejected";
      message(message,
          placeholder("player.name", game.player().name()),
          placeholder("offer.amount", offer)
      );

      return new GameSetup(
          game.player(),
          available,
          game.accepted()
      );
    }

    return new GameSetup(
        game.player(),
        available,
        dealTaken() ? offer : null
    );

  }

  private static GameSetup swap(GameSetup game, List<Box> available) {
    message("offer.swap");
    if (!dealTaken()) {

      return new GameSetup(
          game.player(),
          available,
          game.accepted()
      );
    }
    message("offer.deal.taken");
    var player = game.player();
    var chosen = chooseBox(available, player.box());
    message("offer.swap.outcome",
        placeholder("player.box", player.box().number()),
        placeholder("player.box.new", chosen.number())
    );

    return new GameSetup(
        new Player(player.name(), player.city(), chosen),
        available,
        game.accepted()
    );
  }

  private static boolean dealTaken() {
    while (true) {
      message("offer.deal");
      var choice = requestInt();
      if (choice == 1 || choice == 2) {

        return choice == 1;
      }
      message("offer.deal.error");
    }
  }

}
