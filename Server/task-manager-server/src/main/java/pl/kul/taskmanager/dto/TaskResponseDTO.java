package pl.kul.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
}
