import { checkUserSession } from "../util/checking.js";
// 이동할 곳 리스트
/*
	0 게시글 목록
	1 게시판 정보 관리
	2 게시판 로그
	3 숨겨진 게시글
	4 1:1문의 목록	
*/
const anchors = [
	"/post",
	"/info",
	"/hidden"
];
const path = window.location.pathname;
const id = path.split(path.charAt(path.lastIndexOf("/")))[3];

// 상단 게시판 정보 설정 및 이동
export async function setBoardTitle(divTitle, divSubtitle, tabList) {
	const res = await fetch("/admins/board/" + id);
	const data = await res.json();
	checkUserSession(data);
	const board = data.board;
	
	divTitle.innerText = board.boardName;
	divSubtitle.innerText = board.boardInfo;
	
	tabList.forEach((item, idx) => {
		item.addEventListener('click', () => {
			location.href = "/admin/board/" + id + anchors[idx];
		})
	});
}

// 게시판 게시글 목록
export async function boardPostList(postTable) {
	const res = await fetch("/admins/board/" + id);
	const data = await res.json();
	checkUserSession(data);
	const board = data.board;
	const posts = board.posts;

	if(posts.length > 0) {
		posts.forEach(post => {
			const row = postTable.insertRow();
			// 제목
			row.insertCell(0).textContent = post.title;
			// 작성자
			row.insertCell(1).textContent = post.boardUserId;
			// 날짜
			row.insertCell(2).textContent = post.createdDate;
			// 누르면 해당 글로..........?
			row.addEventListener('click', () => {
				location.href= "/board/" + board.boardId;
			});
		});
	} else {
        const row = postTable.insertRow();
        const cell = row.insertCell(0);
        cell.colSpan = 5;
        cell.textContent = "아직 들어온 신고가 없습니다.";
        cell.style.textAlign = "center";
    }	
}

// 게시판 정보 관리
export async function boardManage(postTitle, userList, blind) {
	const res = await fetch("/admins/board/" + id);
	const data = await res.json();
	const board = data.board;
	
	postTitle[0].value = board.boardName;
	postTitle[1].value = board.boardInfo;
	
	
	const resList = await fetch("/admins/user/list");
	const dataList = await resList.json();
	
	// 사용자 목록
	const empList = dataList.employeeList;
	const userSelectUl = document.querySelector(".user_select");
	
	empList.forEach(emp => {
		const li = document.createElement("li");
		const userDiv = document.createElement("div");
		userDiv.classList.add("check_wrap");
		
		const checker = document.createElement("input");
		checker.type = "checkbox";
		checker.id = emp.employeeID;
		
		li.appendChild(userDiv);
	});
		
/*
	if(empList) {
		empList.forEach((user, idx) => {
			// <div class="user_wrap">
			const userWrap = document.createElement("div");
			userWrap.classList.add("user_wrap");
			
			// <div class="user_name">윤종두</div>
			const userName = document.createElement("div");
			userName.classList.add("user_name");
			userName.innerText = user.employeeName;
			userWrap.appendChild(userName);
			
			// <button class="btn_close"></button>
			const closeBtn = document.createElement("button");
			closeBtn.classList.add("btn_close");
			userWrap.appendChild(closeBtn);
			
			userList.appendChild(userWrap);
			
			// 기존 운영자 삭제
			closeBtn.addEventListener('click', () => {
				if(confirm("정말로 " + maganger.name + "님을 운영자에서 해임하시겠습니까?")) {
					managers.splice(idx, 1);
					userWrap.remove();
				}
			});
		});
	}
 */	
	
	// const submitBtn = document.querySelector(".btn_primary");
	
}


// 숨겨진 게시글
export async function boardHidden() {
	// 컨트롤러에서 추가 필요
}

// 게시판 내 게시글 목록 조회
export async function getPostLists(postLists) {
    // 1번째 게시판이 기본값
    const res = await fetch("/admins/board/" + id + "/post" );
    const data = await res.json();
    const posts = data.postList;
	checkUserSession(data);
	
    if (Array.isArray(posts) && posts.length > 0) {
        posts.forEach((post) => {
            const row = postLists.insertRow();
            // 제목
            row.insertCell(0).textContent = post.title;
            // 작성자
            row.insertCell(1).textContent = post.writer;
            // 날짜
            row.insertCell(2).textContent = post.createdAt;

            row.addEventListener("click", () => {
                location.href = "/post-view?postId=" + post.boardId;
            });
        });
    } else {
        const row = postLists.insertRow();
        const cell = row.insertCell(0);
        cell.colSpan = 3;
		
        const errDiv = document.createElement("div");
		errDiv.classList.add("err_message");
		cell.append(errDiv);
		
		const nodataP = document.createElement("p");
		nodataP.classList.add("nodata");
		nodataP.innerText = "게시글이 존재하지 않습니다.";
		
		errDiv.append(nodataP);
    }
}