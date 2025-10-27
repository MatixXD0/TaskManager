package pl.kul.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kul.taskmanager.dto.ProjectRequestDTO;
import pl.kul.taskmanager.dto.ProjectResponseDTO;
import pl.kul.taskmanager.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO dto) {
        ProjectResponseDTO created = projectService.createProject(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        ProjectResponseDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO dto) {
        ProjectResponseDTO updated = projectService.updateProject(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProjectResponseDTO>> searchProjects(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        String sortBy = "id";

        if (sort.length == 2) {
            sortBy = sort[0];
            direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProjectResponseDTO> result = projectService.searchProjects(id, name, description, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<ProjectResponseDTO> addTaskToProject(@PathVariable Long projectId, @PathVariable Long taskId) {
        ProjectResponseDTO updatedProject = projectService.addTaskToProject(projectId, taskId);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<ProjectResponseDTO> removeTaskFromProject(@PathVariable Long projectId, @PathVariable Long taskId) {
        ProjectResponseDTO updatedProject = projectService.removeTaskFromProject(projectId, taskId);
        return ResponseEntity.ok(updatedProject);
    }
}
