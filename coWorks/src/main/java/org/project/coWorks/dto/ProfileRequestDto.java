package org.project.coWorks.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProfileRequestDto {
	private String userProfilePicture;
	private Long userId;
	private String userName;
	private String userTel;
    private String userMessage;
    private String userMail;
    private String userPosition;
    private String userDepartment;
    private LocalDate userBirth;
}