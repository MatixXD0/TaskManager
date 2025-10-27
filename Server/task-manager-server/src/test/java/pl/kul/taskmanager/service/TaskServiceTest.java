package pl.kul.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pl.kul.taskmanager.dto.TaskRequestDTO;
import pl.kul.taskmanager.dto.TaskResponseDTO;
import pl.kul.taskmanager.exception.TaskNotFoundException;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.model.Task;
import pl.kul.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDTO taskRequestDTO;

    @BeforeEach
    void setUp() {
        task = new Task(
                1L,
                "Test Task",
                "This is a test task",
                Priority.MEDIUM,
                Status.TODO,
                LocalDate.now().plusDays(1),
                null
        );

        taskRequestDTO = new TaskRequestDTO(
                "Test Task",
                "This is a test task",
                Priority.MEDIUM,
                Status.TODO,
                LocalDate.now().plusDays(1),
                1L
        );

    }

    @Test
    @DisplayName("should_createTask_when_validDtoProvided")
    void should_createTask_when_validDtoProvided() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponseDTO responseDTO = taskService.createTask(taskRequestDTO);

        // Then
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertEquals(taskRequestDTO.getName(), savedTask.getName());
        assertEquals(taskRequestDTO.getDescription(), savedTask.getDescription());
        assertEquals(taskRequestDTO.getPriority(), savedTask.getPriority());
        assertEquals(taskRequestDTO.getStatus(), savedTask.getStatus());
        assertEquals(taskRequestDTO.getDueDate(), savedTask.getDueDate());

        assertNotNull(responseDTO);
        assertEquals(task.getId(), responseDTO.getId());
    }

    @Test
    @DisplayName("should_returnAllTasks_when_tasksExist")
    void should_returnAllTasks_when_tasksExist() {
        // Given
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        // When
        List<TaskResponseDTO> responseDTOs = taskService.getAllTasks();

        // Then
        verify(taskRepository, times(1)).findAll();
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertEquals(task.getId(), responseDTOs.getFirst().getId());
    }

    @Test
    @DisplayName("should_returnEmptyList_when_noTasksExist")
    void should_returnEmptyList_when_noTasksExist() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        List<TaskResponseDTO> responseDTOs = taskService.getAllTasks();

        // Then
        verify(taskRepository, times(1)).findAll();
        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    @DisplayName("should_returnTask_when_taskExists")
    void should_returnTask_when_taskExists() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        TaskResponseDTO responseDTO = taskService.getTaskById(1L);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        assertNotNull(responseDTO);
        assertEquals(task.getId(), responseDTO.getId());
    }

    @Test
    @DisplayName("should_throwException_when_taskDoesNotExist")
    void should_throwException_when_taskDoesNotExist() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should_updateTask_when_taskExistsAndValidDtoProvided")
    void should_updateTask_when_taskExistsAndValidDtoProvided() {
        // Given
        Task updatedTask = new Task(
                1L,
                "Updated Task",
                "Updated description",
                Priority.HIGH,
                Status.IN_PROGRESS,
                LocalDate.now().plusDays(2),
                null
        );

        TaskRequestDTO updateDTO = new TaskRequestDTO();
        updateDTO.setName("Updated Task");
        updateDTO.setDescription("Updated description");
        updateDTO.setPriority(Priority.HIGH);
        updateDTO.setStatus(Status.IN_PROGRESS);
        updateDTO.setDueDate(LocalDate.now().plusDays(2));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        TaskResponseDTO responseDTO = taskService.updateTask(1L, updateDTO);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertEquals(updateDTO.getName(), savedTask.getName());
        assertEquals(updateDTO.getDescription(), savedTask.getDescription());
        assertEquals(updateDTO.getPriority(), savedTask.getPriority());
        assertEquals(updateDTO.getStatus(), savedTask.getStatus());
        assertEquals(updateDTO.getDueDate(), savedTask.getDueDate());

        assertNotNull(responseDTO);
        assertEquals(updatedTask.getId(), responseDTO.getId());
        assertEquals(updatedTask.getName(), responseDTO.getName());
    }

    @Test
    @DisplayName("should_throwException_when_updateTaskAnd_taskDoesNotExist")
    void should_throwException_when_updateTaskAnd_taskDoesNotExist() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, taskRequestDTO));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("should_deleteTask_when_taskExists")
    void should_deleteTask_when_taskExists() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("should_throwException_when_deleteTaskAnd_taskDoesNotExist")
    void should_throwException_when_deleteTaskAnd_taskDoesNotExist() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("should_searchTasks_when_validParametersProvided")
    void should_searchTasks_when_validParametersProvided() {
        // Given
        Status status = Status.TODO;
        Priority priority = Priority.MEDIUM;
        Long projectId = 1L;
        LocalDate dueDateFrom = LocalDate.now();
        LocalDate dueDateTo = LocalDate.now().plusDays(5);
        String search = "Test";
        String name = "Test Task";
        Long id = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        // Rozwiązanie problemu niejednoznaczności:
        // Używamy any(Specification.class) zamiast ogólnego any()
        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                status,
                priority,
                projectId,
                dueDateFrom,
                dueDateTo,
                search,
                name,
                id,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(task.getId(), responsePage.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("should_searchTasks_when_noParametersProvided")
    void should_searchTasks_when_noParametersProvided() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(task.getId(), responsePage.getContent().getFirst().getId());
    }


    @Test
    @DisplayName("should_searchTasks_with_onlyPriorityProvided")
    void should_searchTasks_with_onlyPriorityProvided() {
        // Given
        Priority priority = Priority.CRITICAL;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Task criticalTask = new Task(
                3L,
                "Critical Task",
                "A critical task",
                Priority.CRITICAL,
                Status.BLOCKED,
                LocalDate.now().plusDays(3),
                null
        );
        Page<Task> taskPage = new PageImpl<>(List.of(criticalTask), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                priority,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(Priority.CRITICAL, responsePage.getContent().getFirst().getPriority());
    }

    @Test
    @DisplayName("should_searchTasks_with_emptySearchString")
    void should_searchTasks_with_emptySearchString() {
        // Given
        String search = "   "; // tylko białe znaki
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                null,
                null,
                null,
                null,
                search,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    @DisplayName("should_searchTasks_with_multipleParametersProvided")
    void should_searchTasks_with_multipleParametersProvided() {
        // Given
        Status status = Status.BLOCKED;
        Priority priority = Priority.HIGH;
        Long projectId = 2L;
        LocalDate dueDateFrom = LocalDate.now();
        LocalDate dueDateTo = LocalDate.now().plusDays(10);
        String search = "critical";
        String name = "Critical Task";
        Long id = 3L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dueDate").descending());

        Task criticalTask = new Task(
                3L,
                "Critical Task",
                "A critical task description",
                Priority.HIGH,
                Status.BLOCKED,
                LocalDate.now().plusDays(5),
                null
        );

        Page<Task> taskPage = new PageImpl<>(List.of(criticalTask), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                status,
                priority,
                projectId,
                dueDateFrom,
                dueDateTo,
                search,
                name,
                id,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        TaskResponseDTO dto = responsePage.getContent().getFirst();
        assertEquals(status, dto.getStatus());
        assertEquals(priority, dto.getPriority());
        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
    }

    @Test
    @DisplayName("should_searchTasks_returnMultipleTasks")
    void should_searchTasks_returnMultipleTasks() {
        // Given
        Priority priority = Priority.LOW;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Task task1 = new Task(
                4L,
                "Low Priority Task 1",
                "Description 1",
                Priority.LOW,
                Status.TODO,
                LocalDate.now().plusDays(4),
                null
        );

        Task task2 = new Task(
                5L,
                "Low Priority Task 2",
                "Description 2",
                Priority.LOW,
                Status.IN_PROGRESS,
                LocalDate.now().plusDays(5),
                null
        );

        List<Task> tasks = Arrays.asList(task1, task2);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 2);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                priority,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(2, responsePage.getContent().size());
        assertTrue(responsePage.getContent().stream().allMatch(dto -> dto.getPriority() == Priority.LOW));
    }

    @Test
    @DisplayName("should_searchTasks_with_nullAndEmptyParameters")
    void should_searchTasks_with_nullAndEmptyParameters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        // Case 1: All parameters null
        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage1 = taskService.searchTasks(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage1);
        assertEquals(1, responsePage1.getTotalElements());

        // Reset mocks
        reset(taskRepository);

        // Case 2: Some parameters empty or null
        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        Page<TaskResponseDTO> responsePage2 = taskService.searchTasks(
                null,
                null,
                null,
                null,
                null,
                "",
                "",
                null,
                pageable
        );

        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage2);
        assertEquals(1, responsePage2.getTotalElements());
    }

    @Test
    @DisplayName("should_searchTasks_with_nonExistingParameters")
    void should_searchTasks_with_nonExistingParameters() {
        // Given
        Status status = Status.DONE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Task> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                status,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());
    }

    @Test
    @DisplayName("should_searchTasks_with_dueDateRangeProvided")
    void should_searchTasks_with_dueDateRangeProvided() {
        // Given
        LocalDate dueDateFrom = LocalDate.now().plusDays(1);
        LocalDate dueDateTo = LocalDate.now().plusDays(7);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dueDate").ascending());

        Task taskWithDueDate = new Task(
                6L,
                "Task with Due Date",
                "Task description",
                Priority.MEDIUM,
                Status.IN_PROGRESS,
                LocalDate.now().plusDays(5),
                null
        );

        Page<Task> taskPage = new PageImpl<>(List.of(taskWithDueDate), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                null,
                null,
                dueDateFrom,
                dueDateTo,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertTrue(responsePage.getContent().getFirst().getDueDate().isAfter(dueDateFrom.minusDays(1)));
        assertTrue(responsePage.getContent().getFirst().getDueDate().isBefore(dueDateTo.plusDays(1)));
    }

    @Test
    @DisplayName("should_searchTasks_with_projectIdProvided")
    void should_searchTasks_with_projectIdProvided() {
        // Given
        Long projectId = 10L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Task taskWithProject = new Task(
                7L,
                "Task with Project",
                "Task description",
                Priority.HIGH,
                Status.TODO,
                LocalDate.now().plusDays(3),
                null // Możesz ustawić projekt, jeśli istnieje encja Project
        );

        Page<Task> taskPage = new PageImpl<>(List.of(taskWithProject), pageable, 1);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                null,
                projectId,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        // Możesz dodatkowo sprawdzić, czy zadanie należy do odpowiedniego projektu
    }

    @Test
    @DisplayName("should_searchTasks_with_invalidDateRange")
    void should_searchTasks_with_invalidDateRange() {
        // Given
        LocalDate dueDateFrom = LocalDate.now().plusDays(10);
        LocalDate dueDateTo = LocalDate.now().plusDays(5); // dueDateFrom > dueDateTo
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dueDate").ascending());

        // Zakładając, że specyfikacje nie sprawdzają poprawności zakresu dat,
        // repozytorium zwróci pustą stronę
        Page<Task> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // When
        Page<TaskResponseDTO> responsePage = taskService.searchTasks(
                null,
                null,
                null,
                dueDateFrom,
                dueDateTo,
                null,
                null,
                null,
                pageable
        );

        // Then
        verify(taskRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());
    }
}
