package pl.kul.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import pl.kul.taskmanager.dto.ProjectRequestDTO;
import pl.kul.taskmanager.dto.ProjectResponseDTO;
import pl.kul.taskmanager.service.ProjectService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void should_createProject_when_validRequest() {
        // given
        ProjectRequestDTO requestDTO = new ProjectRequestDTO("Project Name", "Project Description");
        ProjectResponseDTO responseDTO = new ProjectResponseDTO(1L, "Project Name", "Project Description", List.of());
        when(projectService.createProject(requestDTO)).thenReturn(responseDTO);

        // when
        ResponseEntity<ProjectResponseDTO> response = projectController.createProject(requestDTO);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(projectService, times(1)).createProject(requestDTO);
    }

    @Test
    void should_getAllProjects() {
        // given
        List<ProjectResponseDTO> projects = List.of(new ProjectResponseDTO(1L, "Project 1", "Description 1", List.of()));
        when(projectService.getAllProjects()).thenReturn(projects);

        // when
        ResponseEntity<List<ProjectResponseDTO>> response = projectController.getAllProjects();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(projects, response.getBody());
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void should_getProjectById_when_projectExists() {
        // given
        ProjectResponseDTO project = new ProjectResponseDTO(1L, "Project 1", "Description 1", List.of());
        when(projectService.getProjectById(1L)).thenReturn(project);

        // when
        ResponseEntity<ProjectResponseDTO> response = projectController.getProjectById(1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(project, response.getBody());
        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void should_updateProject_when_validRequest() {
        // given
        ProjectRequestDTO requestDTO = new ProjectRequestDTO("Updated Name", "Updated Description");
        ProjectResponseDTO responseDTO = new ProjectResponseDTO(1L, "Updated Name", "Updated Description", List.of());
        when(projectService.updateProject(1L, requestDTO)).thenReturn(responseDTO);

        // when
        ResponseEntity<ProjectResponseDTO> response = projectController.updateProject(1L, requestDTO);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(projectService, times(1)).updateProject(1L, requestDTO);
    }

    @Test
    void should_deleteProject_when_projectExists() {
        // when
        ResponseEntity<Void> response = projectController.deleteProject(1L);

        // then
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    void should_searchProjects_when_criteriaProvided() {
        // given
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<ProjectResponseDTO> projectsPage = new PageImpl<>(
                List.of(new ProjectResponseDTO(1L, "Project 1", "Description 1", List.of())),
                pageRequest,
                1
        );
        when(projectService.searchProjects(null, "Project", null, pageRequest)).thenReturn(projectsPage);

        // when
        ResponseEntity<Page<ProjectResponseDTO>> response = projectController.searchProjects(
                null,
                "Project",
                null,
                0,
                10,
                new String[] {"id", "asc"}
        );

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(projectsPage, response.getBody());
        verify(projectService, times(1)).searchProjects(null, "Project", null, pageRequest);
    }


    @Test
    void should_addTaskToProject_when_validIds() {
        // given
        ProjectResponseDTO responseDTO = new ProjectResponseDTO(1L, "Project 1", "Description 1", List.of());
        when(projectService.addTaskToProject(1L, 1L)).thenReturn(responseDTO);

        // when
        ResponseEntity<ProjectResponseDTO> response = projectController.addTaskToProject(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(projectService, times(1)).addTaskToProject(1L, 1L);
    }

    @Test
    void should_removeTaskFromProject_when_validIds() {
        // given
        ProjectResponseDTO responseDTO = new ProjectResponseDTO(1L, "Project 1", "Description 1", List.of());
        when(projectService.removeTaskFromProject(1L, 1L)).thenReturn(responseDTO);

        // when
        ResponseEntity<ProjectResponseDTO> response = projectController.removeTaskFromProject(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(projectService, times(1)).removeTaskFromProject(1L, 1L);
    }

    @Test
    void should_throwException_when_getProjectByIdAndProjectDoesNotExist() {
        // given
        when(projectService.getProjectById(999L)).thenThrow(new RuntimeException("Project not found"));

        // when
        Exception exception = assertThrows(RuntimeException.class, () -> projectController.getProjectById(999L));

        // then
        assertEquals("Project not found", exception.getMessage());
        verify(projectService, times(1)).getProjectById(999L);
    }

    @Test
    void should_throwException_when_deleteNonExistentProject() {
        // given
        doThrow(new RuntimeException("Project not found")).when(projectService).deleteProject(999L);

        // when
        Exception exception = assertThrows(RuntimeException.class, () -> projectController.deleteProject(999L));

        // then
        assertEquals("Project not found", exception.getMessage());
        verify(projectService, times(1)).deleteProject(999L);
    }

    @Test
    void should_returnEmptyList_when_noProjectsExist() {
        // given
        when(projectService.getAllProjects()).thenReturn(List.of());

        // when
        ResponseEntity<List<ProjectResponseDTO>> response = projectController.getAllProjects();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void should_throwException_when_updateProjectWithInvalidData() {
        // given
        ProjectRequestDTO invalidRequest = new ProjectRequestDTO("", ""); // Invalid data
        doThrow(new IllegalArgumentException("Invalid project data")).when(projectService).updateProject(eq(1L), eq(invalidRequest));

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectController.updateProject(1L, invalidRequest));

        // then
        assertEquals("Invalid project data", exception.getMessage());
        verify(projectService, times(1)).updateProject(eq(1L), eq(invalidRequest));
    }

    @Test
    void should_throwException_when_addTaskToNonExistentProject() {
        // given
        doThrow(new RuntimeException("Project or Task not found")).when(projectService).addTaskToProject(999L, 888L);

        // when
        Exception exception = assertThrows(RuntimeException.class, () -> projectController.addTaskToProject(999L, 888L));

        // then
        assertEquals("Project or Task not found", exception.getMessage());
        verify(projectService, times(1)).addTaskToProject(999L, 888L);
    }

    @Test
    void should_throwException_when_removeTaskNotInProject() {
        // given
        doThrow(new IllegalArgumentException("Task not assigned to this project"))
                .when(projectService).removeTaskFromProject(1L, 999L);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectController.removeTaskFromProject(1L, 999L));

        // then
        assertEquals("Task not assigned to this project", exception.getMessage());
        verify(projectService, times(1)).removeTaskFromProject(1L, 999L);
    }

    @Test
    void should_returnEmptyPage_when_noProjectsMatchSearchCriteria() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<ProjectResponseDTO> emptyPage = Page.empty(pageRequest);
        when(projectService.searchProjects(null, "NonExistentName", null, pageRequest)).thenReturn(emptyPage);

        // when
        ResponseEntity<Page<ProjectResponseDTO>> response = projectController.searchProjects(
                null, "NonExistentName", null, 0, 10, new String[]{"id", "asc"}
        );

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(projectService, times(1)).searchProjects(null, "NonExistentName", null, pageRequest);
    }


}
