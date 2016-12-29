package com.charlestati.jcoinche;

class Card {
    private final Suit suit;
    private final CardValue value;

    Card(Suit suit, CardValue value) {
        this.suit = suit;
        this.value = value;
    }

    public String toString() {
        return value.toString().concat(" ").concat(suit.toString());
    }

    Suit getSuit() {
        return suit;
    }

    CardValue getValue() {
        return value;
    }

    boolean isBetterThan(Card other, Suit currentSuit, Trump trump) {
        if (this == other) {
            return false;
        }

        if (this.getSuit() == currentSuit && other.getSuit() != currentSuit) {
            return true;
        } else if (this.getSuit() != currentSuit && other.getSuit() == currentSuit) {
            return false;
        }

        int thisValue = this.suit.toString().equals(trump.toString()) ? this.trumpValue() : this.regularValue();
        int otherValue = other.getSuit().toString().equals(trump.toString()) ? other.trumpValue() : other
                .regularValue();

        return thisValue > otherValue;
    }

    int trumpValue() {
        switch (value) {
            case SEVEN:
                return 0;
            case EIGHT:
                return 0;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case TEN:
                return 10;
            case ACE:
                return 11;
            case NINE:
                return 14;
            case JACK:
                return 20;
            default:
                return 0;
        }
    }

    int regularValue() {
        switch (value) {
            case SEVEN:
                return 0;
            case EIGHT:
                return 0;
            case NINE:
                return 0;
            case JACK:
                return 2;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case TEN:
                return 10;
            case ACE:
                return 11;
            default:
                return 0;
        }
    }
}
