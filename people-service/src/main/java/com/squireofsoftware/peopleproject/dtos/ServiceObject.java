package com.squireofsoftware.peopleproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceObject {
    private int id;
    private Date timestamp;
    private List<ServiceComponent> components;
}
