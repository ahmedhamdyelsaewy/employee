package com.digivisions.task.employeemanager.service;

import com.digivisions.task.employeemanager.exception.EmployeeNotFoundException;
import com.digivisions.task.employeemanager.exception.InvalidInputException;
import com.digivisions.task.employeemanager.model.Employee;
import com.digivisions.task.employeemanager.repository.EmployeeRepository;
import com.digivisions.task.employeemanager.validation.DepartmentVerificationService;
import com.digivisions.task.employeemanager.validation.EmailValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final EmailValidationService validateEmail;
    private final DepartmentVerificationService validateDepartment;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmailService emailService
            , EmailValidationService validateEmail, DepartmentVerificationService validateDepartment) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
        this.validateEmail = validateEmail;
        this.validateDepartment = validateDepartment;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employee.getSalary() < 1500) {
            throw new InvalidInputException("Salary must be greater than or equal to 1,500.");
        } else if (!validateEmail.isValidEmail(employee.getEmail())) {
            throw new InvalidInputException("Invalid email address");
        } else if (!validateDepartment.isValidDepartment(employee.getDepartment())) {
            throw new InvalidInputException("Invalid department");
        }
        Employee savedEmployee = employeeRepository.save(employee);
        emailService.sendEmail(savedEmployee.getEmail(), "Employee Created", "Welcome, " + savedEmployee.getFirstName() + "!");
        return savedEmployee;
    }

    @Transactional
    public Employee updateEmployee(UUID id, Employee updatedEmployee) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setSalary(updatedEmployee.getSalary());
        emailService.sendEmail(employee.getEmail(), "Employee Updated", "Your details have been updated.");
        return employee;
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}

