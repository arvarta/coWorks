import { checkUserSession } from "../util/checking.js";
const groupOptionItems = document.querySelectorAll(".group_option_item ul li a");
const groupSelectBtn = document.querySelector(".group_select");
const groupOptionItem = document.querySelector(".group_option_item");

// 셀렉박스 클릭시 드롭다운 메뉴노출
export function showBoards() {
    groupSelectBtn.addEventListener("click", function () {
        const isVisible = groupOptionItem.style.display === "block";
        groupOptionItem.style.display = isVisible ? "none" : "block";
        groupSelectBtn.classList.toggle("open", !isVisible);
    });

    document.addEventListener("click", function (e) {
        if (!groupSelectBtn.contains(e.target) && !groupOptionItem.contains(e.target)) {
            groupOptionItem.style.display = "none";
            groupSelectBtn.classList.remove("open");
        }
    });
}

// 게시판 누르면 선택
export function chooseBoard() {
    groupOptionItems.forEach(function (item) {
        item.addEventListener("click", function () {
            groupSelectBtn.innerText = item.innerText;
            groupOptionItem.style.display = "none";
            groupSelectBtn.classList.remove("open");
        });
    });
}

// 공백 체크
export function checkNull(boardArgs) {
    if (!boardArgs.board) {
        throw new Error("게시판을 선택해주세요.");
    }
    if (!boardArgs.title) {
        document.querySelector(".btn_core").focus();
        throw new Error("제목을 입력해주세요.");
    }
    if (!boardArgs.content) {
        throw new Error("내용을 입력해주세요.");
    }
}

// 글쓰기
export async function write(boardArgs) {
    const res = await fetch("/board/" + boardArgs.boardId + "/post", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(boardArgs),
    });
	const data = await res.json();
	checkUserSession(data);

	// 임시
    if (res.status === 200) {
        location.href = "/post/1";
    }
}


//게시글 작성 시 첨부 파일 관리
export const uploadFileController = {
    files: [],
    inputTag: document.querySelector("input[type='file'][name='upload_file']"),

    listenerFunction() {},

    detectStart() {
        this.listenerFunction = (e) => this.inputChangeEventListener(e);
        this.inputTag.addEventListener("change", this.listenerFunction, false);
    },

    //파일 첨부 이벤트 리스너 제거
    detectStop() {
        this.inputTag.removeEventListener("change", this.listenerFunction, false);
    },

    //파일이 첨부되면 작동하는 이벤트 리스너
    inputChangeEventListener(e) {
        const files = Array.from(e.target.files);

        if (files.length === 0) {
            return;
        }

        const maxIdx = Math.max(...this.files.map((item) => item.idx), 0) + 1;
        this.files = this.files.concat(files.map((item, index) => ({ file: item, idx: maxIdx + index })));
        const fileListTag = document.querySelector(".file_list");

        files.forEach((item, index) => {
            const wrapTag = document.createElement("div");
            wrapTag.classList.add("file_wrap");

            const closeButtonTag = document.createElement("button");
            closeButtonTag.type = "button";
            closeButtonTag.classList.add("btn_close");
            closeButtonTag.dataset.idx = maxIdx + index;

            closeButtonTag.addEventListener("click", (e) => this.removeFile(e), { once: true });
            wrapTag.appendChild(closeButtonTag);

            //파일 정보가 담긴 태그 생성
            const fileInforTag = document.createElement("div");
            fileInforTag.classList.add("file_infor");

            //파일 이름이 담긴 태그 생성
            const fileNameTag = document.createElement("span");
            fileNameTag.classList.add("file_name");
            fileNameTag.innerText = item.name;

            fileInforTag.appendChild(fileNameTag);

            //파일 크기가 담긴 태그 생성
            const fileSizeTag = document.createElement("span");
            fileSizeTag.classList.add("file_size");

            //파일 크기 계산. 소수점 2자리 까지 출력됨.
            fileSizeTag.innerText = Math.ceil((item.size / 1024) * 100) / 100 + "KB";

            fileInforTag.appendChild(fileSizeTag);
            wrapTag.appendChild(fileInforTag);
            fileListTag.appendChild(wrapTag);
        });

        this.inputTag.value = "";
    },

    //첨부파일 삭제
    removeFile(e) {
        const idx = parseInt(e.target.dataset.idx);

        if (!Number.isNaN(idx) && this.files.some((item) => item.idx === idx)) {
            this.files = this.files.filter((item) => item.idx !== idx);
            e.target.closest(".file_wrap").remove();
        }
    },
};

//게시글 작성 관리 객체
export const whiteController = {
    quill: null,
    submitTag: document.querySelector("#post_submit"),
	myBoardList: [],

    listenerFunction() {},
	
	async initBoardList() {
		const id = localStorage.getItem("userInfo");
		const res = await fetch("/users/" + id + "/board")
		const data = await res.json();
		this.myBoardList = data.myBoardList;
		const boardOptions = document.querySelector(".group_option_item ul");
		const selectBtn = document.querySelector("#select_no_submit");
		const groupOptionItem = selectBtn.nextElementSibling;
		
		this.myBoardList.forEach(board => {
			const option = document.createElement("li");
			const anchor = document.createElement("a");
			anchor.innerText = board.boardName;
			anchor.classList.add = board.boardId;
			
			option.appendChild(anchor);
			boardOptions.appendChild(option);
			
			option.addEventListener("click", function () {
                selectBtn.innerText = option.innerText;
                groupOptionItem.style.display = "none";
                selectBtn.classList.remove("open");
				selectBtn.classList.add(board.boardId);
            });
		});
		
	},

    //에디터 실행
    runEditor(id) {
        if (typeof id !== "string") {
            throw new Error("파라미터 id는 문자열 이여야 합니다.");
			
        } else if (!id.trim()) {
            throw new Error("파라미터 id는 빈값을 넣을 수 없습니다.");
        }

        this.quill = new Quill("#" + id, {
            theme: "snow",
            placeholder: "내용을 입력하세요...",
            modules: {
                toolbar: [[{ header: [1, 2, 3, false] }], ["bold", "italic", "underline"], ["image"], [{ list: "ordered" }, { list: "bullet" }], ["clean"]],
            },
        });
        //게시글 제출 이벤트 리스너 등록
        this.listenerFunction = () => this.submitBoard();
        this.submitTag.addEventListener("click", this.listenerFunction);
    },
    //에디터 종료
    stopEditor() {
        this.submitTag.removeEventListener("click", this.listenerFunction);
    },

    //게시글 제출 및 검증
    async submitBoard() {
		try {
			const boardBtnTag = document.querySelector("button.group_select");

			if (!boardBtnTag || !boardBtnTag.innerText.trim() || boardBtnTag.innerText.includes("게시판을 선택해주세요")) {
			    throw new Error("게시판을 선택해 주세요.");
			}
			const titleTag = document.querySelector("input[type='text'].post_title");

			if (!titleTag || !titleTag.value.trim()) {
			    throw new Error("제목을 입력해 주세요.");
			}

			//에디터에 있는 내용 불러옴
			const content = this.quill.root.innerHTML.trim();
			const formData = new FormData();

			formData.set("board", boardBtnTag.classList[1]);
			formData.set("title", titleTag.value.trim());
			formData.set("content", content);

			uploadFileController.files.forEach((item) => {
			    formData.append("file", item.file, item.file.name);
			});

			const response = await fetch("/users/board/" + boardBtnTag.classList[1] +"/post", {
			    method: "POST",
				header: "Content-Type: multipart/formdata",
			    body: formData
			});
			const data = await response.json();
			if(data.responseCode === 200) {
				location.href = "/user/post/" + data.post.postId;
			}
				
		} catch(Err) {
			alert(Err.message);
		}
        console.log("start");

    },
};

