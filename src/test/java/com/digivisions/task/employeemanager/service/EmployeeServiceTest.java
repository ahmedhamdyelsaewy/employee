package com.digivisions.task.employeemanager.service;

import com.digivisions.task.employeemanager.exception.EmployeeNotFoundException;
import com.digivisions.task.employeemanager.exception.InvalidInputException;
import com.digivisions.task.employeemanager.model.Employee;
import com.digivisions.task.employeemanager.repository.EmployeeRepository;
import com.digivisions.task.employeemanager.validation.DepartmentVerificationService;
import com.digivisions.task.employeemanager.validation.EmailValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailValidationService emailValidationService;

    @Mock
    private DepartmentVerificationService departmentVerificationService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeId = UUID.randomUUID();
        employee = new Employee(employeeId, "ahmed hamdy", "elsawy", "ahmed.hamdey@example.com", "IT", 2000);
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(1, employees.size());
        assertEquals(employee, employees.get(0));
        verify(employeeRepository).findAll();
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployeeById(employeeId);

        assertEquals(employee, foundEmployee);
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void testCreateEmployee_Success() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(departmentVerificationService.isValidDepartment(anyString())).thenReturn(true);
        Employee createdEmployee = employeeService.createEmployee(employee);

        assertEquals(employee, createdEmployee);
        verify(employeeRepository).save(employee);
        verify(emailService).sendEmail(employee.getEmail(), "Employee Created", "Welcome, " + employee.getFirstName() + "!");
    }

    @Test
    void testCreateEmployee_InvalidSalary() {
        Employee invalidEmployee = new Employee(UUID.randomUUID(), "ahmed", "hamdy", "ahmed.hamdy@example.com", "HR", 1000);

        Exception exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(invalidEmployee);
        });

        assertEquals("Salary must be greater than or equal to 1,500.", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        Employee updatedEmployee = new Employee(employeeId, "ahmed", "mohamed", "ahmed.mohamed@example.com", "HR", 2500);

        Employee updated = employeeService.updateEmployee(employeeId, updatedEmployee);

        assertEquals(updatedEmployee.getFirstName(), updated.getFirstName());
        assertEquals(updatedEmployee.getLastName(), updated.getLastName());
        verify(emailService).sendEmail(employee.getEmail(), "Employee Updated", "Your details have been updated.");
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(employeeId, employee);
        });

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }
}
