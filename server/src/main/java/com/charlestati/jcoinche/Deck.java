package com.charlestati.jcoinche;

import java.util.ArrayList;
import java.util.Collections;

class Deck {
    private final ArrayList<Card> cards;
    private int initialSize;

    Deck() {
        cards = new ArrayList<>();
        refill();
    }

    void refill() {
        cards.clear();
        for (Suit s : Suit.values()) {
            for (CardValue cv : CardValue.values()) {
                cards.add(new Card(s, cv));
            }
        }
        initialSize = cards.size();
    }

    void shuffle() {
        Collections.shuffle(cards);
    }

    Card pullCard() {
        return cards.remove(0);
    }

    int size() {
        return cards.size();
    }

    int getInitialSize() {
        return initialSize;
    }

    ArrayList<Card> getCards() {
        return cards;
    }
}
