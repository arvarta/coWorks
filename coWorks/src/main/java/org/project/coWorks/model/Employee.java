package org.project.coWorks.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id", columnDefinition = "BIGINT")
	private Long employeeId;

	@Column(name = "position_Id")
	private Integer positionId;

	@Column(name = "department_id")
	private Integer departmentId;

	@Column(name = "employee_name", length = 30)
	private String employeeName;

	@Column(name = "employee_birth")
	private LocalDate employeeBirth;

	@Column(name = "employee_tel", length = 20)
	private String employeeTel;

	@Column(name = "employee_mail", length = 100)
	private String employeeMail;
}