import { selectNoSubmit, selectAutoSubmit, layerPopup, UserListlayerPopup, submitItem, chooseItem, mapApiLoader } from "../service/content.js";

const selectAutoSubmitBtn = document.querySelector("#select_auto_submit");
const submitItems = document.querySelectorAll("#select_auto_submit_items ul li a");
const selectNoSubmitBtn = document.querySelector("#select_no_submit");
const chooseItems = document.querySelectorAll("#select_no_submit_items ul li a");

selectAutoSubmit(selectAutoSubmitBtn);

selectNoSubmit(selectNoSubmitBtn);
submitItem(submitItems, selectAutoSubmitBtn);
chooseItem(chooseItems, selectNoSubmitBtn);

// 일반 팝업 관련
const popButton = document.querySelectorAll(".pop_btn");
const layerPop = document.querySelector(".layer_pop");
const popCloseBtn = document.querySelector(".pop_close");

// 사용자 목록 팝업 관련
const UserListButton = document.querySelectorAll(".userList_btn");
const layerPopUserList = document.querySelector(".layer_pop_userList");

// 팝업 안 셀렉박스
let popupGroupSelect = null;
if (layerPop) {
    popupGroupSelect = layerPop.querySelector(".group_select");
}

layerPopup(popButton, layerPop, popCloseBtn, popupGroupSelect);

if (layerPopUserList) {
    UserListlayerPopup(UserListButton, layerPopUserList, popCloseBtn);
}

if (document.querySelector("#map_target")) {
    mapApiLoader("map_target");
}
