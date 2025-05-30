import { checkUserSession } from "../util/checking.js";

export const boardViewController = {
    replyTarget: 0,
    commentTarget: 0,

    listenerStart() {
        this.utilReplyEventListener = (e) => this.replyComment(e);
        this.utilEditEventListener = (e) => this.editComment(e);
    },

    utilReplyEventListener() {},
    utilEditEventListener() {},

    submitEventListener() {},

    setBoard(arg) {
        document.querySelector(".post_title").innerText = "임시제목"; //@notComplete
        document.querySelector(".post_info .user_profile .user_name").innerText = "임시 작성자"; //@notComplete

        document.querySelector(".post_info .date .creation_date").innerText = "작성일 : " + "작성날짜"; //@notComplete
        document.querySelector(".post_info .date .update_date").innerText = " | 최근수정 : " + "최근 수정일"; //@notComplete

        const files = /*arg.files*/ []; //@notComplete

        document.querySelector(".file_infor .file_tit .file_size").innerText = `()`; //@notComplete
    },

    generateCommentTag(
        arg = {
            commentId: 0,
            commentContent: "",
            commentDelDate: "",
            commentEditDate: "",
            commentTargetId: 0,
            commentTargetType: "",
            commentWriteDate: "",
            boardUserId: 0,
        }
    ) {
        const topTag = document.createElement("li");
        topTag.classList.add("comment_li");

        const profileTag = document.createElement("a");
        profileTag.classList.add("user_info");
        profileTag.href = "/test";

        const thumbTag = document.createElement("div");
        thumbTag.classList.add("user_thumb");

        const profileImgTag = document.createElement("img");
        profileImgTag.src = "test";

        thumbTag.appendChild(profileImgTag);

        const userNameTag = document.createElement("div");
        userNameTag.classList.add("user_profile");

		const userNameSpanTag = document.createElement("span");
		userNameTag.appendChild(userNameSpanTag);
		userNameSpanTag.classList.add("user_name");
		userNameSpanTag.innerText = "테스터";

		profileTag.appendChild(thumbTag);
		profileTag.appendChild(userNameTag);

        topTag.appendChild(profileTag);

        const dateTag = document.createElement("div");
        dateTag.classList.add("date_wrap");

        const createDateTag = document.createElement("span");
        createDateTag.classList.add("creation_date");
        createDateTag.innerText = "작성일 : " + arg.commentWriteDate;

        dateTag.appendChild(createDateTag);

        const updateDateTag = document.createElement("span");
        updateDateTag.classList.add("update_date");
        updateDateTag.innerText = arg.commentEditDate ? " | 최근수정 : " + arg.commentEditDate : "";

        dateTag.appendChild(updateDateTag);
        topTag.appendChild(dateTag);

        const contentTag = document.createElement("p");
        contentTag.classList.add("comment_content");
        contentTag.innerText = arg.commentContent;
		
        topTag.appendChild(contentTag);

        //댓글 유틸 구간 시작
        const utilTag = document.createElement("div");
        utilTag.classList.add("btn_box_small");

        const leftTag = document.createElement("div");
        leftTag.classList.add("left");

        const replyButtonTag = document.createElement("button");
        replyButtonTag.type = "button";
        replyButtonTag.innerText = "답글";
        replyButtonTag.value = arg.commentId;

        replyButtonTag.addEventListener("click", this.utilReplyEventListener);

        leftTag.appendChild(replyButtonTag);
        utilTag.appendChild(leftTag);

        const rightTag = document.createElement("div");
        rightTag.classList.add("right");

        const editButtonTag = document.createElement("button");
        editButtonTag.type = "button";
        editButtonTag.innerText = "수정";
        editButtonTag.value = arg.commentId;
        editButtonTag.addEventListener("click", this.utilEditEventListener);

        rightTag.appendChild(editButtonTag);

        const deleteButtonTag = document.createElement("button");
        deleteButtonTag.type = "button";
        deleteButtonTag.innerText = "삭제";
        deleteButtonTag.value = arg.commentId;

        const deleteFn = async () => {
            if (window.confirm("정말 삭제하시겠습니까?")) {
                const response = await fetch("/");

                if (response.status === 200) {
                    deleteButtonTag.removeEventListener("click", deleteFn);
                } else {
                    const message = await response.text();

                    alert(message);
                }
            }
        };

        deleteButtonTag.addEventListener("click", deleteFn);
        rightTag.appendChild(deleteButtonTag);
        utilTag.appendChild(rightTag);
        topTag.appendChild(utilTag);

        return topTag;
    },
	
	//댓글 삭제
	async deleteFn() {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            const response = await fetch("/");

            if (response.status === 200) {
                deleteButtonTag.removeEventListener("click", deleteFn);
            } else {
                const message = await response.text();

                alert(message);
            }
        }
    },
	
	// 대댓글 작성
	async replyComment(e) {
	    const value = parseInt(e.target.value);

	    if (this.replyTarget === 0) {
	        this.replyTarget = value;

	        const topDiv = document.createElement("div");
	        topDiv.classList.add("reply_write_area");

	        const profileTag = document.createElement("a");
	        profileTag.classList.add("user_info");
	        profileTag.href = "/test";

	        // 프로필 이미지 영역
	        const thumbTag = document.createElement("div");
	        thumbTag.classList.add("user_thumb");

	        const profileImgTag = document.createElement("img");
	        profileImgTag.src = "test";

	        thumbTag.appendChild(profileImgTag);
	        profileTag.appendChild(thumbTag); // user_thumb 먼저 append

	        // 사용자 이름 영역
	        const userNameTag = document.createElement("div");
	        userNameTag.classList.add("user_profile");

	        const userNameSpanTag = document.createElement("span");
	        userNameSpanTag.classList.add("user_name");
	        userNameSpanTag.innerText = "테스터";

	        userNameTag.appendChild(userNameSpanTag);
	        profileTag.appendChild(userNameTag); // 그 다음 user_profile append

	        topDiv.appendChild(profileTag);

	        // Quill 에디터 영역
	        const replyContainer = document.createElement("div");
	        replyContainer.id = "reply-editor";
	        replyContainer.classList.add("quill-editor-wrapper");
	        topDiv.appendChild(replyContainer);

	        const quill = new Quill(replyContainer, {
	            placeholder: "대댓글을 입력하세요...",
	            modules: {
	                toolbar: false,
	            },
	        });

	        // 제출 버튼
	        const submitButton = document.createElement("button");
	        submitButton.innerText = "대댓글 등록";
			
	        this.submitEventListener = async (e) => {
	            try {
	                const response = await fetch("/board/{boardId}/post/{PostId}/comment/{commentId}/comment/{replyId}", {
	                    method: "POST",
	                    headers: { "Content-Type": "application/json" },
	                    body: JSON.stringify({
	                        id: this.replyTarget,
	                        content: quill.root.innerHTML.trim(), // 수정된 부분
	                    }),
	                });

	                if (response.status === 200) {
	                    submitButton.removeEventListener("click", this.submitEventListener);
	                    e.target.closest(".reply_write_area").remove();
	                    this.run();
	                } else {
	                    const message = await response.text();
	                    throw new Error(message);
	                }
	            } catch (err) {
	                alert(err.message);
	            }
	        };

	        submitButton.addEventListener("click", this.submitEventListener);
	        topDiv.appendChild(submitButton);

	        // 댓글 아래 삽입
	        const commentElement = e.target.closest(".comment_li");
	        if (commentElement.nextSibling) {
	            commentElement.parentNode.insertBefore(topDiv, commentElement.nextSibling);
	        } else {
	            commentElement.parentNode.appendChild(topDiv);
	        }

	    } else {
	        // 이미 열려 있는 대댓글 폼 제거
	        document.querySelector(".reply_write_area").remove();
	        this.replyTarget = 0;
	    }
	},
	
	//댓글 수정
    async editComment(e) {
        if (this.commentTarget === 0) {
            const value = parseInt(e.target.value);

            this.commentTarget = value;

            const viewTags = e.target.closest(".comment_li, .reply_li");

            console.log(!e.target.closest(viewTags.classList.contains(".comment_li") ? "comment_modify_area" : "reply_modify_area"));

            if (!e.target.closest(viewTags.classList.contains("comment_li") ? "comment_modify_area" : "reply_modify_area")) {
                const editTags = document.createElement("div");

                console.log(viewTags);

                editTags.classList.add(viewTags.classList.contains("comment_li") ? "comment_modify_area" : "reply_modify_area");

                const profileTag = document.createElement("a");
                profileTag.classList.add("user_info");
                profileTag.href = "/test";

                const thumbTag = document.createElement("div");
                thumbTag.classList.add("user_thumb");

                const profileImgTag = document.createElement("img");
                profileImgTag.src = "test";

                thumbTag.appendChild(profileImgTag);

                const userNameTag = document.createElement("div");
                userNameTag.classList.add("user_profile");

                const userNameSpanTag = document.createElement("span");
				userNameTag.appendChild(userNameSpanTag);
                userNameSpanTag.classList.add("user_name");
                userNameSpanTag.innerText = "테스터";

				profileTag.appendChild(thumbTag);
				profileTag.appendChild(userNameTag);

                editTags.appendChild(profileTag);
				
				const editorContainer = document.createElement("div");
				editorContainer.id = "commentModi-editor";
				editTags.appendChild(editorContainer);
				
				// Quill 에디터 초기화
				const quill = new Quill(editorContainer, {		
					placeholder: "댓글을 입력하세요...",
					modules: {
					    toolbar: false,
					},
				});

                const cancelTag = document.createElement("button");
                cancelTag.type = "button";
                cancelTag.classList.add("cancel");
                cancelTag.innerText = "취소";

                cancelTag.addEventListener("click", this.utilEditEventListener, { once: true });
                editTags.appendChild(cancelTag);

                const submitTag = document.createElement("button");
                submitTag.innerText = "수정";
                editTags.appendChild(submitTag);

                this.submitEventListener = async (e) => {
                    try {
                        const response = await fetch("/", {
                            method: "모룸",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({
                                content: {
                                    id: this.commentTarget,
                                    content: editorContainer.value.trim(),
                                },
                            }),
                        });

                        //const response = new Response(null, { status: 200 });

                        if (response.status === 200) {
                            const viewTags = e.target.closest(".comment_list").querySelector(`:has(button[type='button'][value='${this.commentTarget}'])`);
                            const editTags = e.target.closest(".comment_modify_area, .reply_modify_area");

                            editTags.style.display = "none";
                            viewTags.style.display = "";
                            cancelTag.removeEventListener("click", this.utilEditEventListener);

                            this.commentTarget = 0;
                        } else {
                            const message = await response.text();

                            throw new Error(message);
                        }
                    } catch (err) {
                        console.error(err);

                        alert(err.message);
                    }
                };

                submitTag.addEventListener("click", this.submitEventListener);
                e.target.closest("ul").appendChild(editTags);
            } else {
                const editTags = e.target.closest(".comment_modify_area, .reply_modify_area");
                editTags.style.display = "";
            }

            viewTags.style.display = "none";
        } else {
            const viewTags = e.target.closest(".comment_list").querySelector(`:has(button[type='button'][value='${this.commentTarget}'])`);
            const editTags = e.target.closest(".comment_modify_area, .reply_modify_area");
            viewTags.style.display = "";
            editTags.style.display = "none";

            this.commentTarget = 0;
        }
    },
};

// 댓글 보기(불러오기)
export async function viewComment(commentView) {
   	//document.querySelector(".comment_list");
	let postId = window.location.pathname.split("/").pop();
	const res = await fetch("/users/board/post/" + postId + "/comment");
	const data = await res.json();
	checkUserSession(data);
	const commentList = data.commentList;
	const buId = data.buId;
	//댓글 전체 묶음
	const commentUI = commentView;
	
	if(Array.isArray(commentList) && commentList.length > 0){
		
	    commentList.forEach((comment) => {
			//댓글 하나
			const li = document.createElement("li");
			const replylist = document.createElement("ul");
			//댓글 작성자 프로필 묶음
			const commentWriter = document.createElement("a");
			commentWriter.classList.add("user_info");
//			commentWriter.href = "/users/" 팝업 띄우는거 모름
//			commentWriter.innerText = file.fileName;	타깃 모름
			
			
			const userThumb = document.createElement("div");
			userThumb.classList.add("user_thumb");
			
			//작성자 프로필 이미지
			const image = document.createElement("img");
			image.src = "/img/thumb_profile.svg";
			image.alt = "image";
			image.style.opacity = "1";
			
			userThumb.appendChild(image);
			commentWriter.appendChild(userThumb);
			
			//작성자 이름 틀
			const writerName = document.createElement("div");
			writerName.classList.add("user_profile");
			
			//작성자 이름
			const name = document.createElement("span");
			name.classList.add("user_name");
			name.innerText = comment.userName;
			
			//삽입
			writerName.appendChild(name);
			commentWriter.appendChild(writerName);
			
			//작성일 묶음
			const dateDiv = document.createElement("div");
			dateDiv.classList.add("date_wrap");
			
			//작성일
			const date = document.createElement("span");
			date.classList.add("creation_date");
			date.innerText = "작성일 : " + comment.writeDate;
			dateDiv.appendChild(date);
			
			if(comment.viewDate !== comment.writeDate){
				//수정일이 있을 시
				const editDate = document.createElement("span");
				editDate.classList.add("update_date");
				editDate.innerText = " | 최근수정 : " + comment.viewDate;
				dateDiv.appendChild(editDate);
			}
			
			//작성내용 묶음	
			const commentContent = document.createElement("p");
			commentContent.classList.add("comment_content");
			commentContent.innerText = comment.content;
			
			//버튼 묶음
			const btnDiv = document.createElement("div");
			btnDiv.classList.add("btn_box_small");
			
			//버튼 틀
			const leftBtn = document.createElement("div");
			leftBtn.classList.add("left");
			
			//답글
			const addBtn = document.createElement("button");
			addBtn.addEventListener("click", boardViewController.replyComment);
			addBtn.innerText = "답글";
			
			leftBtn.appendChild(addBtn);
			btnDiv.appendChild(leftBtn);
			
			if(buId === comment.userId){
				//버튼틀
				const rightBtn = document.createElement("div");
				rightBtn.classList.add("right");
				
				const editBtn = document.createElement("button");
				editBtn.addEventListener("click", boardViewController.editComment);
				editBtn.innerText = "수정";
				
				const delBtn = document.createElement("button");
				delBtn.addEventListener("click", boardViewController.deleteFn);
				delBtn.innerText = "삭제";
				
				rightBtn.appendChild(editBtn);
				rightBtn.appendChild(delBtn);
				btnDiv.appendChild(rightBtn);
			}
			
			if(comment.targetType === "post"){
				li.classList.add("comment_li");
				li.appendChild(commentWriter);
				li.appendChild(dateDiv);
				li.appendChild(commentContent);
				li.appendChild(btnDiv);
			}
			
	//		else{
		//		replylist.classList.add("reply_list");
			//	replylist.appendChild(commentWriter);
			//	replylist.appendChild(dateDiv);
			//	replylist.appendChild(commentContent);
			//	replylist.appendChild(btnDiv);
			//	li.appendChild(replylist);
		//	}
			commentUI.appendChild(li);
			
	    });
		
	}
	
	
}

export async function addComment() {
	const postId = window.location.pathname.split("/").pop();
	const content = quillComment.root.innerText;
	if (!content || content === "") {
	        alert("댓글을 입력하세요.");
	        return;
	}
	
	const comment = {
		"targetId" : postId,
		"targetType" : "post",
		"content" : content
	};
	const res = await fetch("/users/board/post/" + postId + "/comment", {
		method: "POST",
		headers : {"Content-Type":"application/json"},
		body: JSON.stringify(comment)
	});
	if (res.status === 200) {
			location.href = "/user/post/"+ comment.targetId;
	}
}


// 게시판 내의 특정 게시글 보기
export async function viewPost(postView) {
	// document.querySelector(".post_view");
	let postId = window.location.pathname.split("/").pop();
	const res = await fetch("/users/board/post/" + postId);
	const data = await res.json();
	checkUserSession(data);
	const post = data.post;
	
	postView.querySelector(".post_title").innerText = post.postTitle;
	postView.querySelector(".post_content").innerHTML = post.postContent;
	postView.querySelector(".user_name").innerText = post.employeeName;
	postView.querySelector(".creation_date").innerText = "작성일 " + getClearDate(post.postCreateDate);
	
	// 수정일이 있다면
	if(post.postEditDate) {
		const updateDate = document.createElement("span");
		updateDate.classList.add("update_date");
		updateDate.innerText = " ㅣ 최근수정 : " + getClearDate(post.postEditDate);
		postView.querySelector(".date").appendChild(updateDate);
	}
	const postFiles = data.fileList;
	// 파일이 있다면
	if(Array.isArray(postFiles) && postFiles.length > 0) {
		const attachView = document.createElement("div");
		attachView.classList.add("file_attach_view");
		
		const fileInfor = document.createElement("div");
		fileInfor.classList.add("file_infor");
		attachView.appendChild(fileInfor);
		
		const fileTit = document.createElement("span");
		fileTit.classList.add("file_tit");
		fileInfor.appendChild(fileTit);
		
		const addText = document.createElement("span");
		addText.innerHTML = "첨부 파일 <em>" + postFiles.length + "</em>개</span>";
		fileTit.appendChild(addText);
		
		const fileSize = document.createElement("span");
		fileSize.classList.add("file_size");
		fileSize.innerText = "(4.0MB)";
		fileTit.appendChild(fileSize);
		
		const downLoadAll = document.createElement("button");
		downLoadAll.classList.add("btn_downloadAll");
		downLoadAll.innerText = "모두 저장";
		fileInfor.appendChild(downLoadAll);
		
		const fileWrap = document.createElement("div");
		fileWrap.classList.add("file_wrap");
		fileWrap.style.display = "block";
		
		const fileUl = document.createElement("ul");
		fileUl.classList.add("file_list");
		
		postFiles.forEach(file => {
			const li = document.createElement("li");
			
			const fileName = document.createElement("span");
			fileName.classList.add("file_name");
			fileName.innerText = file.fileName;
			li.appendChild(fileName);
			
			const fileSize = document.createElement("span");
			fileSize.classList.add("file_size");
			fileSize.innerText = Math.ceil((file.size / 1024) * 100) / 100 + "KB"
			li.appendChild(fileSize);
			
			const btnDownload = document.createElement("button");
			btnDownload.type = "button";
			btnDownload.classList.add("btn_download");
			li.appendChild(btnDownload);
			
			fileUl.appendChild(li);
		});
		fileWrap.appendChild(fileUl);
		attachView.appendChild(fileWrap);
		
		postView.querySelector(".post_info").after(attachView);
	}
}


// 날짜예쁘게 자르기
function getClearDate(date) {
	const clearDate = date.replace("T", " ");
	return clearDate;
}
