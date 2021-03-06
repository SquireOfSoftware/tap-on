package com.squireofsoftware.peopleproject.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkSignInObject {
    @NotNull
    @Singular
    private List<String> hashes;
    private String message;
}
