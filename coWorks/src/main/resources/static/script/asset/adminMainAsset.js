import { buttonAnchor } from "../service/adminMain.js";

const cardColumns = document.querySelectorAll("div.card_column");
const itemData = document.querySelectorAll(".item_data strong");

buttonAnchor(cardColumns, itemData);
