package com.charlestati.jcoinche;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;

class Player {
    private final ChannelHandlerContext ctx;
    private Card playedCard;
    private String username = null;
    private ArrayList<Card> cards;
    private Contract contract;

    Player(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        cards = new ArrayList<>();
        playedCard = null;
    }

    ChannelHandlerContext getCtx() {
        return ctx;
    }

    void warn(Warning w) {
        sendMessage(w.toString());
    }

    void sendMessage(String msg) {
        ctx.writeAndFlush(msg.concat("\n"));
    }

    void setUsername(String username) {
        this.username = username;
    }

    void addCard(Card card) {
        cards.add(card);
        sendMessage(Protocol.formatSendCard(card));
    }

    void askBid() {
        sendMessage(Protocol.formatAskBid());
    }

    String getUsername() {
        return username;
    }

    void disconnect() {
        ctx.close();
    }

    void setContract(Contract contract) {
        this.contract = contract;
    }

    Contract getContract() {
        return contract;
    }

    void askCard() {
        sendMessage(Protocol.formatAskCard());
    }

    boolean hasCard(Card card) {
        for (Card c : cards) {
            if (c.toString().equals(card.toString())) {
                return true;
            }
        }
        return false;
    }

    boolean hasSuit(Suit suit) {
        for (Card c : cards) {
            if (c.getSuit() == suit) {
                return true;
            }
        }
        return false;
    }

    boolean hasTrump(Trump currentTrump) {
        for (Card c : cards) {
            if (c.getSuit().toString().equals(currentTrump.toString())) {
                return true;
            }
        }
        return false;
    }

    void playCard(Card card) {
        playedCard = card;

        Iterator<Card> iter = cards.iterator();
        while (iter.hasNext()) {
            Card c = iter.next();
            if (c.getSuit() == card.getSuit() && c.getValue() == card.getValue()) {
                iter.remove();
            }
        }
    }

    Card getPlayedCard() {
        return playedCard;
    }
}
