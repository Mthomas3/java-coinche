package com.charlestati.jcoinche;

import io.netty.channel.ChannelHandlerContext;

public class Server {
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 84;
    private final NetworkManager networkManager;
    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final WaitingRoom waitingRoom;

    Server() {
        networkManager = new NetworkManager(this);
        gameManager = new GameManager();
        waitingRoom = new WaitingRoom(gameManager);
        playerManager = new PlayerManager();
    }

    void start(int port) throws InterruptedException {
        networkManager.startListening(port);
    }

    void newConnection(ChannelHandlerContext ctx) {
        playerManager.addPlayer(new Player(ctx));
    }

    void disconnected(ChannelHandlerContext ctx) {
        removePlayer(ctx);
    }

    void newMessage(ChannelHandlerContext ctx, String msg) {
        Player player = playerManager.getPlayer(ctx);
        if (player != null) {
            if (Protocol.messageIsValid(msg)) {
                try {
                    Message m = Protocol.getMessage(msg);
                    String[] content = Protocol.getMessageContent(msg);
                    handleAction(m, content, player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                player.warn(Warning.INVALID_MESSAGE);
            }
        }
    }

    private void handleAction(Message m, String[] content, Player player) {
        switch (m) {
            case S_USERNAME:
                player.setUsername(content[0]);
                player.sendMessage(Protocol.formatSendOnlinePlayers(playerManager.size()));
                waitingRoom.addPlayer(player);
                break;
            case S_PASS:
                gameManager.handlePass(player);
                break;
            case S_BID:
                Contract contract = new Contract(Integer.parseInt(content[0]), Trump.valueOf(content[1]));
                gameManager.handleBid(contract, player);
                break;
            case S_CARD:
                Card card = new Card(Suit.valueOf(content[1]), CardValue.valueOf(content[0]));
                gameManager.handleCard(card, player);
                break;
            default:
                player.warn(Warning.INVALID_MESSAGE);
        }
    }

    void channelExceptionCaught(ChannelHandlerContext ctx) {
        removePlayer(ctx);
    }

    private void removePlayer(ChannelHandlerContext ctx) {
        Player player = playerManager.getPlayer(ctx);
        playerManager.removePlayer(player);
        waitingRoom.removePlayer(player);
        gameManager.removePlayer(player);
        player.disconnect();
    }

    public static void main(String[] args) {
        String port = args.length > 0 ? args[0] : System.getProperty("port", "8080");
        try {
            new Server().start(Integer.parseInt(port));
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
            System.exit(EXIT_SUCCESS);
        } catch (Exception e) {
            System.err.println("An unknown error occured");
            System.exit(EXIT_FAILURE);
        }
    }
}
