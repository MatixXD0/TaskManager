package pl.kul.taskmanagerclient.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private List<TaskDto> tasks;
}
