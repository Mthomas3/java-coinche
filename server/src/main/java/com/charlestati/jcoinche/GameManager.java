package com.charlestati.jcoinche;

import java.util.ArrayList;
import java.util.HashSet;

class GameManager {
    private final HashSet<Game> games;

    GameManager() {
        games = new HashSet<>();
    }

    void removePlayer(Player player) {
        games.stream().filter(g -> g.hasPlayer(player)).forEach(Game::stop);
    }

    void createGame(ArrayList<Player> players) {
        Game game = new Game(players);
        games.add(game);
        game.start();
    }

    void handlePass(Player player) {
        try {
            Game game = getGame(player);
            game.handlePass(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleBid(Contract contract, Player player) {
        try {
            Game game = getGame(player);
            game.handleBid(contract, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleCard(Card card, Player player) {
        try {
            Game game = getGame(player);
            game.handleCard(card, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Game getGame(Player player) throws Exception {
        for (Game g : games) {
            if (g.hasPlayer(player)) {
                return g;
            }
        }

        throw new Exception();
    }
}
