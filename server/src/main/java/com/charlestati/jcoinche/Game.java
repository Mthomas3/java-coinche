package com.charlestati.jcoinche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

class Game {
    private static final int MIN_CONTRACT_VALUE = 80;
    private static final int CONTRACT_MULTIPLE = 10;
    private final Deck deck;
    private final ArrayList<Player> players;
    private Player waitingFor;
    private Player firstPlayer;
    private Player highestBidder;
    private Trump currentTrump;
    private Suit currentSuit;
    private ArrayList<Card> firstTeamCards;
    private ArrayList<Card> secondTeamCards;
    private int firstTeamScore;
    private int secondTeamScore;

    Game(ArrayList<Player> players) {
        this.players = players;
        Collections.shuffle(this.players);
        deck = new Deck();
        firstTeamCards = new ArrayList<>();
        secondTeamCards = new ArrayList<>();
        firstTeamScore = 0;
        secondTeamScore = 0;
    }

    void start() {
        if (players.size() > 3) {
            players.get(0).sendMessage(Protocol.formatSendTeam(players.get(2).getUsername()));
            players.get(2).sendMessage(Protocol.formatSendTeam(players.get(0).getUsername()));

            players.get(1).sendMessage(Protocol.formatSendTeam(players.get(3).getUsername()));
            players.get(3).sendMessage(Protocol.formatSendTeam(players.get(1).getUsername()));
        }

        newGame(players.get(0));
    }

    private void newGame(Player firstPlayer) {
        sendToAll(Protocol.formatBroadcastGameStart());
        this.firstPlayer = firstPlayer;
        highestBidder = null;
        currentTrump = null;
        currentSuit = null;

        for (Player p : players) {
            p.setContract(null);
        }

        deck.refill();
        deck.shuffle();
        dealCards();
        askBid(firstPlayer);
    }

    private void newRound(Player firstPlayer) {
        sendToAll(Protocol.formatBroadcastNewRound());
        this.firstPlayer = firstPlayer;
        currentSuit = null;
        firstTeamCards.clear();
        secondTeamCards.clear();
        deck.refill();
        deck.shuffle();
        dealCards();
        askBid(firstPlayer);
    }

    private void dealCards() {
        int cardsPerPlayer = deck.size() / players.size();

        for (Player player : players) {
            for (int i = 0; i < cardsPerPlayer; ++i) {
                player.addCard(deck.pullCard());
            }
        }
    }

    private void askBid(Player player) {
        waitingFor = player;
        player.askBid();
    }

    private void askCard(Player player) {
        waitingFor = player;
        player.askCard();
    }

    void handlePass(Player player) {
        if (player == waitingFor) {
            sendToOthers(Protocol.formatBroadcastPass(player.getUsername()), player);
            if (isLastPlayer(player)) {
                if (highestBidder == null) {
                    newGame(players.get(0));
                } else {
                    currentTrump = highestBidder.getContract().getTrump();
                    Player nextPlayer = getNextPlayer(players.get(0));
                    firstPlayer = nextPlayer;
                    askCard(nextPlayer);
                }
            } else {
                Player nextPlayer = getNextPlayer(player);
                askBid(nextPlayer);
            }
        }
    }

    void handleBid(Contract contract, Player player) {
        if (player == waitingFor) {
            if (contractIsValid(contract) && (highestBidder == null || contract
                    .compareTo(highestBidder.getContract()) > 0)) {
                sendToOthers(Protocol.formatBroadcastBid(player.getUsername(), contract), player);
                player.setContract(contract);
                highestBidder = player;
                firstPlayer = player;
                Player nextPlayer = getNextPlayer(player);
                askBid(nextPlayer);
            } else {
                player.warn(Warning.INVALID_BID);
            }
        }
    }

    void handleCard(Card card, Player player) {
        if (player == waitingFor) {
            if (canPlayCard(card, player)) {
                sendToOthers(Protocol.formatBroadcastCard(player.getUsername(), card), player);
                if (currentSuit == null) {
                    currentSuit = card.getSuit();
                }
                player.playCard(card);
                if (isLastPlayer(player)) {
                    Player winner = getWinner();
                    sendToAll(Protocol.formatBroadcastWin(winner.getUsername()));
                    ArrayList<Card> winnersCards = players.indexOf(winner) % 2 != 0 ? firstTeamCards : secondTeamCards;
                    winnersCards.addAll(players.stream().map(Player::getPlayedCard).collect(Collectors.toList()));
                    if (firstTeamCards.size() + secondTeamCards.size() == deck.getInitialSize()) {
                        countPoints();
                        newRound(winner);
                    } else {
                        currentSuit = null;
                        firstPlayer = winner;
                        askCard(winner);
                    }
                } else {
                    Player nextPlayer = getNextPlayer(player);
                    askCard(nextPlayer);
                }
            } else {
                player.warn(Warning.INVALID_CARD);
            }
        }
    }

    boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    private Player getWinner() {
        Player winner = players.get(0);
        for (Player p : players) {
            if (p.getPlayedCard().isBetterThan(winner.getPlayedCard(), currentSuit, currentTrump)) {
                winner = p;
            }
        }
        return winner;
    }

    private void countPoints() {
        int firstTeamPoints = 0;
        int secondTeamPoints = 0;

        boolean firstTeamHasTheContract = players.indexOf(highestBidder) % 2 == 0;

        for (Card c : firstTeamCards) {
            firstTeamPoints += getPointsValue(c, currentTrump);
        }

        for (Card c : secondTeamCards) {
            secondTeamPoints += getPointsValue(c, currentTrump);
        }

        if (firstTeamHasTheContract) {
            if (firstTeamPoints > highestBidder.getContract().getValue()) {
                firstTeamPoints += highestBidder.getContract().getValue();
            } else {
                firstTeamPoints = 0;
                secondTeamPoints = 160 + highestBidder.getContract().getValue();
            }
        } else {
            if (secondTeamPoints > highestBidder.getContract().getValue()) {
                secondTeamPoints += highestBidder.getContract().getValue();
            } else {
                secondTeamPoints = 0;
                firstTeamPoints = 160 + highestBidder.getContract().getValue();
            }
        }

        firstTeamScore += firstTeamPoints;
        secondTeamScore += secondTeamPoints;

        sendToAll(Protocol.formatBroadcastScore(players.get(0).getUsername(), players.get(2).getUsername(),
                firstTeamPoints, firstTeamScore));
        sendToAll(Protocol.formatBroadcastScore(players.get(1).getUsername(), players.get(3).getUsername(),
                secondTeamPoints, secondTeamScore));
    }

    private int getPointsValue(Card c, Trump currentTrump) {
        if (c.getSuit().toString().equals(currentTrump.toString())) {
            return c.trumpValue();
        } else {
            return c.regularValue();
        }
    }

    private boolean canPlayCard(Card card, Player player) {
        if (!player.hasCard(card)) {
            return false;
        }

        if (currentSuit == null) {
            return true;
        }

        if (card.getSuit() == currentSuit) {
            return true;
        }

        if (card.getSuit() != currentSuit && player.hasSuit(currentSuit)) {
            return false;
        }

        if (!player.hasSuit(currentSuit) && card.getSuit().toString().equals(currentTrump.toString())) {
            return true;
        }

        if (!player.hasSuit(currentSuit) && !player.hasTrump(currentTrump)) {
            return true;
        }

        return false;
    }

    private void sendToOthers(String msg, Player player) {
        players.stream().filter(p -> p != player).forEach(p -> p.sendMessage(msg));
    }

    private void sendToAll(String msg) {
        for (Player p : players) {
            p.sendMessage(msg);
        }
    }

    private boolean isLastPlayer(Player player) {
        if (firstPlayer != null) {
            int i = players.indexOf(firstPlayer);
            return player == players.get(i > 0 ? i - 1 : players.size() - 1);
        } else {
            return players.indexOf(player) == players.size() - 1;
        }
    }

    private Player getNextPlayer(Player player) {
        return players.get(players.indexOf(player) == players.size() - 1 ? 0 : players.indexOf(player) + 1);
    }

    private boolean contractIsValid(Contract contract) {
        return contract.getValue() >= MIN_CONTRACT_VALUE && contract.getValue() % CONTRACT_MULTIPLE == 0;
    }

    void stop() {
        // todo Put back in the waiting room instead
        players.forEach(Player::disconnect);
    }
}
