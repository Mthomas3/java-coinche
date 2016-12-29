package com.charlestati.jcoinche;

import java.util.Arrays;

class Protocol {
    static String formatSendUsername(String username) {
        return Message.S_USERNAME.toString().concat(" ").concat(username);
    }

    static String formatSendOnlinePlayers(int nbPlayers) {
        return Message.S_ONLINE_PLAYERS.toString().concat(" ").concat(Integer.toString(nbPlayers));
    }

    static String formatSendCard(Card card) {
        return Message.S_CARD.toString().concat(" ").concat(card.toString());
    }

    static String formatSendBid(int value, Trump trump) {
        return Message.S_BID.toString().concat(" ").concat(Integer.toString(value)).concat(" ")
                .concat(trump.toString());
    }

    static String formatSendPass() {
        return Message.S_PASS.toString();
    }

    static String formatSendTeam(String username) {
        return Message.S_TEAM.toString().concat(" ").concat(username);
    }

    static String formatSendCoinche() {
        return Message.S_COINCHE.toString();
    }

    static String formatSendSurcoinche() {
        return Message.S_SURCOINCHE.toString();
    }

    static String formatAskBid() {
        return Message.A_BID.toString();
    }

    static String formatAskCard() {
        return Message.A_CARD.toString();
    }

    static String formatBroadcastPass(String username) {
        return Message.B_PASS.toString().concat(" ").concat(username);
    }

    static String formatBroadcastNewRound() {
        return Message.B_NEW_ROUND.toString();
    }

    static String formatBroadcastBid(String username, Contract contract) {
        return Message.B_BID.toString().concat(" ").concat(username).concat(" ").concat(contract.toString());
    }

    static String formatBroadcastGameStart() {
        return Message.B_GAME_START.toString();
    }

    static String formatBroadcastCard(String username, Card card) {
        return Message.B_CARD.toString().concat(" ").concat(username).concat(" ").concat(card.toString());
    }

    static String formatBroadcastWin(String username) {
        return Message.B_WIN.toString().concat(" ").concat(username);
    }

    static boolean messageIsValid(String msg) {
        String[] splitted = msg.split("\\s+");

        for (Message m : Message.values()) {
            if (m.toString().equals(splitted[0])) {
                return true;
            }
        }

        return false;
    }

    static Message getMessage(String msg) throws Exception {
        String[] splitted = msg.split("\\s+");

        for (Message m : Message.values()) {
            if (m.toString().equals(splitted[0])) {
                return m;
            }
        }

        throw new Exception();
    }

    static String[] getMessageContent(String msg) throws Exception {
        String[] splitted = msg.split("\\s+");

        for (Message m : Message.values()) {
            if (m.toString().equals(splitted[0])) {
                return Arrays.copyOfRange(splitted, 1, splitted.length);
            }
        }

        throw new Exception();
    }

    static String formatBroadcastScore(String username1, String username2, int points, int total) {
        return Message.B_SCORE.toString().concat(" ").concat(username1).concat(" ").concat(username2).concat(" ")
                .concat(Integer.toString(points)).concat(" ").concat(Integer.toString(total));
    }
}
