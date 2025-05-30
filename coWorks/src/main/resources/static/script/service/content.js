// 셀렉박스 클릭시 드롭다운 메뉴 노출 (자동제출)
export function selectAutoSubmit(selectAutoSubmitBtn) {
    // if (!selectAutoSubmitBtn) return; //방어코드
    if (selectAutoSubmitBtn) {
        const groupOptionItem = selectAutoSubmitBtn.nextElementSibling;

        selectAutoSubmitBtn.addEventListener("click", function (e) {
            e.stopPropagation(); // 이벤트 버블링 방지
            const isVisible = false;
            if (groupOptionItem.style.display === "block") {
                isVisible = true;
            }
            groupOptionItem.style.display = isVisible ? "none" : "block";
            selectAutoSubmitBtn.classList.toggle("open", !isVisible);
        });

        document.addEventListener("click", function (e) {
            if (!selectAutoSubmitBtn.contains(e.target) && !groupOptionItem.contains(e.target)) {
                groupOptionItem.style.display = "none";
                selectAutoSubmitBtn.classList.remove("open");
            }
        });
    }
    // if (!groupOptionItem) return; //드롭다운 요소가 없
}
// 누르면 선택
export function submitItem(submitItems, selectAutoSubmitBtn) {
    if (selectAutoSubmitBtn) {
        const groupOptionItem = selectAutoSubmitBtn.nextElementSibling;

        submitItems.forEach(function (item) {
            item.addEventListener("click", function () {
                selectAutoSubmitBtn.innerText = item.innerText;
                groupOptionItem.style.display = "none";
                selectAutoSubmitBtn.classList.remove("open");
            });
        });
    }
}

// 셀렉박스 클릭시 드롭다운 메뉴 노출 (자동제출X)
export function selectNoSubmit(selectNoSubmitBtn) {
    if (selectNoSubmitBtn) {
        const groupOptionItem = selectNoSubmitBtn.nextElementSibling;

        selectNoSubmitBtn.addEventListener("click", function (e) {
            e.stopPropagation(); // 이벤트 버블링 방지
            const isVisible = false;
            if (groupOptionItem.style.display === "block") {
                isVisible = true;
            }
            groupOptionItem.style.display = isVisible ? "none" : "block";
            selectNoSubmitBtn.classList.toggle("open", !isVisible);
        });

        document.addEventListener("click", function (e) {
            if (!selectNoSubmitBtn.contains(e.target) && !groupOptionItem.contains(e.target)) {
                groupOptionItem.style.display = "none";
                selectNoSubmitBtn.classList.remove("open");
            }
        });
    }
}

// 누르면 선택
export function chooseItem(chooseItems, selectNoSubmitBtn) {
    if (selectNoSubmitBtn) {
        const groupOptionItem = selectNoSubmitBtn.nextElementSibling;

        chooseItems.forEach(function (item) {
            item.addEventListener("click", function () {
                selectNoSubmitBtn.innerText = item.innerText;
                groupOptionItem.style.display = "none";
                selectNoSubmitBtn.classList.remove("open");
            });
        });
    }
}

// 레이어 팝업 노출
export function layerPopup(popButton, layerPop, popCloseBtn, popupGroupSelect) {
    popButton.forEach((btn) => {
        btn.addEventListener("click", function (e) {
            e.stopPropagation();
            layerPop.style.display = "block";
			console.log('pop-up');

            // 팝업 안 셀렉박스 바인딩 (팝업 열릴 때마다)
            if (popupGroupSelect) {
                groupSelect(popupGroupSelect);
            }
        });
    });
    if (popCloseBtn && layerPop) {
        popCloseBtn.addEventListener("click", function () {
            layerPop.style.display = "none";
        });
    }
}
// 멤버 레이어 팝업 노출
export function UserListlayerPopup(UserListButton, layerPopUserList, popCloseBtn) {
    UserListButton.forEach((btn) => {
        btn.addEventListener("click", function (e) {
            e.stopPropagation();
            layerPopUserList.style.display = "block";
        });
    });

    if (popCloseBtn && layerPopUserList) {
        popCloseBtn.addEventListener("click", function () {
            layerPopUserList.style.display = "none";
        });
    }
}

export async function getBoard() {
    try {
        const profileFn = async () => {
            const response = await fetch("/");

            if (response.status === 200) {
                return await response.json();
            } else {
                const message = await response.text();

                console.error(message);
                throw new Error("프로필 로드중 오류 발생.");
            }
        };

        const boardFn = async () => {
            const response = await fetch("/");

            if (response.status === 200) {
                return await response.json();
            } else {
                const message = await response.text();

                console.error(message);
                throw new Error("게시글 로드중 오류 발생.");
            }
        };

        const [profile, board] = await Promise.all([profileFn(), boardFn()]);
        const profileImgTag = document.querySelector(".user_thumb > img");
        profileImgTag.src = profile.src;

        const userNameTag = document.querySelector(".user_profile .user_name");
        userNameTag.innerText = profile.memNm;

        const userGradeTag = document.querySelector(".user_profile .user_grade");
        userGradeTag.innerText = profile.grade.toUpperCase();

        const gradeClassNameObj = {
            bronze: "grade_b",
            slive: "grade_s",
            gold: "grade_g",
            platinum: "grade_p",
            diamond: "grade_d",
        };

        if (profile.grade in gradeClassNameObj) {
            userGradeTag.classList.add(gradeClassNameObj[profile.grade]);
        }

        const titleTag = document.querySelector(".post_title");
        titleTag.innerText = board.title;

        const creationDateTag = document.querySelector(".creation_date");
        creationDateTag.innerText = "작성일 : " + board.createDate;

        const updateDateTag = document.querySelector(".update_date");
        updateDateTag.innerText = board.updateDate ? "최근수정 : " + board.updateDate : "";
		
    } catch (err) {
        alert(err.message);

        window.history.back();
    }
}

//네이버 지도 api 실행 함수
export function mapApiLoader(id) {
    if (typeof id !== "string" || !id.trim()) {
        throw new Error("파라미터에 mapApi를 적용할 아이디를 넣어 주세요.");
    }

    const targetTag = document.querySelector("#" + id.replace("#", ""));
    if (!targetTag) {
        throw new Error("해당되는 id가 없습니다. 혹시 없는 id를 입력하셨나요?");
    }

    window.navermap_authFailure = function (...arg) {
        console.error(arg);
    };
    new naver.maps.Map(targetTag, {
        center: new naver.maps.LatLng(37.3595704, 127.105399),
        zoom: 10,
    });
}
