package com.charlestati.jcoinche;

import java.util.Objects;

class Game {
    private Client client;
    private ManagePlayer managePlayer;
    private boolean askContract = false;
    private boolean playerCard = false;
    private Players players;

    private static final int EXIT_FAILURE = 84;

    Game(String host, String port) {
        this.client = new Client(host, Integer.parseInt(port), this);
        try {
            this.getNameFromPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(EXIT_FAILURE);
        }
        this.managePlayer = new ManagePlayer();
        this.managePlayer.getCommands();
    }

    ManagePlayer getManagePlayer() {
        return this.managePlayer;
    }

    Client getClient() {
        return this.client;
    }

    private void getNameFromPlayer() throws Exception {
        System.out.println("Hi, please enter your name:");

        while (true) {
            String nickname = this.client.getMessageFromPlayer();
            if (nickname.startsWith("\t") || nickname.startsWith(" ") || nickname.length() < 3 || nickname
                    .equals("You")) {
                System.out.println("\"" + nickname + "\"" + " is not a valid nickname. Try again:");
            } else {
                this.players = new Players();
                this.client.sendMessageToserver(Protocol.formatSendUsername(nickname));
                break;
            }
        }
    }

    Players getPlayers() {
        return this.players;
    }

    private void handleMessageFromPlayer(String message) throws Exception {

        if (this.managePlayer.getCommands().containsKey(ManagePlayer.getAction(message))) {
            this.managePlayer.setMessage(message, this);
            this.managePlayer.getCommands().get(ManagePlayer.getAction(message)).run();
        } else if (this.playerCard && message.length() > 2) {
            if (this.players.getDeck().getPlayerCards().contains(message)) {
                this.managePlayer.setMessage(message, this);
                this.managePlayer.getCommands().get(ManagePlayer.getAction("CARD")).run();
            } else {
                System.out.println("You card is not valid, you can check your deck with \"print\".");
            }

        } else if (message.length() > 1 && !this.playerCard) {
            System.out.println("Invalid command, type Help to see all available commands.");
        }
    }

    private void saveCards(String cards) throws Exception {
        String[] result = Protocol.getMessageContent(cards);
        if (result.length > 1)
            this.players.getDeck().setPlayerCards(result[0].concat(" ").concat((result[1])));
        else
            this.players.getDeck().setPlayerCards("INVALID CARD");

        if (this.players.getDeck().getPlayerCards().size() == this.players.getDeck().getDecksize()) {
            System.out.println("Here are your cards : \n");

            this.getPlayers().getDeck().printFormatDeckCards();
        }
    }

    void run() throws InterruptedException {
        try {
            while (true) {
                String message = this.client.getMessageFromPlayer();
                if (message == null)
                    break;
                else {
                    if (message.equals("quit")) {
                        //this.getClient().getChannel().closeFuture().sync();
                        break;
                    }
                    this.handleMessageFromPlayer(message);
                }
            }
            this.client.SyncWritting();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("error run fonction");
            System.exit(EXIT_FAILURE);
        } finally {
            this.client.getGroup().shutdownGracefully();
        }
    }

    private void sendPlayersOnline(String message) throws Exception {
        for (String value : Protocol.getMessageContent(message)) {
            if (Integer.parseInt(value) < this.players.getRoomnumber())
                System.out.println(
                        this.players.getRoomnumber() - (Integer
                                .parseInt(value)) + " players have to join before starting the game!");
            else if (Integer.parseInt(value) == this.players.getRoomnumber())
                System.out.println("The game is ready to start!");
        }
    }

    void setContract(boolean bool) {
        this.askContract = bool;
    }

    boolean getContract() {
        return this.askContract;
    }

    private void askContract() throws Exception {
        System.out.println("It's your turn to take or pass:");
        this.askContract = true;
    }

    private void showStartGame() {
        //this.players.getDeck().getPlayerCards().clear();
        System.out.println("The game is starting now!");
    }

    void setPlayerCard(boolean bool) {
        this.playerCard = bool;
    }

    boolean getPlayerCard() {
        return this.playerCard;
    }

    private void playerCard() {
        System.out.println("You can play a card:");
        this.playerCard = true;
    }

    private void showCardsToPlayers(String message) throws Exception {
        String value[] = Protocol.getMessageContent(message);

        if (value.length > 2) {
            System.out.println(
                    this.players.getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[0]) + " played " + this.players
                            .getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[1] + " " + value[2]));
        }
    }

    private void showNewRound() {
        System.out.println("New round!");
    }

    private void showTheWinner(String message) throws Exception {
        String value[] = Protocol.getMessageContent(message);
        System.out.println(this.players.getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[0]) + " won.");
    }

    private void showMate(String message) throws Exception {
        String value[] = Protocol.getMessageContent(message);
        System.out.println(
                "You are playing with " + this.players.getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[0]));
    }

    private void showScore(String message) throws Exception {
        String value[] = Protocol.getMessageContent(message);

        System.out.println(
                this.players.getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[0]) + " and " + this.players
                        .getDeck().formatStringToColor(Deck.ANSI_YELLOW, value[1]) + " won " + this.players.getDeck()
                        .formatStringToColor(Deck.ANSI_YELLOW,
                                value[2]) + " points and their total score is " + this.players.getDeck()
                        .formatStringToColor(Deck.ANSI_YELLOW, value[3]));
    }

    void handlerMsg(String message) throws Exception {
        // System.out.println("(from serveur)-> " + this.serverMessage + " TO -> " + this.players.getName());
        switch (Protocol.getMessage(message)) {
            case S_USERNAME:
                break;
            case S_ONLINE_PLAYERS:
                this.sendPlayersOnline(message);
                break;
            case S_CARD:
                this.saveCards(message);
                break;
            case B_BID:
                this.players.setCONTRAT(message);
                break;
            case A_BID:
                this.askContract();
                break;
            case S_PASS:
                break;
            case S_TEAM:
                this.showMate(message);
                break;
            case S_BID:
                break;
            case S_COINCHE:
                break;
            case S_SURCOINCHE:
                break;
            case B_PASS:
                this.players.showPass(message);
                break;
            case B_GAME_START:
                this.showStartGame();
                break;
            case A_CARD:
                this.playerCard();
                break;
            case B_NEW_ROUND:
                this.showNewRound();
                break;
            case B_CARD:
                this.showCardsToPlayers(message);
                break;
            case B_WIN:
                this.showTheWinner(message);
                break;
            case B_SCORE:
                this.showScore(message);
                break;
            case INVALID_CARD:
                System.out.println("You cannot play that card.");
                this.playerCard();
                if (!Objects.equals(this.players.getDeck().getLastCardsDeleted(), ""))
                    this.players.getDeck().setPlayerCards(this.players.getDeck().getLastCardsDeleted());
                break;
        }
    }
}
