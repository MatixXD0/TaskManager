package pl.kul.taskmanagerclient.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.kul.taskmanagerclient.Config;
import pl.kul.taskmanagerclient.dto.PageResponse;
import pl.kul.taskmanagerclient.dto.TaskDto;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class TaskApiService extends BaseApiService {
    private static final String BASE_URL = Config.BASE_URL + "/tasks";

    public TaskApiService() {
        this(HttpClient.newHttpClient(), new ObjectMapper().findAndRegisterModules());
    }

    public TaskApiService(HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
    }

    public TaskDto getTaskById(Long id) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, "/" + id, "GET", null, new TypeReference<>() {
        });
    }

    public List<TaskDto> getAllTasks() throws IOException, InterruptedException {
        return sendRequest(BASE_URL, null, "GET", null, new TypeReference<>() {
        });
    }

    public TaskDto createTask(TaskDto task) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, null, "POST", task, new TypeReference<>() {
        });
    }

    public TaskDto updateTask(Long id, TaskDto updatedTask) throws IOException, InterruptedException {
        return sendRequest(BASE_URL, "/" + id, "PUT", updatedTask, new TypeReference<>() {
        });
    }

    public void deleteTask(Long id) throws IOException, InterruptedException {
        sendRequest(BASE_URL, "/" + id, "DELETE", null, new TypeReference<Void>() {
        });
    }

    public PageResponse<TaskDto> searchTasks(Map<String, String> queryParams) throws IOException, InterruptedException {
        String url = buildUrlWithQuery(BASE_URL, "/search", queryParams);
        return sendRequest(url, null, "GET", null, new TypeReference<>() {
        });
    }
}
