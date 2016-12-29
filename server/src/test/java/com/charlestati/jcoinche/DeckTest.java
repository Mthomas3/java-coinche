package com.charlestati.jcoinche;

import org.junit.Test;

public class DeckTest {
    @Test
    public void checkSize() throws Exception {
        Deck deck = new Deck();
        if (deck.size() != 32) {
            throw new Exception();
        }
    }

    @Test
    public void checkShuffle() throws Exception {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck2.shuffle();
        int similarities = 0;
        for (int i = 0; i < deck1.size(); ++i) {
            if (deck1.getCards().get(i) == deck2.getCards().get(i)) {
                ++similarities;
            }
        }

        if (similarities == deck1.size()) {
            throw new Exception();
        }
    }
}
