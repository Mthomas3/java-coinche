package com.charlestati.jcoinche;

class Contract implements Comparable<Contract> {
    private final int value;
    private final Trump trump;

    Contract(int value, Trump trump) {
        this.value = value;
        this.trump = trump;
    }

    public String toString() {
        return Integer.toString(this.value).concat(" ").concat(this.trump.toString());
    }

    @Override
    public int compareTo(Contract o) {
        return Integer.compare(this.value, o.value);
    }

    int getValue() {
        return value;
    }

    Trump getTrump() {
        return trump;
    }
}
