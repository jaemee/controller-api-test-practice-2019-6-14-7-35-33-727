package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {

    private List<Todo> todos = new ArrayList<>();

    @Autowired
    private TodoController todoController;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Todo todo;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAll() throws Exception {
        //given
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", is(todos)));
    }

    @Test
    void getTodo() throws Exception {
        todo = new Todo(3, "Test", true, 4);
        //given
        Optional<Todo> optionalTodo = Optional.of(todo);
        when(todoRepository.findById(3)).thenReturn(optionalTodo);
        //when
        ResultActions result = mvc.perform(get("/todos/3"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void saveTodo() throws Exception {
        //given
        todo = new Todo(3, "Test", true, 4);
        todos.add(todo);
        when(todoRepository.getAll()).thenReturn(todos);

        //when
        ResultActions result = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)));
        //then
        result.andExpect(status().isCreated());
    }

    @Test
    void should_return_of_deleteOneTodo_when_id_existing() throws Exception {
        todo = new Todo(3, "Test", true, 4);
        //given
        Optional<Todo> optionalTodo = Optional.of(todo);
        when(todoRepository.findById(3)).thenReturn(optionalTodo);
        //when
        ResultActions result = mvc.perform(delete("/todos/3"));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    void should_return_not_found_deleteOneTodo_when_id_not_exist() throws Exception {
        ResultActions result = mvc.perform(delete("/todos/4"));
        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_updateTodo_when_id_exisiting() throws Exception {
        todo = new Todo(3, "Test", true, 4);
        ResultActions result = mvc.perform(patch("/todos/10")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)));
        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_bad_request_updateTodo_when_wrong_parameter() throws Exception {
        ResultActions result = mvc.perform(patch("/todos/10")
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        result.andExpect(status().isBadRequest());
    }

    @Test
    void should_return_ok_updateTodo_when_deleted() throws Exception {
        todo = new Todo(3, "Test", true, 4);
        ResultActions result = mvc.perform(patch("/todos/3")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)));
        result.andExpect(status().isOk());
    }
}