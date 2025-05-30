package org.project.coWorks.dto;

import lombok.Data;

@Data
public class JoinRequest {
    private Long userId;
    private String userPw;
    private String email;
}