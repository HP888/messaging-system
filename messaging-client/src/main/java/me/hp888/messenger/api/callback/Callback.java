package me.hp888.messenger.api.callback;

import me.hp888.messenger.api.packet.Packet;

/**
 * @author hp888 on 07.11.2020.
 */

public interface Callback<T extends Packet> {

    void done(T packet);

    void error(String message);

}