package com.charlestati.jcoinche;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

class WaitingRoom {
    private static final int PLAYERS_PER_GAME = 4;
    private final GameManager gameManager;
    private final LinkedHashSet<Player> players;

    WaitingRoom(GameManager gameManager) {
        this.gameManager = gameManager;
        players = new LinkedHashSet<>();
    }

    void addPlayer(Player player) {
        players.add(player);
        startMatchMaking();
    }

    void removePlayer(Player player) {
        gameManager.removePlayer(player);
        players.remove(player);
    }

    private void startMatchMaking() {
        while (players.size() >= PLAYERS_PER_GAME) {
            Iterator<Player> iterator = players.iterator();
            ArrayList<Player> playersToPull = new ArrayList<>(PLAYERS_PER_GAME);

            for (int i = 0; i < PLAYERS_PER_GAME; ++i) {
                playersToPull.add(iterator.next());
            }

            players.removeAll(playersToPull);
            gameManager.createGame(playersToPull);
        }
    }
}
