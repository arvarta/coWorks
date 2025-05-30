package org.project.coWorks.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
	private MultipartFile userProfilePicture;
    private String userMessenger;
    private String userStatus;
}
