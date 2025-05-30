import { checkNull, verifyReq, join, verifyEmail } from "../service/join.js";

let inputs = document.querySelectorAll("input");
const submitBtn = document.querySelector("button[type='submit']");
const msgs = [
	"사번을 입력해주세요",
	"비밀번호릉 입력해주세요",
	"비밀번호를 재확인해주세요",
	"이메일을 입력해주세요",
	"인증번호를 입력해주세요"
];

let authProgress = "대기중";

submitBtn.addEventListener("click", async (event) => {
    try {
        event.stopPropagation();

        if (authProgress === "대기중") {
            checkNull(inputs, msgs);
            await verifyReq(inputs[3].value.trim());
            authProgress = "전송됨";
			
        } else if (authProgress === "전송됨") {
            checkNull(inputs, msgs);

            const codeInput = document.querySelector("input[type='text']:last-of-type");

			const verifyArgs = {
                email: inputs[3].value.trim(),
                code: codeInput.value.trim()
			};
			codeInput.addEventListener('change', async () => {
				await verifyEmail(verifyArgs);
			});
			
            const joinArgs = {
                id: inputs[0].value.trim(),
                password: inputs[1].value.trim(),
            };
            await join(joinArgs);

            alert("회원가입 신청이 완료되었습니다. 관리자의 승인을 기다려주세요.");
            location.href = "/login";
        } else {
            throw new Error("오류가 발생했습니다");
        }
    } catch (err) {
        alert(err.message);
    }
});
