package com.charlestati.jcoinche;

enum valueCards {
    SEVEN(7), EIGHT(8), NINE(9), TEN(10);

    private int value;

    valueCards(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

