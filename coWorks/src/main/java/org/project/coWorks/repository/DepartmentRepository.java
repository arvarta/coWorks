package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.Department;
import org.project.coWorks.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer>{

	List<Employee> findByDepartmentId(Long departmentId);
}
