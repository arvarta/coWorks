package org.project.coWorks.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private Long userId;
    private String UserPw;
    private String messenger;
    private String tier;
}
