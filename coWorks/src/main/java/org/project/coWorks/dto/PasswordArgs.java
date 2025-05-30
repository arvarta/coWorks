package org.project.coWorks.dto;

import lombok.Data;

@Data
public class PasswordArgs {
	private String oldPassword;
    private String newPassword;
    private String retry;
}
