package pl.kul.taskmanager.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import pl.kul.taskmanager.model.Priority;
import pl.kul.taskmanager.model.Status;
import pl.kul.taskmanager.model.Task;

import java.time.LocalDate;

public class TaskSpecification {

    public static Specification<Task> hasStatus(Status status) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> belongsToProject(Long projectId) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("project", JoinType.LEFT).get("id"), projectId);
    }

    public static Specification<Task> dueDateAfterOrEqual(LocalDate date) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), date);
    }

    public static Specification<Task> dueDateBeforeOrEqual(LocalDate date) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), date);
    }

    public static Specification<Task> nameContains(String keyword) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Task> descriptionContains(String keyword) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Task> hasId(Long id) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

}
