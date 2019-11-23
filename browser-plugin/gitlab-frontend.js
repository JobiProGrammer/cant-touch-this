// Red border for debugging
document.body.style.border = "5px solid red";

// Random debug button
var button = document.createElement("button");
button.innerHTML = "Press me";

function place_person_icon(filename) {
    // Create icon
    var icon = document.createElement("img");
    icon.src = browser.runtime.getURL("icons/person.png");
    icon.style = "text-align:right; height:1em; width:auto";
    // Find right table cell
    var filename_text = Array.from(document.querySelectorAll("#tree-slider > tbody > tr > td.tree-item-file-name > a"))
    .find(el => el.title === filename);
    var filename_box = filename_text.parentElement;
    // Arrange and insert elements
    var divr = document.createElement("div");
    divr.style = "float:right;width:10%;";
    filename_box.append(divr);
    divr.append(icon);
}

// Put person icon next to file object where text is ".idea"
place_person_icon(".idea");
//console.log(Array.from(document.querySelectorAll("#tree-slider > tbody > tr > td.tree-item-file-name > a")).find(el => el.title===".idea"));