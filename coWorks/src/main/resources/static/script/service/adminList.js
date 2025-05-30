import { checkUserSession } from "../util/checking.js";

// 사용자 목록 받는 반복문...
function forEachList(userList, parentPart) {
	
	userList.forEach((data, idx) => {
		// <li>태그. 앞으로 반복될 노드. 이 안에 유저 정보가 들어있음
		const userInfo = document.createElement("li");
		userInfo.classList.add("pop_btn");
		userInfo.dataset.id = data.userId || 1;
		
		userInfo.addEventListener("click", (e) =>{
			memberProfileController.initList(userList);
			memberProfileController.memberSelect(e)}
		);

		// <li>태그 안에 <a href="프로필 상세로 이동" class="user_info"> 추가
		const userAnchor = document.createElement("a");
		userAnchor.classList.add("user_info");
		userInfo.append(userAnchor);

		// <a>태그 안에 <div class="user_thumb"> 추가, 프사 추가(수정 예정)
		const userThumb = document.createElement("div");
		userThumb.classList.add("user_thumb");
		userThumb.innerHTML = "<img src='/img/thumb_profile.svg' alt='image' style='opacity: 1;'>";
		userAnchor.append(userThumb);

		// <a>태그 안에 <div class="user_profile"> 추가
		const userProfile = document.createElement("div");
		userProfile.classList.add("user_profile");
		userAnchor.append(userProfile);

		// <span class="user_name">유저명</span>
		const userName = document.createElement("span");
		userName.classList.add("user_name");
		userName.innerText = userList[idx].userName;
		userProfile.append(userName);

		// <span class="user_position">과장</span>
		const userPosition = document.createElement("span");
		userPosition.classList.add("user_position");
		userPosition.innertext = data.position;
		userProfile.append(userPosition);

		// <div class="user_subscript"><p>자기소개를 입력해주세요.</p></div>
		const userSubscript = document.createElement("div");
		userSubscript.classList.add("user_subscript");
		if(!data.userMessage) {
			data.userMessage = "자기소개를 입력해주세요.";
		}
		userSubscript.innerHTML = "<p>" + data.userMessage + "</p>";
		
		userProfile.append(userSubscript);

		// 반복될 노드를 listDiv에 append
		parentPart.appendChild(userInfo);
		
	});
}

// 테이블 반복문
function forEachTable(responseList, parentTable, isBoard) {
    const tbody = parentTable.querySelector("tbody"); // tbody 선택

    responseList.forEach(data => {
        const row = tbody.insertRow(); // tbody에 행 추가

        if (isBoard === true) {
            row.insertCell(0).textContent = data.boardName;
            row.insertCell(1).textContent = data.boardInfo;

            row.addEventListener("click", () => {
                location.href = "/admin/board/" + data.boardId;
            });
        } else {
            row.insertCell(0).textContent = data.proposalBoardName;
            row.insertCell(1).textContent = data.proposalBoardInfo;
        }
    });
}

// nodata : list
function showNodata(appendParent, msg) {
	const nodataLi = document.createElement("li");

	const errDiv = document.createElement("div");
	errDiv.classList.add("err_message");
	nodataLi.append(errDiv);

	const nodataP = document.createElement("p");
	nodataP.classList.add("nodata");
	nodataP.innerText = msg;

	errDiv.append(nodataP);	
	nodataLi.style.textAlign = "center";
	appendParent.appendChild(nodataLi);
}

// nodata : table
function showTableNodata(appendTable, msg) {
	const row = appendTable.insertRow();
	const cell = row.insertCell(0);
	cell.colSpan = 2;
	cell.style.backgroundColor = "#f8f9fb";

	const errDiv = document.createElement("div");
	errDiv.classList.add("err_message");
	cell.append(errDiv);

	const nodataP = document.createElement("p");
	nodataP.classList.add("nodata");
	nodataP.innerText = msg;

	errDiv.append(nodataP);
}

// 페이지네이션
const PER_PAGE = 10;

function paginatetion(container, totalCount, currentPage, onPageClick) {
    const totalPage = Math.max(1, Math.ceil(totalCount / PER_PAGE));

    // 페이지 컨테이너 비우기
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

// 프로필
export const memberProfileController = {
    targetId: 0,
    userList: [],

    popupChange() {
        const popupTag = document.querySelector(".layer_pop");
        popupTag.style.display = popupTag.style.display === "none" ? "block" : "none";
    },
	
	initList(uList) {
		this.userList = uList;
	},

    memberSelect(e) {
       // console.log(e.target);
        const id = parseInt(e.currentTarget.dataset.id);

        if (id > 0) {
            this.targetId = id;
            this.popupChange();
            this.popupValueChange(
				this.userList.find((f) => f.userId === id)
			);
        }
    },

    // mode 파라미터는 가입신청 승인/거절
    async confirmJoin(mode) {
        if (window.confirm(`가입신청을 ${mode === "allow" ? "승인" : "거절"} 하시겠습니까?`)) {
			const chooseMethod = mode === "allow" ? "POST" : "DELETE"
            const response = await fetch(`/admins/user/${this.targetId}/sign`, {
                method: chooseMethod,
                headers: { "Content-Type": "application/json" }});

            if (response.status === 200) {
                alert(`회원 가입이 ${mode === "allow" ? "승인" : "거절"} 되었습니다.`);
                window.location.reload();
            } else {
                const message = await response.text();
                throw new Error(message);
            }
        }
    },

    popupValueChange(data) {
        const userProfileTag = document.querySelector("#user_modal_profile");
        userProfileTag.querySelector(".user_name").innerText = data.userName;
        userProfileTag.querySelector(".user_subscript p").innerText = data.userMessage;
		console.log(data.userName);

        // 가져오기...
        const forEachList = [
			data.userDepartment, data.userPosition, data.userTel, data.userMail, data.userBirth
		];
		
		console.log(forEachList);

        document.querySelectorAll(".infor_list li > p").forEach((item, index) => {
            item.innerText = forEachList[index];
        });

        document.querySelector("button[type='button'].pop_close").addEventListener(
            "click", () => {
                this.targetId = 0;
                this.popupChange();
            },
            { once: true }
        );

        if (window.location.pathname.includes("/join-list")) {
            document.querySelector(".btnList button.btn_primary").addEventListener("click", () => this.confirmJoin("allow"));
            document.querySelector(".btnList button.deny").addEventListener("click", () => this.confirmJoin("deny"));
        }
    },
};
// 수정된 회원가입 신청 내역 받아오기
export async function getSignInUsers(signIns, countEm, paginationContainer) {
	const res = await fetch("/admins/user/list?userStatus=stand");
	const data = await res.json();
	checkUserSession(data);

	const totalCount = data.totalCount;
	const userList = data.userList;
	countEm.innerText = totalCount;

	function renderPage(page) {
		signIns.innerHTML = "";
		const pageData = userList.slice((page - 1) * PER_PAGE, page * PER_PAGE);
		if (pageData.length > 0) {
			forEachList(pageData, signIns);
		} else {
			showNodata(signIns, "승인 대기 중인 사용자가 존재하지 않습니다");
		}
		paginatetion(paginationContainer, totalCount, page, renderPage);
	}

	renderPage(1);
}
// 유저 목록 받아오기
export async function userList(listUl, countEm, paginationContainer) {
	const res = await fetch("/admins/user/list");
	const data = await res.json();
	checkUserSession(data);

	const totalCount = data.totalCount;
	countEm.innerText = totalCount;

	async function renderPage(page) {
		  listUl.innerHTML = "";

		  const res = await fetch(`/admins/user/list?page=${page}&size=${PER_PAGE}`);
		  const data = await res.json();

		  const userList = data.userList;
		  const totalCount = data.totalCount;

		  if (userList.length > 0) {
		    forEachList(userList, listUl);
		  } else {
		    showNodata(listUl, "사용자가 존재하지 않습니다");
		  }

		  paginatetion(paginationContainer, totalCount, page, renderPage);
		}

	renderPage(1);
}

// 수정된 회원탈퇴 신청 내역 받아오기
export async function getSignOutUsers(signOuts, countEm, paginationContainer) {
	const res = await fetch("/admins/user/list?userStatus=delete");
	const data = await res.json();
	checkUserSession(data);

	const userList = data.userList;
	const totals = data.totalCount;
	countEm.innerText = totals;

	function renderPage(page) {
		signOuts.innerHTML = "";
		const pageData = userList.slice((page - 1) * PER_PAGE, page * PER_PAGE);
		if (pageData.length > 0) {
			forEachList(pageData, signOuts);
		} else {
			showNodata(signOuts, "탈퇴 신청한 사용자가 존재하지 않습니다");
		}
		paginatetion(paginationContainer, totals, page, renderPage);
	}

	renderPage(1);
}

// 게시판 리스트 목록
export async function boardList(listTable) {
    const res = await fetch("/admins/board", {
        method: "GET",
        headers: { "Content-Type": "application/json" },
    });

    const data = await res.json();
	checkUserSession(data);
    const boards = data.boardList;
	console.log(data);
	let isBoard = true;
	// if (boards.length > 0) {
    if (boards) {
        forEachTable(boards, listTable, isBoard);
    } else {
		showTableNodata(listTable, "게시판이 존재하지 않습니다");
    }
}


// 게시판 추가 신청 내역
export async function getAddLists(addLists) {
    const res = await fetch("/admins/proposal/list");
    const data = await res.json();
	checkUserSession(data);
    const proposals = data.proposalList;

    if (proposals.length > 0) {
        forEachTable(proposals, addLists);
    } else {
        showTableNodata(addLists, "추가 신청 내역이 존재하지 않습니다");
    }
}