package pl.kul.taskmanager.specification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kul.taskmanager.model.Project;
import pl.kul.taskmanager.repository.ProjectRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProjectSpecificationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        Project project1 = new Project(null, "Project Alpha", "Description Alpha", List.of());
        Project project2 = new Project(null, "Project Beta", "Description Beta", List.of());
        projectRepository.save(project1);
        projectRepository.save(project2);
    }

    @Test
    void should_findProjectsByNameContainingKeyword() {
        // given
        var spec = ProjectSpecification.nameContains("Alpha");

        // when
        List<Project> results = projectRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Project Alpha", results.getFirst().getName());
    }

    @Test
    void should_findProjectsByDescriptionContainingKeyword() {
        // given
        var spec = ProjectSpecification.descriptionContains("Beta");

        // when
        List<Project> results = projectRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Description Beta", results.getFirst().getDescription());
    }

    @Test
    void should_findProjectById() {
        // given
        Long id = projectRepository.findAll().getFirst().getId();
        var spec = ProjectSpecification.hasId(id);

        // when
        List<Project> results = projectRepository.findAll(spec);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(id, results.getFirst().getId());
    }


}
