package com.sachin.appraisal.security;

import com.sachin.appraisal.entity.Employee;
import com.sachin.appraisal.repository.EmployeeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public AppUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No employee with email " + email));

        return new org.springframework.security.core.userdetails.User(
                employee.getEmail(),
                employee.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
        );
    }
}
