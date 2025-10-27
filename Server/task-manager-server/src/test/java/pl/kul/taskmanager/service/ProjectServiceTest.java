package pl.kul.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.kul.taskmanager.dto.ProjectRequestDTO;
import pl.kul.taskmanager.dto.ProjectResponseDTO;
import pl.kul.taskmanager.exception.ProjectNotFoundException;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Project;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.model.Task;
import pl.kul.taskmanager.repository.ProjectRepository;
import pl.kul.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project sampleProject;
    private ProjectRequestDTO sampleProjectRequest;

    @BeforeEach
    void setUp() {
        sampleProject = new Project(1L, "Sample Project", "Sample Description", Collections.emptyList());
        sampleProjectRequest = new ProjectRequestDTO("Sample Project", "Sample Description");
    }

    @Test
    void should_createProject_when_validRequest() {
        // given
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        // when
        ProjectResponseDTO response = projectService.createProject(sampleProjectRequest);

        // then
        assertNotNull(response);
        assertEquals(sampleProject.getId(), response.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void should_getAllProjects_when_projectsExist() {
        // given
        when(projectRepository.findAll()).thenReturn(List.of(sampleProject));

        // when
        List<ProjectResponseDTO> response = projectService.getAllProjects();

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(sampleProject.getId(), response.getFirst().getId());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void should_getProjectById_when_projectExists() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));

        // when
        ProjectResponseDTO response = projectService.getProjectById(1L);

        // then
        assertNotNull(response);
        assertEquals(sampleProject.getId(), response.getId());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void should_throwException_when_getProjectByIdAndProjectDoesNotExist() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(1L));

        // then
        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void should_updateProject_when_projectExists() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        // when
        ProjectResponseDTO response = projectService.updateProject(1L, sampleProjectRequest);

        // then
        assertNotNull(response);
        assertEquals(sampleProject.getId(), response.getId());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void should_deleteProject_when_projectExists() {
        // given
        when(projectRepository.existsById(1L)).thenReturn(true);

        // when
        projectService.deleteProject(1L);

        // then
        verify(projectRepository, times(1)).existsById(1L);
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_throwException_when_deleteProjectAndProjectDoesNotExist() {
        // given
        when(projectRepository.existsById(1L)).thenReturn(false);

        // when
        Exception exception = assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(1L));

        // then
        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, times(1)).existsById(1L);
    }

    @Test
    void should_searchProjects_when_criteriaProvided() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(sampleProject), pageable, 1);
        when(projectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(projectPage);

        // when
        Page<ProjectResponseDTO> response = projectService.searchProjects(null, "Sample", null, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(sampleProject.getId(), response.getContent().getFirst().getId());
        verify(projectRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void should_searchProjects_when_idProvided() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(sampleProject), pageable, 1);
        when(projectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(projectPage);

        // when
        Page<ProjectResponseDTO> response = projectService.searchProjects(sampleProject.getId(), null, null, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(sampleProject.getId(), response.getContent().getFirst().getId());
        verify(projectRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    void should_searchProjects_when_descriptionProvided() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(sampleProject), pageable, 1);
        when(projectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(projectPage);

        // when
        Page<ProjectResponseDTO> response = projectService.searchProjects(null, null, "Sample Description", pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(sampleProject.getDescription(), response.getContent().getFirst().getDescription());
        verify(projectRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    void should_addTaskToProject_when_validIds() {
        // given
        Task sampleTask = new Task(1L, "Sample Task", "Task Description", Priority.HIGH, Status.TODO, LocalDate.now(), null);
        sampleProject.setTasks(new ArrayList<>());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        // when
        ProjectResponseDTO response = projectService.addTaskToProject(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(1, response.getTasks().size());
        assertEquals("Sample Task", response.getTasks().getFirst().getName());
        verify(projectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }


    @Test
    void should_removeTaskFromProject_when_validIds() {
        // given
        Task sampleTask = new Task(1L, "Sample Task", "Task Description", Priority.HIGH, Status.TODO, LocalDate.now(), sampleProject);
        sampleProject.setTasks(new ArrayList<>(List.of(sampleTask))); // Ustaw modyfikowalną listę
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        // when
        ProjectResponseDTO response = projectService.removeTaskFromProject(1L, 1L);

        // then
        assertNotNull(response);
        assertTrue(response.getTasks().isEmpty());
        verify(projectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }


    @Test
    void should_throwException_when_addTaskToNonExistentProject() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(ProjectNotFoundException.class, () -> projectService.addTaskToProject(1L, 1L));

        // then
        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
        verify(taskRepository, never()).findById(anyLong());
    }

    @Test
    void should_throwException_when_removeTaskNotInProject() {
        // given
        Task sampleTask = new Task(1L, "Sample Task", "Task Description", Priority.HIGH, Status.TODO, LocalDate.now(), null);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectService.removeTaskFromProject(1L, 1L));

        // then
        assertEquals("Task is not assigned to this project.", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findById(1L);
    }




}
