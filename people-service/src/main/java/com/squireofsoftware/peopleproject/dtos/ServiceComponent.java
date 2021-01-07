package com.squireofsoftware.peopleproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceComponent {
    private int serviceId;
    private int id;
    private String type;
    private List<Integer> people;
}
