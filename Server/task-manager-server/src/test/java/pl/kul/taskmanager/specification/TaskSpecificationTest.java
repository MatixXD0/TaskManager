package pl.kul.taskmanager.specification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Project;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.model.Task;
import pl.kul.taskmanager.repository.ProjectRepository;
import pl.kul.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskSpecificationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;


    @BeforeEach
    void setUp() {
        Task task1 = new Task(null, "Task Alpha", "Description Alpha", Priority.HIGH, Status.TODO, LocalDate.now().plusDays(5), null);
        Task task2 = new Task(null, "Task Beta", "Description Beta", Priority.MEDIUM, Status.IN_PROGRESS, LocalDate.now().plusDays(10), null);
        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @Test
    void should_findTasksByStatus() {
        // given
        var spec = TaskSpecification.hasStatus(Status.TODO);

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Status.TODO, results.getFirst().getStatus());
    }

    @Test
    void should_findTasksByPriority() {
        // given
        var spec = TaskSpecification.hasPriority(Priority.HIGH);

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Priority.HIGH, results.getFirst().getPriority());
    }

    @Test
    void should_findTasksByDueDateAfterOrEqual() {
        // given
        LocalDate date = LocalDate.now().plusDays(7);
        var spec = TaskSpecification.dueDateAfterOrEqual(date);

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.getFirst().getDueDate().isAfter(date) || results.getFirst().getDueDate().isEqual(date));
    }

    @Test
    void should_findTasksByDueDateBeforeOrEqual() {
        // given
        LocalDate date = LocalDate.now().plusDays(7);
        var spec = TaskSpecification.dueDateBeforeOrEqual(date);

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.getFirst().getDueDate().isBefore(date) || results.getFirst().getDueDate().isEqual(date));
    }

    @Test
    void should_findTasksByNameContainingKeyword() {
        // given
        var spec = TaskSpecification.nameContains("Alpha");

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.getFirst().getName().contains("Alpha"));
    }

    @Test
    void should_findTasksByDescriptionContainingKeyword() {
        // given
        var spec = TaskSpecification.descriptionContains("Beta");

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.getFirst().getDescription().contains("Beta"));
    }

    @Test
    void should_findTaskById() {
        // given
        Long id = taskRepository.findAll().getFirst().getId();
        var spec = TaskSpecification.hasId(id);

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(id, results.getFirst().getId());
    }

    @Test
    void should_findTasksBelongingToSpecificProject() {
        // given
        Project project = new Project(null, "Project 1", "Description", List.of());
        projectRepository.save(project); // Save project to persist relationship
        Task task = new Task(null, "Task Alpha", "Description Alpha", Priority.HIGH, Status.TODO, LocalDate.now().plusDays(5), project);
        taskRepository.save(task);

        var spec = TaskSpecification.belongsToProject(project.getId());

        // when
        List<Task> results = taskRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(project.getId(), results.getFirst().getProject().getId());
    }

}