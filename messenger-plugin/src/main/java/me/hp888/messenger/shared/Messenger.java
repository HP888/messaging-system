package me.hp888.messenger.shared;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp888 on 09.11.2020.
 */

public final class Messenger {

    @Getter
    @Setter
    private static MessengerPlugin instance;

    private Messenger() {
    }

}