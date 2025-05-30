package org.project.coWorks.service;

import java.util.ArrayList;
import java.util.List;

import org.project.coWorks.model.Employee;
import org.project.coWorks.model.User;
import org.project.coWorks.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	public List<Employee> findByDepartmentId(Integer departmentId) {
		return employeeRepository.findByDepartmentId(departmentId);
	}

	public Employee findByEmployeeId(Long userId) {
		return employeeRepository.findById(userId).orElse(null);
	}

	public List<Employee> employeeListFindByUserList(List<User> userList) {
		List<Employee> result = new ArrayList<>();
		for(User user : userList) {
			Employee e = employeeRepository.findById(user.getUserId()).orElse(null);
			if(e != null) {
				result.add(e);
			} else {
				Employee temp = new Employee();
				temp.setEmployeeId(user.getUserId());
				temp.setEmployeeName("존재하지 않는 사원입니다.");
				result.add(temp);
			}
		}
		return result;
	}
}
