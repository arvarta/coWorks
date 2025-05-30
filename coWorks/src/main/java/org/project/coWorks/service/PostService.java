package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.dto.PostDto;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.model.Employee;
import org.project.coWorks.model.Post;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.EmployeeRepository;
import org.project.coWorks.repository.PostRepository;
import org.project.coWorks.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PostService {
	@Value("${file.upload-dir}") // application.properties에서 파일 업로드 경로 읽어옴
	private String fileDir;
	
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private UploadFileRepository uploadFileRepository;
	@Autowired
	private UploadFileService uploadFileService;
	@Autowired
	private BoardUserRepository boardUserRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	// 게시판별 숨겨진 게시글 표시
	public List<Post> findbyBoardId(long boardId, String status) {
		List<Post> list = null;
		Board board = boardRepository.findById(boardId).orElse(null);
		if (status.equals("hidden")&&board != null);
			list = postRepository.findByBoardIdAndPostDelDateIsNotNullOrderByPostCreateDateDesc(board.getBoardId());
		return list;
	}
	// 게시글 보기
	@Transactional
	public PostDto findByPostIdAtDto(Long postId) {
		Post p = postRepository.findByPostIdAndPostDelDateIsNullOrderByPostCreateDateDesc(postId);
		if(p == null) {
			return null;
		}
		String t = p.getPostTitle();
		String c = p.getPostContent();
		Long bui = p.getBoardUserId();
		Long bi = p.getBoardId();
		String en = employeeRepository
			.findById(boardUserRepository
				.findById(p.getBoardUserId())
				.get().getUserId()).get()
			.getEmployeeName();
		String s = p.getPostStatus();
		LocalDateTime pcd = p.getPostCreateDate(); 
		LocalDateTime ped = p.getPostEditDate(); 
		LocalDateTime pdd = p.getPostDelDate(); 
		PostDto pDto = new PostDto();
		pDto.setBoardId(bi);
		pDto.setBoardName(
			boardRepository.findById(bi).get().getBoardName()
		);
		pDto.setBoardUserId(bui);
		pDto.setEmployeeName(en);
		pDto.setPostContent(c);
		pDto.setPostCreateDate(pcd);
		pDto.setPostSimpleDate(
				pcd.toString().substring(0, pcd.toString().indexOf("T")));
		pDto.setPostDelDate(pdd);
		pDto.setPostEditDate(ped);
		pDto.setPostId(postId);
		pDto.setPostStatus(s);
		pDto.setPostTitle(t);
		return pDto;
	}
	// 게시글 ID로 가져오기
	public Post findById(Long postId) {
		return postRepository.findById(postId).orElse(null);
	}	
	// 게시글 수정
	@Transactional
	public int updatePost(Long postId, Long buId, String title,
		String content, MultipartFile[] files) {

		Post oldPost = postRepository.findById(postId).orElse(null);

		if (!oldPost.getBoardUserId().equals(buId)) {
			return CheckResult.NO_EXIST;
		}
		
		// 게시글 기본 정보 수정
		if(title == null || content == null) {
			return CheckResult.NO_EXIST;
		}else {
			Post newPost = new Post();
			newPost.setPostTitle(title);
			newPost.setPostContent(content);
			newPost.setPostEditDate(LocalDateTime.now());
			newPost = postRepository.save(newPost);
			if(files != null) {
				if(uploadFileService.save(newPost.getPostId(),files) != null)
					return CheckResult.SUCCESS;
				return CheckResult.PARTIAL_SUCCESS;
			}
			return CheckResult.SUCCESS;
		}
	}

	// 게시글 숨김
	public String deletePost(Long postId) {
		Post post = postRepository.findById(postId).orElse(null);
		if (post == null) {
			throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
		}

		if ("unvisible".equalsIgnoreCase(post.getPostStatus())) {
			throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
		}

		post.setPostStatus("unvisible");
		postRepository.save(post);

		return "unvisible";
	}

	// 게시글 보기
	@Transactional
	public Post findByPostId(Long postId) {
		return postRepository.findById(postId).orElseThrow(null);
	}	

	//게시글 작성
	public int savePost(Long boardId, String title, 
		String content, MultipartFile[] files, Long boardUserId) {
		if(title == null || boardId == null ||
			boardUserId == null || content == null) {
			return CheckResult.NO_EXIST;
		}
		Post newPost = new Post();
		newPost.setPostTitle(title);
		newPost.setBoardUserId(boardUserId);
		newPost.setBoardId(boardId);
		newPost.setPostContent(content);
		newPost.setPostStatus(Post.STATUS[Post.POSTTING]);
		newPost.setPostCreateDate(LocalDateTime.now());
		newPost = postRepository.save(newPost);
		if(files != null && files.length > 0) {
			if(uploadFileService.save(newPost.getPostId(),files) != null)
				return CheckResult.SUCCESS;
			return CheckResult.PARTIAL_SUCCESS;
		}
		return CheckResult.SUCCESS;
	}



//	public List<Post> findPostByWriterOrValue(User user, String keyword ,String value){
//		List<Post> result = new ArrayList<>();
//		List<Post> plist = null;
//		if(keyword.equals("writer")) {
//			plist = pRepository.findPostByWriter(value);
//		}else {
//			plist = pRepository.findPostByValue("%"+value+"%");
//		}
//		List<BoardUser> buList = uRepository.findBoardUserByUserId(user.getUserId());
//		if(buList != null) {
//			for(BoardUser butemp : buList) {
//				Long buBoardId = butemp.getBoard().getId();
//				for(Post ptemp : plist) {
//					Long pBoardId = ptemp.getBoard().getId();
//					if(pBoardId.equals(buBoardId)) {
//						result.add(ptemp);
//					};
//				}
//			}
//		}
//		return result;
//	}
	public List<Post> searchPosts(HttpServletRequest request, Long userId) {
		List<Post> result = new ArrayList<>();
		String writer = (String)request.getAttribute("writer");
		String title = (String)request.getAttribute("title");
		String content = (String)request.getAttribute("content");
		String total = (String)request.getAttribute("total");
	
		List<Board> boards = new BoardService().findByUserId(userId);
		if(total != null) {
			result = findByTotal(total, boards, userId);
		}else {
			result = findByDetail(writer, title, content, boards, userId);
		}
		return result;
	}
	private List<Post> findByTitle(String keyword,Long boardId){
		return postRepository
				.findByBoardIdAndPostTitleLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(boardId,keyword);
	}
	private List<Post> findByContent(String keyword,Long boardId){
		return postRepository
				.findByBoardIdAndPostContentLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(boardId,keyword);
	}
	private List<Post> findByTotal(String keyword, List<Board> boards,
		Long userId){
		List<Post> writerPosts = 
			assembleListByType("writer",boards,keyword);
		List<Post> titlePosts = 
			assembleListByType("title", boards, keyword);
		List<Post> contentPosts = 
			assembleListByType("content", boards, keyword);
		
		List<Post> result = duplicatesPost(writerPosts, titlePosts, "total");
		result = duplicatesPost(result, contentPosts, "total");
		return result;
	}
	
	private List<Post> duplicatesPost(List<Post> list1, List<Post> list2, String what){
		List<Post> result = null;
		if(what.endsWith("total")) {
			result = new ArrayList<>(list1);
		}else if(what.equals("detail")) {
			result = new ArrayList<>();
		}else {
			return new ArrayList<Post>();
		}
		for(Post l1e : list1) {
			boolean isEquals = false;
			for(Post l2e : list2) {
				if(l1e.getPostId().equals(l2e.getPostId())){
					isEquals = true;
					break;
				}
			}
			if(!isEquals && what.equals("total")) {
				result.add(l1e);
			}else if(isEquals && what.equals("detail")) {
				result.add(l1e);
			}
		}
		return result;
	}
	
	public Post findByBoardUserIdAndLastWritePost(Long buid) {
		return postRepository.findByBoardUserIdOrderByPostCreateDateDesc(buid).get(0);
	}
	
	private List<Post> findByDetail(String writer, String title,
		String content, List<Board> boards, Long userId){
		List<Post> writerPosts = 
			assembleListByType("writer",boards,writer);
		List<Post> titlePosts = 
			assembleListByType("title", boards, title);
		List<Post> contentPosts = 
			assembleListByType("content", boards, content);
		List<Post> result = duplicatesPost(writerPosts, titlePosts, "detail");
		result = duplicatesPost(result, contentPosts, "detail");
		return result;
	}
	
	private List<Post> assembleListByType(String type,
		List<Board> boards, String keyword) {
		List<Post> result = new ArrayList<>();
		for(Board b : boards) {
			Long bId = b.getBoardId();
			List<Post> temp = null;
			if(type.equals("write")) {
				List<Employee> eList = employeeRepository
					.findByEmployeeName(keyword);
				for(Employee e : eList) {
					BoardUser bu = boardUserRepository
						.findByUserIdAndBoardId(e.getEmployeeId(), bId);
					if(bu != null) {
						temp = postRepository
							.findByBoardUserIdOrderByPostCreateDateDesc(bu.getBoardUserId());
						if(temp != null && temp.size() > 0) {
							for(Post p : temp) {
								result.add(p);
							}
						}
					}
				}
			} else if (type.equals("title")) {
				temp = findByTitle(keyword, bId);
				if(temp != null && temp.size() > 0) {
					for(Post p : temp) {
						result.add(p);
					}
				}
			} else if (type.equals("content")) {
				temp = findByContent(keyword, bId);
				if(temp != null && temp.size() > 0) {
					for(Post p : temp) {
						result.add(p);
					}
				}
			} else {
				return temp;
			}
		}
		return result;
	}
	
	// 내가 쓴 게시글 목록 조회
	public List<PostDto> getMyPosts(Long userId) {
		List<BoardUser> boardUsers = boardUserRepository.findByUserId(userId);
		if (boardUsers == null)
			return null;
		List<PostDto> result = new ArrayList<>();
		for(BoardUser bu : boardUsers) {
			List<Post> tempList = postRepository.findByBoardUserIdOrderByPostCreateDateDesc(bu.getBoardUserId());
			if(tempList == null || tempList.size() < 1) continue;
			for(Post p : tempList) {
				PostDto element = findByPostIdAtDto(p.getPostId());
				if(element != null) {
					result.add(element);
				}
			}
		}
		return result;
	}
}
