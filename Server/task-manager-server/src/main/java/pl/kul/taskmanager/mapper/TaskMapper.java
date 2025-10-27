package pl.kul.taskmanager.mapper;

import pl.kul.taskmanager.dto.TaskRequestDTO;
import pl.kul.taskmanager.dto.TaskResponseDTO;
import pl.kul.taskmanager.model.Task;

public class TaskMapper {

    public static Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        return task;
    }

    public static TaskResponseDTO toDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setProjectId(task.getProject() != null ? task.getProject().getId() : null);
        return dto;
    }
}
