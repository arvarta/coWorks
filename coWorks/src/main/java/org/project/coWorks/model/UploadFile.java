package org.project.coWorks.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
 
@Data
@Entity
@Table(name = "file")
public class UploadFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id", columnDefinition = "BIGINT")
    private Long fileId;

	@Column(name = "post_id", columnDefinition = "BIGINT")//
    private Long postId;
	
	@Column(name = "post_size", columnDefinition = "BIGINT")//
    private Long size;

	@Column(name = "file_name", length = 50)
	private String fileName;

	@Column(name = "file_date", columnDefinition = "DATETIME")
	private LocalDateTime fileDate = LocalDateTime.now();
}
