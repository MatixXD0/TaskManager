package pl.kul.taskmanagerclient.dto;

import lombok.Data;
import pl.kul.taskmanagerclient.enums.Priority;
import pl.kul.taskmanagerclient.enums.Status;

import java.time.LocalDate;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
    private ProjectDto project;
}
