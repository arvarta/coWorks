package org.project.coWorks.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.model.UploadFile;
import org.project.coWorks.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {
	@Value("${file.upload-dir}") // application.properties에서 파일 업로드 경로 읽어옴
	private String fileDir;
	
	@Autowired
	private UploadFileRepository uploadFileRepository;
	
	public List<UploadFile> findByPostId(Long postId) {
		if(postId == null || postId < 1) {
			return null;
		}
		List<UploadFile> target = uploadFileRepository.findByPostId(postId);
		if(target == null) {
			return null;
		}
		return target;
	}

	public List<UploadFile> save(Long postId, MultipartFile... files) {
		List<UploadFile> uploadFiles = new ArrayList<>();

	    File dir = new File(fileDir);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }
	    List<UploadFile> oldFiles = findByPostId(postId);
	    if(oldFiles != null && !oldFiles.isEmpty()) {
	    	for(UploadFile uf : oldFiles) {
	    		File f = Paths.get(fileDir, uf.getFileName()).toFile();
	    		if(f.exists()) {
	    			if(!f.delete()) {
	    				System.out.println("삭제 실패 : " + f.getAbsolutePath());
	    			}
	    		}else {	    			
	    			System.out.println("존재하지 않음 : " + f.getAbsolutePath());
	    		}
	    	}
	    }
	    for (MultipartFile file : files) {
	        if (file == null || file.isEmpty()) continue;
	        
	        try {
	        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	        	LocalDateTime now = LocalDateTime.now();
	        	String saveTime = now.format(formatter);
				String originalFileName = file.getOriginalFilename();
				if(originalFileName == null || originalFileName.isBlank()) {
					System.err.println("파일 이름이 없음: " + file);
				}
				String savedFileName = saveTime + "_" + originalFileName;
				File destination = new File(fileDir, savedFileName);
				file.transferTo(destination);

				UploadFile uploadFile = new UploadFile();
				uploadFile.setFileName(savedFileName);
				uploadFile.setPostId(postId);
				uploadFile.setFileDate(now);
				uploadFile.setSize(file.getSize());
				uploadFiles.add(uploadFileRepository.save(uploadFile));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	    }

	    return uploadFiles;
	}

	public int updatePicture(Long userId, MultipartFile file) {
		File dir = new File(fileDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if(save(null, file) != null) {
			return CheckResult.SUCCESS;
		}
		return CheckResult.FAIL;
	}
	
	public String getPicture(Long fileId) {
		if(fileId == null) {
			return null;
		}
		UploadFile fileInfo = uploadFileRepository.findById(fileId).orElse(null);
		Path filePath = Paths.get(fileDir, fileInfo.getFileName());
		return filePath.toString();
	}
	
	public byte[] getFile(Long fileId) {
		UploadFile fileInfo = uploadFileRepository.findById(fileId).orElse(null);
		if(fileInfo == null) {
			return null;
		}
		try {
			Path filePath = Paths.get(fileDir, fileInfo.getFileName());
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			return null;
		}
	}
}
