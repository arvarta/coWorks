package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	List<Employee> findByDepartmentId(Integer departmentId);

	List<Employee> findByEmployeeName(String employeeName);
}
