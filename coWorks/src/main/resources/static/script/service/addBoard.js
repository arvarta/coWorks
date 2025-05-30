export const addBoardTargetController = {
    fullMemberList: [],

    adminCheckList: [],
    userCheckList: [],

    mode: "",

    run() {
        const buttonTag = document.querySelector(".pop_con .btn_core");

        buttonTag.addEventListener("click", (e) => this.checkSubmit(e));

        const submitTag = document.querySelector(".btn_primary");

        submitTag.addEventListener("click", () => this.requestSubmit());
		
		const userStatus = {
			"userStatus":"allow"
		};
		
        fetch("/admins/user", {
            method: "GET",
            headers: {"Content-Type": "application/json"},
			body: JSON.stringify(userStatus)
        })
            .then((res) => {
                console.log(res);
                return res.text();
            })
            .then((list) => {
                console.log(list);
            });
    },

    setMode(value) {
        this.mode = value;
        this.setCheckList();
    },

    getCheckList() {
        return Array.from(document.querySelectorAll(".user_select li input[type='checkbox']:checked")).map((item) => {
            const id = parseInt(item.value.substring(0, item.value.indexOf("||")).replace(/\D/g, ""));
            const userName = item.value.substring(item.value.indexOf("||") + 2);

            return {
                id,
                userName,
            };
        });
    },

    setCheckList() {
        document.querySelectorAll(".user_select li input[type='checkbox']").forEach((item) => {
            if (this.mode === "admin" && this.adminCheckList.some((f) => f.id + "||" + f.userName === item.value)) {
                item.checked = true;
            } else if (this.mode === "user" && this.userCheckList.some((f) => f.id + "||" + f.userName === item.value)) {
                item.checked = true;
            } else {
                item.checked = false;
            }
        });
    },

    clearCheckList() {
        document.querySelectorAll(".user_select li input[type='checkbox']:checked").forEach((item) => {
            item.checked = false;
        });
    },

    checkSubmit() {
        const checkList = this.getCheckList();

        document.querySelector(".layer_pop_userList").style.display = "none";

        if (this.mode === "admin") {
            this.adminCheckList = checkList;
        } else if (this.mode === "user") {
            this.userCheckList = checkList;
        } else {
            throw new Error("사용자 또는 운영자 유형이 아닙니다.");
        }

        const listTag = document.querySelector(`.option .user_list.${this.mode}_div`);

        listTag.querySelectorAll(".user_wrap > button.btn_close").forEach((item) => {
            item.click();
        });

        listTag.innerText = "";

        checkList.forEach((item) => {
            const wrapTag = document.createElement("div");
            wrapTag.classList.add("user_wrap");

            const nameTag = document.createElement("div");
            nameTag.classList.add("user_name");
            nameTag.innerText = item.userName;

            wrapTag.appendChild(nameTag);

            const closeButtonTag = document.createElement("button");
            closeButtonTag.classList.add("btn_close");
            closeButtonTag.type = "button";
            closeButtonTag.value = item.id.toString() + "||" + this.mode;

            closeButtonTag.addEventListener(
                "click",
                (e) => {
                    const { value } = e.target;

                    const id = parseInt(value.substring(0, value.indexOf("||")).replace(/\D/g, ""));
                    const mode = value.substring(value.indexOf("||") + 2);

                    if (mode === "admin") {
                        this.adminCheckList = this.adminCheckList.filter((item) => item.id !== id);
                    } else {
                        this.userCheckList = this.userCheckList.filter((item) => item.id !== id);
                    }

                    e.target.closest(".user_wrap").remove();
                },
                { once: true }
            );

            wrapTag.appendChild(closeButtonTag);

            listTag.appendChild(wrapTag);
        });
    },

    async requestSubmit() {
        try {
            const titleTag = document.querySelector(".post_title[name='title']");
            const infoTag = document.querySelector(".post_title[name='info']");
            const commentTag = document.querySelector(".post_title[name='comment']");

            if (!titleTag || !titleTag.value.trim()) {
                throw new Error("게시판 제목을 입력해 주세요.");
            } else if (!infoTag || !infoTag.value.trim()) {
                throw new Error("게시판 설명을 입력해 주세요.");
            } else if (!commentTag || !commentTag.value.trim()) {
                throw new Error("게시판 신청 코멘트를 입력해 주세요.");
            }

            if (this.adminCheckList.length === 0) {
                throw new Error("게시판 운영자를 선택해 주세요.");
            }

            const response = await fetch("/proposals", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    title: titleTag.value.trim(),
                    info: infoTag.value.trim(),
                    comment: commentTag.value.trim(),
                    admin: this.adminCheckList.map((item) => item.id),
                    user: this.userCheckList.map((item) => item.id),
                }),
            });

            if (response.status === 200 || response.status === 201) {
                alert("정상적으로 신청되었습니다.");

                window.location.href = "/";
            } else {
                const message = await response.text();

                throw new Error(message);
            }
        } catch (err) {
            alert(err.message);
        }
    },
};
