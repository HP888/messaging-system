package me.hp888.messenger.api.packet;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

/**
 * @author hp888 on 08.11.2020.
 */

@Getter
public abstract class CallbackPacket implements Packet {

    @Setter
    private long callbackId;
    private boolean response;
    private String errorMessage;

    public void setResponse() {
        this.response = true;
    }

    public boolean isSuccess() {
        return Objects.isNull(errorMessage);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}