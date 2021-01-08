package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.CheckinLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinLogObject {
    private Timestamp timestamp;
    private String message;

    public static CheckinLogObject map(CheckinLog checkinLog) {
        if (checkinLog != null) {
            return CheckinLogObject.builder()
                    .message(checkinLog.getMessage())
                    .timestamp(checkinLog.getTimestamp())
                    .build();
        }
        return null;
    }
}
