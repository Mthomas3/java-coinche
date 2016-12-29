package com.charlestati.jcoinche;

class PlayersAction {
    private String message;
    private final static int MINCONTRACT = 80;
    private final static int MAXCONTRACT = 160;
    private Game game;

    void doPass() {
        if (this.game.getContract()) {
            System.out.println("You passed");
            this.game.setContract(false);
            this.game.getClient().sendMessageToserver(Protocol.formatSendPass());
        } else
            System.out.println("You cannot pass right now");
    }

    void setMessage(String message, Game game) {
        this.message = message;
        this.game = game;
    }

    void doHelp() {
        System.out.println("The available commands are: ");
        this.game.getManagePlayer().getCommands().forEach((key, value) -> System.out.print(key + " "));
        System.out.println("\nEnd commands.");
    }

    void doPrint() {
        System.out.println(
                "You have " + this.game.getPlayers().getDeck().getPlayerCards().size() + " cards in your deck : \n");
        this.game.getPlayers().getDeck().printFormatDeckCards();
        System.out.print("\n");
    }

    private String handleFailure(int numberFailure) {
        if (numberFailure < MINCONTRACT)
            return "The minimum number required is : " + MINCONTRACT;
        else if (numberFailure > MAXCONTRACT)
            return "The maximum number allowed is : " + MAXCONTRACT;
        else if (numberFailure <= this.game.getPlayers().getContrat())
            return "Your number has to be higher than the last player (" + this.game.getPlayers().getContrat() + ")";
        else if (!(numberFailure % 10 == 0))
            return "Your number has to be a multiple of ten";
        return "Unknown error";
    }

    void doTake() throws Exception {
        if (this.game.getContract()) {
            System.out.println("What contract do you want? (NUMBER + TRUMP)");
            while (true) {
                String reply = this.game.getClient().getMessageFromPlayer();
                if (reply.toLowerCase().startsWith("pass")) {
                    this.doPass();
                    break;
                }
                String[] splitted = reply.split("\\s+");
                try {
                    if (Integer.parseInt(splitted[0]) >= MINCONTRACT && Integer
                            .parseInt(splitted[0]) <= MAXCONTRACT && Integer.parseInt(splitted[0]) > this.game
                            .getPlayers().getContrat() && (Integer.parseInt(splitted[0]) % 10 == 0)) {
                        try {
                            this.game.getClient()
                                    .sendMessageToserver(Protocol.formatSendBid(Integer.parseInt(splitted[0]),
                                            ManagePlayer.getTrump(splitted[1])));
                            System.out.println("Contract accepted.");
                            this.game.setContract(false);
                            break;
                        } catch (Exception e) {
                            System.out.println("The trump is not valid.");
                        }
                    } else
                        System.out.println(handleFailure(Integer.parseInt(splitted[0])));
                } catch (Exception e) {
                    System.out.println("Invalid number, (NUMBER + TRUMP).");
                }
            }
        } else {
            System.out.println("It's not your turn to bid.");
        }
    }

    void playCard() {
        String[] splitted = this.message.split("\\s+");

        if (this.game.getPlayerCard()) {
            try {
                Suit suit = ManagePlayer.getSuitValues(splitted[1]);
                CardValue val = ManagePlayer.getCardValues(splitted[0]);
                Card _card = new Card(suit, val);
                this.game.getClient().sendMessageToserver(Protocol.formatSendCard(_card));

                this.game.getPlayers().getDeck().deleteCardFromDeck(message);
                this.game.getPlayers().getDeck().setLastCardsDeleted(message);

                this.game.setPlayerCard(false);

            } catch (Exception e) {
                System.out.println("Exception caught");
            }
        } else
            System.out.println("You cannot play a card right now.");
    }
}
