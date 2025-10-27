package pl.kul.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kul.taskmanager.dto.TaskRequestDTO;
import pl.kul.taskmanager.dto.TaskResponseDTO;
import pl.kul.taskmanager.exception.TaskNotFoundException;
import pl.kul.taskmanager.mapper.TaskMapper;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.model.Task;
import pl.kul.taskmanager.repository.TaskRepository;
import pl.kul.taskmanager.specification.TaskSpecification;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = TaskMapper.toEntity(dto);
        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(saved);
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskMapper.toDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task updatedTask = taskRepository.findById(id)
                .map(task -> {
                    task.setName(dto.getName());
                    task.setDescription(dto.getDescription());
                    task.setPriority(dto.getPriority());
                    task.setStatus(dto.getStatus());
                    task.setDueDate(dto.getDueDate());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskMapper.toDTO(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    public Page<TaskResponseDTO> searchTasks(Status status, Priority priority, Long projectId,
                                             LocalDate dueDateFrom, LocalDate dueDateTo,
                                             String search, String name, Long id, Pageable pageable) {
        Specification<Task> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and(TaskSpecification.hasStatus(status));
        }

        if (priority != null) {
            spec = spec.and(TaskSpecification.hasPriority(priority));
        }

        if (projectId != null) {
            spec = spec.and(TaskSpecification.belongsToProject(projectId));
        }

        if (dueDateFrom != null) {
            spec = spec.and(TaskSpecification.dueDateAfterOrEqual(dueDateFrom));
        }

        if (dueDateTo != null) {
            spec = spec.and(TaskSpecification.dueDateBeforeOrEqual(dueDateTo));
        }

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(
                    Specification.where(TaskSpecification.nameContains(search))
                            .or(TaskSpecification.descriptionContains(search))
            );
        }

        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and(TaskSpecification.nameContains(name));
        }

        if (id != null) {
            spec = spec.and(TaskSpecification.hasId(id));
        }

        Page<Task> tasks = taskRepository.findAll(spec, pageable);
        return tasks.map(TaskMapper::toDTO);
    }


}