package com.demo.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {
    private UUID id;
    private String name;
    private UUID organizationId;
    private String organizationName;
}