package com.charlestati.jcoinche;

class Players {
    private static final int ROOMNUMBER = 4;
    private Deck deck;
    private int contrat = 0;

    Players() {
        this.deck = new Deck();
    }

    int getContrat() {
        return this.contrat;
    }

    void setCONTRAT(String message) throws Exception {
        String[] splitted = message.split("\\s+");
        if (splitted.length > 3) {
            System.out.println(this.getDeck()
                    .formatStringToColor(Deck.ANSI_YELLOW, splitted[1]) + " took " + splitted[2] + " " + splitted[3]);
            try {
                this.contrat = Integer.parseInt(splitted[2]);
            } catch (Exception ignored) {
            }
        }
    }

    void showPass(String message) {
        String[] splitted = message.split("\\s+");
        if (splitted.length > 1) {
            System.out.println(this.getDeck().formatStringToColor(Deck.ANSI_YELLOW, splitted[1]) + " passed");
        }
    }

    int getRoomnumber() {
        return ROOMNUMBER;
    }

    Deck getDeck() {
        return this.deck;
    }
}
