package com.charlestati.jcoinche;

import java.util.HashMap;
import java.util.Map;

class ManagePlayer {
    private Map<Action, Runnable> commands = new HashMap<>();
    private final PlayersAction player = new PlayersAction();

    ManagePlayer() {
        commands.put(Action.TAKE, () -> {
            try {
                player.doTake();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        commands.put(Action.HELP, player::doHelp);
        commands.put(Action.PRINT, player::doPrint);
        commands.put(Action.PASS, player::doPass);
        commands.put(Action.CARD, player::playCard);
    }

    Map<Action, Runnable> getCommands() {
        return this.commands;
    }

    void setMessage(String message, Game game) {
        this.player.setMessage(message, game);
    }

    static CardValue getCardValues(String message) throws Exception {
        for (CardValue a : CardValue.values()) {
            if (message.toUpperCase().startsWith(a.toString())) {
                return a;
            }
        }
        throw new Exception();
    }

    static Suit getSuitValues(String message) throws Exception {
        for (Suit a : Suit.values()) {
            if (message.toUpperCase().startsWith(a.toString())) {
                return a;
            }
        }
        throw new Exception();
    }

    static Trump getTrump(String message) throws Exception {
        for (Trump a : Trump.values()) {
            if (message.toUpperCase().startsWith(a.toString())) {
                return a;
            }
        }
        throw new Exception();
    }

    static Action getAction(String message) {
        for (Action a : Action.values()) {
            if (message.toUpperCase().startsWith(a.toString())) {
                return a;
            }
        }
        return Action.UNDEFINED;
    }

}
