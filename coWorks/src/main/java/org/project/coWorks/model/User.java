package org.project.coWorks.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {
	public static final String[] USER_STATUS = {
			"manager", "user", "delete",
			"stand", "admin" 
	};
	public static final int DELETE = 2;
	public static final int STAND = 3;
	public static final int ADMIN = 4;
	
	public static final String[] BOARD_USER_STATUS = {
			"MANAGER",
			"USER"
	};
	public static final int MANAGER = 0;	
	public static final int USER = 1;
	
	@Id
	@Column(name="user_id", columnDefinition = "BIGINT")
	private Long userId;
	
	@Column(name = "employee_id")
	private Long employeeId;

	@Column(name = "user_pw", columnDefinition = "TEXT")
	private String userPw;

	@Column(name = "user_message")
	private String userMessage;

	@Column(name = "file_id")
	private Long profileId;
	
	@Column(name = "user_status", length = 20)
	private String userStatus;
}
