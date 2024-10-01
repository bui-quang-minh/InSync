package com.in_sync.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProjectDtos {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddProjectDto{
        private String projectName ;
        private String description ;
        private String  userIdClerk;
        private Boolean isPublish ;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UpdateProjectDto{
        private UUID id;
        private String projectName ;
        private String description ;
        private Boolean isPublish ;
    }
}
