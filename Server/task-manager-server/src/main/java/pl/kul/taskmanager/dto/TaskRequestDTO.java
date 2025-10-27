package pl.kul.taskmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @NotNull
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Status status;

    @FutureOrPresent(message = "Due date must be in the future or present")
    private LocalDate dueDate;

    private Long projectId;
}
