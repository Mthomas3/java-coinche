package com.charlestati.jcoinche;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

class PlayerManager {
    private final HashMap<io.netty.channel.ChannelHandlerContext, Player> players;

    PlayerManager() {
        players = new HashMap<>();
    }

    void addPlayer(Player player) {
        players.put(player.getCtx(), player);
    }

    void removePlayer(Player player) {
        players.remove(player.getCtx());
    }

    Player getPlayer(ChannelHandlerContext ctx) {
        return players.get(ctx);
    }

    int size() {
        return players.size();
    }
}
