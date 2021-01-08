package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.CheckinLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinLogObject {
    private LocalDateTime timestamp;
    private String message;

    public static CheckinLogObject map(CheckinLog checkinLog) {
        if (checkinLog != null) {
            return CheckinLogObject.builder()
                    .message(checkinLog.getMessage())
                    .timestamp(checkinLog.getTimestamp().toLocalDateTime())
                    .build();
        }
        return null;
    }
}
