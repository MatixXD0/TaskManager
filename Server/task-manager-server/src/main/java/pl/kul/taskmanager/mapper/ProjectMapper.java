package pl.kul.taskmanager.mapper;

import pl.kul.taskmanager.dto.ProjectRequestDTO;
import pl.kul.taskmanager.dto.ProjectResponseDTO;
import pl.kul.taskmanager.model.Project;

import java.util.stream.Collectors;

public class ProjectMapper {

    public static Project toEntity(ProjectRequestDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return project;
    }

    public static ProjectResponseDTO toDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setTasks(project.getTasks().stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }
}
