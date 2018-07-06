package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageObject {
    private String message;
    private boolean isSuccessful;

    public MessageObject(boolean isSuccessful, String message){
        this.isSuccessful = isSuccessful;
        this.message = message;
    }
}
