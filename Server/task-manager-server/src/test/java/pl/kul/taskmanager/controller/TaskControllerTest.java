package pl.kul.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.kul.taskmanager.dto.TaskRequestDTO;
import pl.kul.taskmanager.dto.TaskResponseDTO;
import pl.kul.taskmanager.exception.TaskNotFoundException;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.service.TaskService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskRequestDTO validTaskRequest;
    private TaskResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        validTaskRequest = new TaskRequestDTO();
        validTaskRequest.setName("Test Task");
        validTaskRequest.setDescription("Description");
        validTaskRequest.setPriority(Priority.HIGH);
        validTaskRequest.setStatus(Status.TODO);
        validTaskRequest.setDueDate(LocalDate.now());

        sampleResponseDTO = new TaskResponseDTO();
        sampleResponseDTO.setId(1L);
        sampleResponseDTO.setName("Test Task");
        sampleResponseDTO.setDescription("Description");
        sampleResponseDTO.setPriority(Priority.HIGH);
        sampleResponseDTO.setStatus(Status.TODO);
        sampleResponseDTO.setDueDate(LocalDate.now());
        sampleResponseDTO.setProjectId(null);
    }

    @Test
    void should_createTask_when_validRequest() throws Exception {
        // given
        when(taskService.createTask(org.mockito.ArgumentMatchers.any(TaskRequestDTO.class)))
                .thenReturn(sampleResponseDTO);

        // when
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sampleResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(sampleResponseDTO.getName())))
                .andExpect(jsonPath("$.description", is(sampleResponseDTO.getDescription())))
                .andExpect(jsonPath("$.priority", is(sampleResponseDTO.getPriority().toString())))
                .andExpect(jsonPath("$.status", is(sampleResponseDTO.getStatus().toString())));

        verify(taskService, times(1)).createTask(org.mockito.ArgumentMatchers.any(TaskRequestDTO.class));
    }

    @Test
    void should_returnAllTasks_when_getAllTasksIsCalled() throws Exception {
        // given
        TaskResponseDTO task1 = new TaskResponseDTO();
        task1.setId(1L);
        task1.setName("Task 1");
        task1.setPriority(Priority.LOW);
        task1.setStatus(Status.TODO);

        TaskResponseDTO task2 = new TaskResponseDTO();
        task2.setId(2L);
        task2.setName("Task 2");
        task2.setPriority(Priority.HIGH);
        task2.setStatus(Status.IN_PROGRESS);

        List<TaskResponseDTO> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // when
        mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Task 1")))
                .andExpect(jsonPath("$[0].priority", is(Priority.LOW.toString())))
                .andExpect(jsonPath("$[0].status", is(Status.TODO.toString())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Task 2")))
                .andExpect(jsonPath("$[1].priority", is(Priority.HIGH.toString())))
                .andExpect(jsonPath("$[1].status", is(Status.IN_PROGRESS.toString())));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void should_returnTask_when_getTaskByIdIsCalledWithExistingId() throws Exception {
        // given
        when(taskService.getTaskById(eq(1L))).thenReturn(sampleResponseDTO);

        // when
        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sampleResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(sampleResponseDTO.getName())))
                .andExpect(jsonPath("$.priority", is(Priority.HIGH.toString())))
                .andExpect(jsonPath("$.status", is(Status.TODO.toString())));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void should_throwTaskNotFound_when_getTaskByIdIsCalledWithNonExistingId() throws Exception {
        // given
        when(taskService.getTaskById(eq(100L))).thenThrow(new TaskNotFoundException(100L));

        // when
        mockMvc.perform(get("/api/tasks/{id}", 100L))
                .andDo(print())
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found with ID: 100")));

        verify(taskService, times(1)).getTaskById(100L);
    }

    @Test
    void should_updateTask_when_validRequest() throws Exception {
        // given
        TaskRequestDTO updateRequest = new TaskRequestDTO();
        updateRequest.setName("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority(Priority.MEDIUM);
        updateRequest.setStatus(Status.IN_PROGRESS);
        updateRequest.setDueDate(LocalDate.now());

        TaskResponseDTO updatedResponse = new TaskResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName(updateRequest.getName());
        updatedResponse.setDescription(updateRequest.getDescription());
        updatedResponse.setPriority(updateRequest.getPriority());
        updatedResponse.setStatus(updateRequest.getStatus());
        updatedResponse.setDueDate(updateRequest.getDueDate());

        when(taskService.updateTask(eq(1L), org.mockito.ArgumentMatchers.any(TaskRequestDTO.class)))
                .thenReturn(updatedResponse);

        // when
        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.priority", is(Priority.MEDIUM.toString())))
                .andExpect(jsonPath("$.status", is(Status.IN_PROGRESS.toString())));

        verify(taskService, times(1)).updateTask(eq(1L), org.mockito.ArgumentMatchers.any(TaskRequestDTO.class));
    }

    @Test
    void should_returnNotFound_when_updateTaskWithInvalidId() throws Exception {
        // given
        when(taskService.updateTask(eq(99L), org.mockito.ArgumentMatchers.any(TaskRequestDTO.class)))
                .thenThrow(new TaskNotFoundException(99L));

        // when
        mockMvc.perform(put("/api/tasks/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andDo(print())
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found with ID: 99")));

        verify(taskService, times(1)).updateTask(eq(99L), org.mockito.ArgumentMatchers.any(TaskRequestDTO.class));
    }

    @Test
    void should_deleteTask_when_validIdIsProvided() throws Exception {
        // given
        doNothing().when(taskService).deleteTask(eq(1L));

        // when
        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andDo(print())
                // then
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void should_returnNotFound_when_deleteTaskWithInvalidId() throws Exception {
        // given
        doThrow(new TaskNotFoundException(99L)).when(taskService).deleteTask(eq(99L));

        // when
        mockMvc.perform(delete("/api/tasks/{id}", 99L))
                .andDo(print())
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found with ID: 99")));

        verify(taskService, times(1)).deleteTask(99L);
    }

    @Test
    void should_searchTasks_when_validParametersProvided() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<TaskResponseDTO> mockPage = new PageImpl<>(List.of(sampleResponseDTO), pageable, 1);

        when(taskService.searchTasks(
                eq(Status.TODO),
                eq(Priority.HIGH),
                eq(10L),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                eq("searchTerm"),
                eq("SomeName"),
                eq(5L),
                org.mockito.ArgumentMatchers.any(Pageable.class))
        ).thenReturn(mockPage);

        // when
        mockMvc.perform(get("/api/tasks/search")
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("projectId", "10")
                        .param("dueDateFrom", "2025-01-01")
                        .param("dueDateTo", "2025-12-31")
                        .param("search", "searchTerm")
                        .param("name", "SomeName")
                        .param("id", "5")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andDo(print())
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(sampleResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(sampleResponseDTO.getName())))
                .andExpect(jsonPath("$.content[0].priority", is(sampleResponseDTO.getPriority().toString())))
                .andExpect(jsonPath("$.content[0].status", is(sampleResponseDTO.getStatus().toString())))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(taskService, times(1)).searchTasks(
                eq(Status.TODO),
                eq(Priority.HIGH),
                eq(10L),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                eq("searchTerm"),
                eq("SomeName"),
                eq(5L),
                org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }

    @Test
    void should_returnBadRequest_when_creatingTaskWithEmptyName() throws Exception {
        // given
        validTaskRequest.setName(""); // Nieprawidłowa wartość
        // when
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andDo(print())
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name must be between 3 and 100 characters")));

        verify(taskService, never()).createTask(org.mockito.ArgumentMatchers.any(TaskRequestDTO.class));
    }

    @Test
    void should_returnBadRequest_when_creatingTaskWithDueDateInPast() throws Exception {
        // given
        validTaskRequest.setDueDate(LocalDate.now().minusDays(1)); // Data w przeszłości
        // when
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andDo(print())
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Due date must be in the future or present")));

        verify(taskService, never()).createTask(org.mockito.ArgumentMatchers.any(TaskRequestDTO.class));
    }
}



