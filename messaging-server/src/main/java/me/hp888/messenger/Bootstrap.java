package me.hp888.messenger;

/**
 * @author hp888 on 07.11.2020.
 */

public final class Bootstrap {

    public static void main(String[] args) {
        final Thread thread = new Thread(MessengerServer.getInstance()::start);
        thread.setName("Server thread");
        thread.start();
    }

}