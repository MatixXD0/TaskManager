package pl.kul.taskmanager.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.kul.taskmanager.model.Project;

public class ProjectSpecification {

    public static Specification<Project> nameContains(String keyword) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Project> descriptionContains(String keyword) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase() + "%");
    }


    public static Specification<Project> hasId(Long id) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }
}
