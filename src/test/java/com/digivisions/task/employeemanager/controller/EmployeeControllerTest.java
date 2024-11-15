package com.digivisions.task.employeemanager.controller;

import com.digivisions.task.employeemanager.exception.EmployeeNotFoundException;
import com.digivisions.task.employeemanager.model.Employee;
import com.digivisions.task.employeemanager.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(UUID.randomUUID(), "Ahmed Hamdy", "Ahmed.Hamdy@example.com"));

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Ahmed Hamdy")));
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Employee employee = new Employee(id, "Ahmed Hamdy", "Ahmed.Hamdy@example.com");

        when(employeeService.getEmployeeById(id)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Ahmed Hamdy")));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(employeeService.getEmployeeById(id)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        Employee employee = new Employee(UUID.randomUUID(), "Ahmed Hamdy", "Ahmed.Hamdy@example.com");

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content("{\"firstName\": \"Ahmed Hamdy\", \"email\": \"Ahmed.Hamdy@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Ahmed Hamdy")));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Employee employee = new Employee(id, "Ahmed Hamdy", "Ahmed.Hamdy@example.com");

        when(employeeService.updateEmployee(eq(id), any(Employee.class))).thenReturn(employee);

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType("application/json")
                        .content("{\"firstName\": \"Ahmed Hamdy\", \"email\": \"Ahmed.Hamdy@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Ahmed Hamdy")));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(employeeService.updateEmployee(eq(id), any(Employee.class))).thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType("application/json")
                        .content("{\"firstName\": \"Ahmed Hamdy\", \"email\": \"Ahmed.Hamdy@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(employeeService).deleteEmployee(id);

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new EmployeeNotFoundException("Employee not found")).when(employeeService).deleteEmployee(id);

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }
}