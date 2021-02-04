package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TaskNotFoundException;
import com.codesoom.assignment.application.TaskService;
import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TaskController 클래스")
public class TestControllerTest {
    private static final Long GIVEN_ID = 1L;
    private static final String GIVEN_TITLE = "task1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    protected ObjectMapper objectMapper;

    Task taskSubject() {
        Task task = new Task();
        task.setId(GIVEN_ID);
        task.setTitle(GIVEN_TITLE);
        return task;
    }

    @Nested
    @DisplayName("GET /TASKS는")
    class Describe_getTasks {
        @Nested
        @DisplayName("서비스로 호출하는 Tasks가 존재하면")
        class Context_with_tasks {
            @BeforeEach
            void setUp() {
                Task task = new Task();
                task.setId(GIVEN_ID);
                task.setTitle(GIVEN_TITLE);
                List<Task> tasks = new ArrayList<>();
                tasks.add(task);

                given(taskService.getTasks())
                        .willReturn(tasks);
            }

            @DisplayName("OK 상태와 tasks 목록을 리턴한다.")
            @Test
            void it_return_tasks() throws Exception {
                mockMvc.perform(get("/tasks"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(GIVEN_TITLE)));
            }
        }

        @Nested
        @DisplayName("서비스로 호출하는 Tasks가 없으면")
        class Context_without_tasks {

            @DisplayName("OK와 비어있는 tasks 목록 리턴한다.")
            @Test
            void it_return_empty_tasks() throws Exception {
                mockMvc.perform(get("/tasks"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("[]")));
            }
        }
    }

    @Nested
    @DisplayName("GET /TASK는")
    class Describe_getTask {

        @Nested
        @DisplayName("서비스로 호출하는 Task가 없으면")
        class Context_without_task {
            @BeforeEach
            void setUp() {
                given(taskService.getTask(GIVEN_ID))
                        .willThrow(new TaskNotFoundException(GIVEN_ID));
            }
            @DisplayName("Not Found 상태를 리턴한다.")
            @Test
            void It_returns_not_found() throws Exception {
                mockMvc.perform(get("/tasks/" + GIVEN_ID))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("서비스로 호출하는 Task가 존재하면")
        class Context_with_task {
            @BeforeEach
            void setUp() {
                Task task = new Task();
                task.setId(GIVEN_ID);
                task.setTitle(GIVEN_TITLE);

                given(taskService.getTask(GIVEN_ID))
                        .willReturn(task);
            }

            @DisplayName("OK 상태와 task를 리턴한다.")
            @Test
            void it_return_task() throws Exception {
                final String content = "{\"id\":1,\"title\":\"task1\"}";

                mockMvc.perform(get("/tasks/" + GIVEN_ID))
                        .andExpect(status().isOk())
                        .andExpect(content().string(content));
            }
        }
    }

    @Nested
    @DisplayName("POST /tasks는")
    class Describe_create {
        @Nested
        @DisplayName("생성할 Task가 존재하면")
        class Context_with_task {
            Task task = taskSubject();

            @Test
            @DisplayName("Created 상태를 리턴한다.")
            void It_returns_created() throws Exception {
                mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task))
                ).andExpect(status().isCreated());
            }
        }
    }

}
