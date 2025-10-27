package pl.kul.taskmanager.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_createTask_when_validDataProvided() {
        // given
        Long id = 1L;
        String name = "Test Task";
        String description = "Task description";
        Priority priority = Priority.HIGH;
        Status status = Status.TODO;
        LocalDate dueDate = LocalDate.now().plusDays(5);
        Project project = new Project();

        // when
        Task task = new Task(id, name, description, priority, status, dueDate, project);

        // then
        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getName()).isEqualTo(name);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getPriority()).isEqualTo(priority);
        assertThat(task.getStatus()).isEqualTo(status);
        assertThat(task.getDueDate()).isEqualTo(dueDate);
        assertThat(task.getProject()).isEqualTo(project);
    }

    @Test
    void should_throwValidationError_when_nameIsTooShort() {
        // given
        Task task = new Task(null, "a", null, Priority.HIGH, Status.TODO, LocalDate.now(), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must be between 3 and 100 characters");
    }

    @Test
    void should_throwValidationError_when_dueDateIsInThePast() {
        // given
        Task task = new Task(null, "Valid Name", null, Priority.HIGH, Status.TODO, LocalDate.now().minusDays(1), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Due date must be in the future or present");
    }

    @Test
    void should_updateTaskProperties_when_validDataProvided() {
        // given
        Task task = new Task();
        String updatedName = "Updated Task Name";
        String updatedDescription = "Updated Description";
        Priority updatedPriority = Priority.MEDIUM;
        Status updatedStatus = Status.IN_PROGRESS;
        LocalDate updatedDueDate = LocalDate.now().plusDays(10);
        Project updatedProject = new Project();

        // when
        task.setName(updatedName);
        task.setDescription(updatedDescription);
        task.setPriority(updatedPriority);
        task.setStatus(updatedStatus);
        task.setDueDate(updatedDueDate);
        task.setProject(updatedProject);

        // then
        assertThat(task.getName()).isEqualTo(updatedName);
        assertThat(task.getDescription()).isEqualTo(updatedDescription);
        assertThat(task.getPriority()).isEqualTo(updatedPriority);
        assertThat(task.getStatus()).isEqualTo(updatedStatus);
        assertThat(task.getDueDate()).isEqualTo(updatedDueDate);
        assertThat(task.getProject()).isEqualTo(updatedProject);
    }

    @Test
    void should_throwValidationError_when_descriptionExceedsMaxLength() {
        // given
        String longDescription = "a".repeat(501);
        Task task = new Task(null, "Valid Name", longDescription, Priority.LOW, Status.TODO, LocalDate.now(), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description cannot exceed 500 characters");
    }

    @Test
    void should_createTask_when_validDueDateIsToday() {
        // given
        LocalDate today = LocalDate.now();

        // when
        Task task = new Task(null, "Valid Task", null, Priority.HIGH, Status.TODO, today, null);

        // then
        assertThat(task.getDueDate()).isEqualTo(today);
    }

    @Test
    void should_createTask_when_optionalFieldsAreNull() {
        // given
        Task task = new Task(null, "Valid Task", null, Priority.LOW, Status.TODO, LocalDate.now(), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isEmpty();
    }


    @Test
    void should_throwValidationError_when_priorityIsNull() {
        // given
        Task task = new Task(null, "Valid Task", null, null, Status.TODO, LocalDate.now(), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("nie może mieć wartości null");
    }

    @Test
    void should_throwValidationError_when_statusIsNull() {
        // given
        Task task = new Task(null, "Valid Task", null, Priority.HIGH, null, LocalDate.now(), null);

        // when
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("nie może mieć wartości null");
    }

    @Test
    void should_allowSettingNullInOptionalFields() {
        // given
        Task task = new Task(null, "Valid Task", "Some description", Priority.MEDIUM, Status.TODO, LocalDate.now(), new Project());

        // when
        task.setDescription(null);
        task.setProject(null);

        // then
        assertThat(task.getDescription()).isNull();
        assertThat(task.getProject()).isNull();
    }

    @Test
    void should_returnTrue_when_tasksAreEqual() {
        // given
        Task task1 = new Task(1L, "Test Task", "Description", Priority.LOW, Status.BLOCKED, LocalDate.now(), null);
        Task task2 = new Task(1L, "Test Task", "Description", Priority.LOW, Status.BLOCKED, LocalDate.now(), null);

        // when
        boolean areEqual = task1.equals(task2);

        // then
        assertThat(areEqual).isTrue();
        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
    }

    @Test
    void should_updateAllFieldsSuccessfully() {
        // given
        Task task = new Task();
        LocalDate dueDate = LocalDate.now().plusDays(3);
        Project project = new Project();

        // when
        task.setId(1L);
        task.setName("Updated Name");
        task.setDescription("Updated Description");
        task.setPriority(Priority.CRITICAL);
        task.setStatus(Status.DONE);
        task.setDueDate(dueDate);
        task.setProject(project);

        // then
        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getName()).isEqualTo("Updated Name");
        assertThat(task.getDescription()).isEqualTo("Updated Description");
        assertThat(task.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(task.getStatus()).isEqualTo(Status.DONE);
        assertThat(task.getDueDate()).isEqualTo(dueDate);
        assertThat(task.getProject()).isEqualTo(project);
    }



}
