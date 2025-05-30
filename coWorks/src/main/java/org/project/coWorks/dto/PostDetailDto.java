package org.project.coWorks.dto;

import java.util.List;

import org.project.coWorks.model.Comment;
import org.project.coWorks.model.UploadFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDetailDto {
	private Long postId;
	private Long userId;
    private List<UploadFile> files;
    private List<Comment> comments;
}
