package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.dto.CommentDto;
import org.project.coWorks.dto.PostDto;
import org.project.coWorks.model.Comment;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.CommentRepository;
import org.project.coWorks.repository.EmployeeRepository;
import org.project.coWorks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
	@Autowired
	private CommentRepository cRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private BoardUserRepository boardUserRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UploadFileService uploadFileService;
	
	
	public int save(Map<String, Object> json,Long userId) {
		String temp = (String)json.get("targetId");
		Long targetId = null;
		if(temp != null) {
			targetId = Long.parseLong(temp);
		}
		String targetType = (String)json.get("targetType");
		String content = (String)json.get("content");
		if(targetId == null || content == null || targetType == null) {
			return CheckResult.NO_EXIST;
		}
		Comment c = new  Comment();
		c.setBoardUserId(userId);
		c.setCommentContent(content);
		c.setCommentTargetId(targetId);
		c.setCommentTargetType(targetType);
		if(cRepository.save(c) == null){
			return CheckResult.FAIL;
		}else {
			return CheckResult.SUCCESS;
		}
	}
	
	// 댓글 수정
	@Transactional
	public int updateComment(Long commentId, Long buId, Map<String, Object> json) {
		Comment comment = cRepository.findById(commentId).orElseThrow(null);
		String content = (String)json.get("content");
		if(comment == null ||content == null) {
			return CheckResult.NO_EXIST;
		}
		if (!comment.getBoardUserId().equals(buId)) {
			return CheckResult.NO_MATCH;
		}
		comment.setCommentContent(content);
		comment.setCommentEditDate(LocalDateTime.now());
		cRepository.save(comment);
		return CheckResult.SUCCESS;
	}
	// 댓글 숨김
	@Transactional
	public int hideComment(Long commentId, Long buId) {
		Comment comment = cRepository.findById(commentId).orElseThrow(null);
		if(comment == null) {
			return CheckResult.NO_EXIST;
		}
		if (!comment.getBoardUserId().equals(buId)) {
			return CheckResult.NO_MATCH;
		}
		comment.setCommentDelDate(LocalDateTime.now());
		cRepository.save(comment);
		return CheckResult.SUCCESS;
	}
	
	//댓글 찾기
	public List<Comment> comments(Long postId){
		List<Comment> comments = cRepository.findByCommentTargetId(postId);
		List<Comment> result = new ArrayList<>();
		for(Comment comment : comments) {
			result.add(comment);
			List<Comment> temp = cRepository.findByCommentTargetIdAndCommentTargetType(comment.getCommentId(), "comment");
			if(temp != null && temp.size() > 0) {
				for(Comment ctemp : temp) {
					result.add(ctemp);
				}
			}
		}
		return result;
	}
	// 아래내용은 추가 사항

	//댓글Dto 찾기
	public List<CommentDto> commentsDto(Long postId){
		List<Comment> comments = comments(postId);
		List<CommentDto> result = new ArrayList<>();
		for(Comment c : comments) {
			result.add(commentDto(c.getCommentId()));
		}
		return result;
	}
	
	// 댓글 보기
	@Transactional
	public CommentDto commentDto(Long commentId) {
		Comment c = cRepository.findByCommentIdAndCommentDelDateIsNull(commentId);
		if(c == null) {
			return null;
		}
		Long buId = c.getBoardUserId();
		Long uId = boardUserRepository
			.findById(buId).get().getUserId();
		String en = employeeRepository
			.findById(uId).get().getEmployeeName();
		LocalDateTime cwd = c.getCommentWriteDate(); 
		LocalDateTime ced = c.getCommentEditDate(); 

		CommentDto cDto = new CommentDto();
		String temp = cwd.toString().substring(0, cwd.toString().indexOf("T"));
		cDto.setWriteDate(temp);
		if(ced != null) {
			temp = ced.toString().substring(0, ced.toString().indexOf("T"));
		}
		cDto.setViewDate(temp);
		
		cDto.setUserId(buId);
		cDto.setProfile(
			uploadFileService.getPicture(
				userRepository.findById(uId).get().getProfileId()));
		cDto.setUserName(en);
		cDto.setContent(c.getCommentContent());
		cDto.setTargetId(c.getCommentTargetId());
		cDto.setTargetType(c.getCommentTargetType());
		return cDto;
	}
		
}
