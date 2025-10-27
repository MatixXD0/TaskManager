package pl.kul.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<TaskResponseDTO> tasks;
}
