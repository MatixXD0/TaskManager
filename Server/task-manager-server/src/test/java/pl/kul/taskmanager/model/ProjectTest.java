package pl.kul.taskmanager.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_createProject_when_validDataProvided() {
        // given
        Long id = 1L;
        String name = "Test Project";
        String description = "Project description";
        List<Task> tasks = new ArrayList<>();

        // when
        Project project = new Project(id, name, description, tasks);

        // then
        assertThat(project.getId()).isEqualTo(id);
        assertThat(project.getName()).isEqualTo(name);
        assertThat(project.getDescription()).isEqualTo(description);
        assertThat(project.getTasks()).isEmpty();
    }

    @Test
    void should_throwValidationError_when_nameIsTooShort() {
        // given
        Project project = new Project(null, "ab", null, new ArrayList<>());

        // when
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Project name must be between 3 and 100 characters");
    }

    @Test
    void should_throwValidationError_when_descriptionExceedsMaxLength() {
        // given
        String longDescription = "a".repeat(501);
        Project project = new Project(null, "Valid Project", longDescription, new ArrayList<>());

        // when
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description cannot exceed 500 characters");
    }

    @Test
    void should_addTaskToProject_when_taskIsValid() {
        // given
        Project project = new Project();
        Task task = new Task(null, "Task Name", "Task Description", Priority.HIGH, Status.TODO, null, project);

        // when
        project.getTasks().add(task);

        // then
        assertThat(project.getTasks()).contains(task);
        assertThat(task.getProject()).isEqualTo(project);
    }

    @Test
    void should_removeTaskFromProject_when_taskIsRemoved() {
        // given
        Project project = new Project();
        Task task = new Task(null, "Task Name", "Task Description", Priority.HIGH, Status.TODO, null, project);
        project.getTasks().add(task);

        // when
        project.getTasks().remove(task);

        // then
        assertThat(project.getTasks()).doesNotContain(task);
    }

    @Test
    void should_allowNullDescription_when_descriptionIsOptional() {
        // given
        Project project = new Project(null, "Valid Project", null, new ArrayList<>());

        // when
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void should_allowDuplicateTasksInProject() {
        // given
        Project project = new Project();
        Task task = new Task(null, "Task Name", "Task Description", Priority.HIGH, Status.TODO, null, project);

        // when
        project.getTasks().add(task);
        project.getTasks().add(task);

        // then
        assertThat(project.getTasks()).hasSize(2);
    }

    @Test
    void should_throwValidationError_when_nameIsNull() {
        // given
        Project project = new Project(null, null, "Description", new ArrayList<>());

        // when
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("nie może mieć wartości null");
    }

    @Test
    void should_serializeAndDeserializeProject() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = new Project(1L, "Test Project", "Test Description", new ArrayList<>());

        // when
        String json = objectMapper.writeValueAsString(project);
        Project deserializedProject = objectMapper.readValue(json, Project.class);

        // then
        assertThat(deserializedProject).isEqualTo(project);
    }

    @Test
    void should_updateProjectProperties() {
        // given
        Project project = new Project(1L, "Initial Name", "Initial Description", new ArrayList<>());

        // when
        project.setName("Updated Name");
        project.setDescription("Updated Description");

        // then
        assertThat(project.getName()).isEqualTo("Updated Name");
        assertThat(project.getDescription()).isEqualTo("Updated Description");
    }

}


