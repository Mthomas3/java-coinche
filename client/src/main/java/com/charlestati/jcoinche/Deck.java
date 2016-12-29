package com.charlestati.jcoinche;

import java.util.LinkedList;
import java.util.List;

class Deck {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_WHITE = "\u001B[37m";
    static final String ANSI_YELLOW = "\u001B[33m";
    private final static int DECKSIZE = 8;
    private String lastCardsDeleted = "";
    private List<String> playerCards;

    Deck() {

        this.playerCards = new LinkedList<>();
    }

    void setLastCardsDeleted(String cardsDeleted) {
        this.lastCardsDeleted = cardsDeleted;
    }

    String getLastCardsDeleted() {
        return this.lastCardsDeleted;
    }

    private int getValueOfCards(String number) {
        for (valueCards value : valueCards.values()) {
            if (value.toString().equals(number))
                return value.getValue();
        }
        return 0;
    }

    private String getValueOfCardsHigher(String num) {
        for (valueCardsHigh value : valueCardsHigh.values()) {
            if (value.toString().equals(num)) {
                return changePrintString(value.getValue());
            }
        }
        return "";
    }

    private String getLine(int value, String suit) {
        String[] splitted = suit.split("\\s+");

        if (value == 0) {
            //while (suit.length() < 14)
            //  suit += " ";
            return this.formatStringToColor(ANSI_BLUE, suit);
        } else if (value == 1)
            return ("┌────────────┐");
        else if (value == 2) {
            if (getValueOfCards(splitted[0]) > 0 && getValueOfCards(splitted[0]) <= 9)
                return String.format("│%d           │", getValueOfCards(splitted[0]));
            else if (getValueOfCards(splitted[0]) == 10)
                return String.format("│%d          │", getValueOfCards(splitted[0]));
            else
                return String.format("│%s           │", getValueOfCardsHigher(splitted[0]));
        } else if (value == 6) {
            return String.format("│      %s     │", getValueOfCardsHigher(splitted[1]));
        } else if (value == 10) {
            if (getValueOfCards(splitted[0]) > 0 && getValueOfCards(splitted[0]) <= 9)
                return String.format("│           %d│", getValueOfCards(splitted[0]));
            else if (getValueOfCards(splitted[0]) == 10)
                return String.format("│          %d│", getValueOfCards(splitted[0]));
            else
                return String.format("│           %s│", getValueOfCardsHigher(splitted[0]));
        } else if (value == 11)
            return "└────────────┘";
        return "│            │";
    }

    void printFormatDeckCards() {

        int _start = 0;

        for (int i = 0; i < 12; ++i) {
            String printResult = "";
            int j;
            for (j = 0; j < this.playerCards.size() && j < 4; ++j) {

                printResult += getLine(i, this.playerCards.get(j)).concat("\t");
            }
            _start = j;
            System.out.println(printResult);
        }
        System.out.print("\n");
        for (int i = 0; i < 12; ++i) {
            String printResult = "";
            for (int j = _start; j < this.playerCards.size() && j < 8; ++j) {

                printResult += getLine(i, this.playerCards.get(j)).concat("\t");
            }
            System.out.println(printResult);
        }
    }

    void deleteCardFromDeck(String card) {

        if (this.playerCards.contains(card)) {

            for (int i = 0; i < this.playerCards.size(); ++i) {

                if (card.equals(this.playerCards.get(i))) {
                    this.playerCards.remove(i);
                }

            }

        }
    }

    int getDecksize() {
        return DECKSIZE;
    }

    void setPlayerCards(String cardsName) {
        this.playerCards.add(cardsName);
    }

    List<String> getPlayerCards() {
        return this.playerCards;
    }

    String formatStringToColor(String color, String content) {
        return color + content + ANSI_RESET;
    }

    private String changePrintString(String symb) {
        switch (symb) {
            case "♥":
                return this.formatStringToColor(ANSI_RED, "♥");
            case "♠":
                return this.formatStringToColor(ANSI_WHITE, "♠");
            case "♣":
                return this.formatStringToColor(ANSI_WHITE, "♣");
            case "♦":
                return this.formatStringToColor(ANSI_RED, "♦");
        }
        return symb;
    }

}
