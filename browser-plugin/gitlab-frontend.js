// Infer current project directory from browser URL
function get_curr_browser_dir() {
    var url = window.location.pathname  // e.g. /aDogCalledSpot/cant-touch-this/tree/master/your/path/here.txt
    var split = url.split("/").filter(sub => sub !== "");
    if (split.length <= 4) {
        return ".";      // Root directory
    } else {
        return split.slice(4).join("/");
    }
}

// Place person icons next to all files on the current page with the file names specified
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

// MAIN
place_person_icons([".idea", ".gitignore"]);
console.log(get_curr_browser_dir());
