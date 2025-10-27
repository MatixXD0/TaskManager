package pl.kul.taskmanagerclient.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.kul.taskmanagerclient.Config;
import pl.kul.taskmanagerclient.dto.PageResponse;
import pl.kul.taskmanagerclient.dto.ProjectDto;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class ProjectApiService extends BaseApiService {
    private static final String BASE_URL = Config.BASE_URL + "/projects";

    public ProjectApiService() {
        this(HttpClient.newHttpClient(), new ObjectMapper().findAndRegisterModules());
    }

    public ProjectApiService(HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
    }

    public ProjectDto getProjectById(Long id) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, "/" + id, "GET", null, new TypeReference<>() {
        });
    }

    public List<ProjectDto> getAllProjects() throws IOException, InterruptedException {
        return sendRequest(BASE_URL, null, "GET", null, new TypeReference<>() {
        });
    }

    public ProjectDto createProject(ProjectDto project) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, null, "POST", project, new TypeReference<>() {
        });
    }

    public ProjectDto updateProject(Long id, ProjectDto updatedProject) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, "/" + id, "PUT", updatedProject, new TypeReference<>() {
        });
    }

    public void deleteProject(Long projectId) throws IOException, InterruptedException {
        sendRequest(BASE_URL, "/" + projectId, "DELETE", null, new TypeReference<Void>() {
        });
    }

    public ProjectDto modifyTaskInProject(Long projectId, Long taskId, String method) throws IOException, InterruptedException {
        String path = String.format("/%d/tasks/%d", projectId, taskId);
        return sendRequest(BASE_URL, path, method, null, new TypeReference<>() {
        });
    }

    public ProjectDto addTaskToProject(Long projectId, Long taskId) throws IOException, InterruptedException {
        return modifyTaskInProject(projectId, taskId, "POST");
    }

    public ProjectDto removeTaskFromProject(Long projectId, Long taskId) throws IOException, InterruptedException {
        return modifyTaskInProject(projectId, taskId, "DELETE");
    }

    public PageResponse<ProjectDto> searchProjects(Map<String, String> queryParams) throws IOException, InterruptedException {
        String url = buildUrlWithQuery(BASE_URL, "/search", queryParams);
        return sendRequest(url, null, "GET", null, new TypeReference<>() {
        });
    }
}
