package com.charlestati.jcoinche;

enum valueCardsHigh {

    JACK("J"), QUEEN("Q"), KING("K"), ACE("A"), HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");


    private String value;

    valueCardsHigh(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
