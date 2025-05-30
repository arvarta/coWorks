package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long>{

	List<UploadFile> findByPostId(Long postId);

	UploadFile findByFileName(String fileName);
	
	UploadFile findByFileId(Long profileId);
}
