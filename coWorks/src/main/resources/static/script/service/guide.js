/*
	이 파일은 자바스크립트 가이드 템플릿 파일입니다
	1. JavaScript 파일 경로(service, asset)
	2. import/export 하는 방법
	3. 서버에 fetch를 보내는 방법
	4. async -> await를 하는 방법
	5. 서버에서 받은 데이터를 사용하는 방법
*/

// 1. JavaScript 파일 경로
/*
	현재 자바스크립트 파일 경로 구조는 다음과 같습니다
	script
	ㄴ asset
	ㄴ service
	
	asset은 버튼, div 등을 가져와서 함수에 파라미터로 넣는 역할입니다
	service는 서버와 주고받는 일, 공백 확인 등의 함수들을 담는 역할입니다
	따라서 service에서 함수를 만들고, 그걸 asset에 넘겨줘야합니다
	마치 service 클래스를 만들고 controller에서 그걸 쓰는 것처럼요!
*/

// 2. import/export 하는 방법
/*
	<<export>>
	함수의 앞에 export를 붙입니다
	export를 붙인 함수는 이제 asset에서 가져와서 사용할 수 있어요
*/
export function forGuide(buttonA) {
    buttonA.addEventListener("click", () => {
        alert("클릭했어요");
    });
}
/*
	<<import>>
	파일의 최상단에 import를 해줍니다
	import java.util.* 하는 것처럼 말이에요!
	이 파일에서는 임시로 여기 뒀지만 실제로는 최상단에 붙여주세요
	
	import { 가져올함수명 } from "../서비스파일명.js";
	한번에 여러개의 함수도 가져올 수 있어요
	asset과 service 파일 경로가 서로 다르기때문에 앞에 ../ 붙여주세요
*/
//@error 함수 forGuide()는 이미 선언되었는데 또 import로 불러와서 선언 할 수 없음.
//import { forGuide } from "guide.js";

/*
	모듈 파일은 일반 자바스크립트랑 조금 다릅니다
	<script type='module' src="script/asset/에셋파일명.js"></script>
	html파일 head 안에 넣어주시면 돼요. script 타입을 모듈로 하고 가져와야해요!
	모듈 타입의 스크립트는 DOM컨텐츠로드가 자동으로 됩니다. DOM컨텐츠로드 안넣어도댐
	
	주의) 모듈 파일은 서버를 켠 상태에서만 정상적으로 작동 가능해요
	테스트를 하고싶다면 스북이 localhost8080 서버 켜시고 테스트합시다
*/

// 3. 서버에 fetch를 보내는 방법
/*
	json 형식으로 서버에 보낼거에요
	json 형식은 java의 Map과 비슷하게 Key:Value로 되어있습니다
	
	<<asset>>
	asset에서 json을 만들고 만든 그 json을 파라미터로 넣어줍시다
	argInputA, argInputB는 각각 예시입니다
*/
const argInputA = document.querySelector("input[type='text']");
const argInputB = document.querySelector("p.post_title");
const json = {
    argA: argInputA.value.trim(),
    argB: argInputB.innerText,
};

/*
	<<service>>
	service에서 함수를 만들거에요 위에서 만든 json을 파라미터로 받아요
	fetch로 보낼때는 앞에 async를 붙여서 비동기로 만들거에요
	붙이지 않는 방법도 있지만 너무 복잡해져서 async를 씁니다..
	설명하면 너무 길어지니까 다음에~~!!
	
	const 응답 = await 페치('링크', json)의 형식입니다
	/보낼곳: 컨트롤러로 보낼 링크입니다. api를 참고하면 좋습니다
	method: POST, PUT, DELETE 3개 중 1개입니다 마찬가지로 api를 참고해주세요
*/
export async function sendFetchEx1(json) {
    const response = await fetch("/보낼곳", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(json),
    });
}

// 4. async -> await를 하는 방법
/*
	await는 서버에서 응답이 올 때까지 기다리게 합니다
	응답이 오면 기다리는 것을 멈추고 수행해요
*/
export async function sendFetchEx2(json) {
    const response = await fetch("/보낼곳", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(json),
    });

    // 응답이 오면 res를 json으로 바꿈. 이제 받은 데이터를 사용할 수 있어요!
    const data = response.json();

    // 5. 서버에서 받은 데이터를 사용하는 방법
    // 이것은 예시입니다 : 응답이 상태가 201이라면
    if (response.status === 201) {
        // 할 일
    } else {
        // 아니라면 예외를 던지기. 서버에서 받은 메시지를 사용할 수 있어요
        throw new Error(data.message);
    }
}

// ex. 함수를 import해서 사용
import { sendFetchEx2 } from "guide.js";

document.querySelector("button").addEventListener("click", async () => {
    sendFetchEx2(json);
});
