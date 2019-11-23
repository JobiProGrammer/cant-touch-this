// Red border for debugging
document.body.style.border = "5px solid red";

// Random debug button
var button = document.createElement("button");
button.innerHTML = "Press me";

function place_person_icons(filenames) {
    // Create icon
    var icon = document.createElement("img");
    icon.src = browser.runtime.getURL("icons/person.png");
    icon.style = "text-align:right; height:1em; width:auto";
    // Find right table cells
    var filename_texts = Array.from(document.querySelectorAll("#tree-slider > tbody > tr > td.tree-item-file-name > a"))
    .filter(el => filenames.includes(el.title));
    var filename_boxes = filename_texts.map(el => el.parentElement);
    // Arrange and insert elements
    for (i=0; i<filename_boxes.length; i++) {
        var divr = document.createElement("div");
        divr.style = "float:right;width:10%;";
        filename_boxes[i].append(divr);
        divr.append(icon.cloneNode());
    }
}

// Put person icon next to file object where text is ".idea"
place_person_icons([".idea", ".gitignore"]);