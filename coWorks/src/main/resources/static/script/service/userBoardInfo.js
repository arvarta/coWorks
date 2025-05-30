import { checkUserSession } from "../util/checking.js";
// 페이지네이션
const PER_PAGE = 10;

function paginatetion(container, totalCount, currentPage, onPageClick) {
    const totalPage = Math.max(1, Math.ceil(totalCount / PER_PAGE));
	
	container.innerHTML = "";

    const pageFirst = document.createElement("a");
    pageFirst.className = "page_first";
    pageFirst.addEventListener("click", () => onPageClick(1));
    container.appendChild(pageFirst);

    const pagePrev = document.createElement("a");
    pagePrev.className = "page_prev";
    pagePrev.addEventListener("click", () => {
        if (currentPage > 1) onPageClick(currentPage - 1);
    });
    container.appendChild(pagePrev);

    const pageNumber = document.createElement("span");
    pageNumber.className = "page_number";

    for (let i = 1; i <= totalPage; i++) {
        const page = document.createElement("a");
        page.textContent = i;
        if (i === currentPage) page.classList.add("on");
        page.addEventListener("click", () => onPageClick(i));
        pageNumber.appendChild(page);
    }

    container.appendChild(pageNumber);

    const pageNext = document.createElement("a");
    pageNext.className = "page_next";
    pageNext.addEventListener("click", () => {
        if (currentPage < totalPage) onPageClick(currentPage + 1);
    });
    container.appendChild(pageNext);

    const pageLast = document.createElement("a");
    pageLast.className = "page_last";
    pageLast.addEventListener("click", () => onPageClick(totalPage));
    container.appendChild(pageLast);
} 

// 게시판 내 게시글 목록 조회
export async function getPostLists(postLists, paginationContainer) {
    // 1번째 게시판이 기본값
	let boardId = window.location.pathname.split("/").pop();
	if(!boardId || boardId === "main") {
		boardId = 1;
	}
	
	async function renderPage(page) {
		postLists.innerHTML = "";
		const colgroup = document.createElement("colgroup");
		const col = document.createElement("col");
		const col120 = document.createElement("col");
		const col100 = document.createElement("col");
		col120.style.width = "120px";
		col100.style.width = "100px";
		
		colgroup.appendChild(col);
		colgroup.appendChild(col120);
		colgroup.appendChild(col100);
		
		postLists.appendChild(colgroup);
		
	    const res = await fetch(`/users/board/${boardId}?page=${page}`);
	    const data = await res.json();
		checkUserSession(data);
		
		const totalCount = data.totalCount;
	    const postList = data.postList;
		const boardInfo = data.boardInfo;
		
		const boardTitle = document.querySelector("div.boardTop_wrap div");
		boardTitle.innerText = boardInfo.boardName;
		document.querySelector(".board_subscript").innerText = boardInfo.boardInfo;
		
//		const pageData = postList.slice((page - 1) * PER_PAGE, page * PER_PAGE);
		if (postList.length > 0) {
			postList.forEach((post) => {
			    const row = postLists.insertRow();
			    // 제목
			    row.insertCell(0).textContent = post.postTitle;
			    // 작성자
			    row.insertCell(1).textContent = post.employeeName;
			    // 날짜
			    row.insertCell(2).textContent = post.postSimpleDate;

			    row.addEventListener("click", () => {
			        location.href = "/user/post/" + post.postId;
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
		paginatetion(paginationContainer, totalCount, page, renderPage);
	}
	renderPage(1);
	
}

// 검색
export async function searchByKeywords() {}

// 게시판 추가 신청 사용자 목록
export async function getPurposeUserList() {}

// 게시판 추가 신청 제출
export async function purposeToAdmin() {}

// 이용 가능한 게시판 리스트 레프트
export async function getMyBoardList(boardMenu) {
	const id = localStorage.getItem("userInfo");
	const res = await fetch("/users/" + id + "/board");
	const data = await res.json();
	checkUserSession(data);
	const boardList = data.myBoardList;
	
	const pathId = window.location.pathname.split("/").pop();
	
	boardList.forEach(board => {
		// <li class="menu_item">
		const boardLi = document.createElement("li");
		boardLi.classList.add("menu_item");
		
		const boardAnchor = document.createElement("a");
		boardAnchor.href = "/user/board/" + board.boardId;
		boardAnchor.innerText = board.boardName;
		boardLi.appendChild(boardAnchor);
		
		if(pathId === board.boardId) {
			boardLi.classList.add("on");
			
		} else {
			boardLi.classList.remove("on");
		}
		boardMenu.appendChild(boardLi);
		
	});
}