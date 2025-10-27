package pl.kul.taskmanager.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kul.taskmanager.dto.ProjectRequestDTO;
import pl.kul.taskmanager.dto.ProjectResponseDTO;
import pl.kul.taskmanager.exception.ProjectNotFoundException;
import pl.kul.taskmanager.exception.TaskNotFoundException;
import pl.kul.taskmanager.mapper.ProjectMapper;
import pl.kul.taskmanager.model.Project;
import pl.kul.taskmanager.model.Task;
import pl.kul.taskmanager.repository.ProjectRepository;
import pl.kul.taskmanager.repository.TaskRepository;
import pl.kul.taskmanager.specification.ProjectSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = mapAndValidateProject(dto);
        Project saved = saveProject(project);
        return ProjectMapper.toDTO(saved);
    }

    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO getProjectById(Long id) {
        Project project = findProjectById(id);
        return ProjectMapper.toDTO(project);
    }

    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto) {
        Project project = findProjectById(id);
        updateProjectDetails(project, dto);
        Project savedProject = saveProject(project);
        return ProjectMapper.toDTO(savedProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException(id);
        }
        projectRepository.deleteById(id);
    }

    public Page<ProjectResponseDTO> searchProjects(Long id, String name, String description, Pageable pageable) {
        Specification<Project> spec = Specification.where(null);

        if (id != null) {
            spec = spec.and(ProjectSpecification.hasId(id));
        }

        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and(ProjectSpecification.nameContains(name));
        }

        if (description != null && !description.trim().isEmpty()) {
            spec = spec.and(ProjectSpecification.descriptionContains(description));
        }

        Page<Project> projects = projectRepository.findAll(spec, pageable);
        return projects.map(ProjectMapper::toDTO);
    }

    @Transactional
    public ProjectResponseDTO addTaskToProject(Long projectId, Long taskId) {
        Project project = findProjectById(projectId);
        Task task = findTaskById(taskId);

        assignTaskToProject(task, project);
        saveTaskAndProject(task, project);

        return ProjectMapper.toDTO(project);
    }

    @Transactional
    public ProjectResponseDTO removeTaskFromProject(Long projectId, Long taskId) {
        Project project = findProjectById(projectId);
        Task task = findTaskById(taskId);

        validateTaskAssignmentToProject(task, project);
        unassignTaskFromProject(task, project);
        saveTaskAndProject(task, project);

        return ProjectMapper.toDTO(project);
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void updateProjectDetails(Project project, ProjectRequestDTO dto) {
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
    }

    private void assignTaskToProject(Task task, Project project) {
        task.setProject(project);
        project.getTasks().add(task);
    }

    private void unassignTaskFromProject(Task task, Project project) {
        task.setProject(null);
        project.getTasks().remove(task);
    }

    private void validateTaskAssignmentToProject(Task task, Project project) {
        if (task.getProject() == null || !task.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Task is not assigned to this project.");
        }
    }

    private Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    private void saveTaskAndProject(Task task, Project project) {
        taskRepository.save(task);
        projectRepository.save(project);
    }

    private Project mapAndValidateProject(ProjectRequestDTO dto) {
        return ProjectMapper.toEntity(dto);
    }
}