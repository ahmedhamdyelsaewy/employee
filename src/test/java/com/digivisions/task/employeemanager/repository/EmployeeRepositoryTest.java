package com.digivisions.task.employeemanager.repository;

import com.digivisions.task.employeemanager.exception.EmployeeNotFoundException;
import com.digivisions.task.employeemanager.model.Employee;
import com.digivisions.task.employeemanager.service.EmailService;
import com.digivisions.task.employeemanager.service.EmployeeService;
import com.digivisions.task.employeemanager.validation.DepartmentVerificationService;
import com.digivisions.task.employeemanager.validation.EmailValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeRepositoryTest {

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

    private UUID employeeId;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeId = UUID.randomUUID();
        employee = new Employee(employeeId, "ahmed hamdy", "elsawy", "ahmed.hamdey@example.com", "IT", 2000);
    }

    // Positive Test Case: Find an Employee by ID
    @Test
    void testFindEmployeeById_Success() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        Optional<Employee> foundEmployee = Optional.ofNullable(employeeService.getEmployeeById(employeeId));
        
        assertTrue(foundEmployee.isPresent());
        assertEquals(employeeId, foundEmployee.get().getId());
        assertEquals("ahmed hamdy", foundEmployee.get().getFirstName());
    }

    @Test
    void testFindEmployeeById_NotFound() {
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(employeeId));
    }

    @Test
    void testSaveEmployee_Success() {
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(emailValidationService.isValidEmail(anyString())).thenReturn(true);
        when(departmentVerificationService.isValidDepartment(anyString())).thenReturn(true);
        Employee savedEmployee = employeeService.createEmployee(employee);

        assertNotNull(savedEmployee);
        assertEquals(employeeId, savedEmployee.getId());
        verify(emailService).sendEmail(employee.getEmail(), "Employee Created", "Welcome, " + employee.getFirstName() + "!");
    }

    @Test
    void testSaveEmployee_NullEmployee() {
        assertThrows(NullPointerException.class, () -> {
            employeeService.createEmployee(null);
        });
    }

    @Test
    void testDeleteEmployee_Success() {
        doNothing().when(employeeRepository).delete(employee);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> employeeService.deleteEmployee(employeeId));

    }


    @Test
    void testDeleteEmployee_NotFound() {
        doThrow(new EmployeeNotFoundException("Employee not found")).when(employeeRepository).deleteById(employeeId);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(employeeId));
    }

    @Test
    void testFindAllEmployees_Performance() {
        List<Employee> largeEmployeeList = generateLargeEmployeeList(10000);
        when(employeeRepository.findAll()).thenReturn(largeEmployeeList);
        
        long startTime = System.currentTimeMillis();
        List<Employee> employees = employeeService.getAllEmployees();
        long endTime = System.currentTimeMillis();
        
        assertEquals(10000, employees.size());
        assertTrue((endTime - startTime) < 1000);
    }

    private List<Employee> generateLargeEmployeeList(int size) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            employees.add(new Employee(UUID.randomUUID(), "Employee " + i, "Role " + i));
        }
        return employees;
    }
}
  