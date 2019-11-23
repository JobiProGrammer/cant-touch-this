// Infer current project directory from browser URL
function get_curr_browser_dir() {
    var url = window.location.pathname  // e.g. /aDogCalledSpot/cant-touch-this/tree/master/your/path/here.txt
    var split = url.split("/").filter(sub => sub !== "");
    if (split.length <= 4) {
        return "";      // Root directory
    } else {
        return split.slice(4).join("/");
    }
}

// Filter out all filepaths that are not relevant for the current page
// and format them to what is shown on gitlab
function filter_to_scope (filenames) {
    var prefix = get_curr_browser_dir();
    console.log("Current dir:", prefix);
    return filenames.filter(n => n.startsWith(prefix)).map(s => s.substring(prefix.length).split("/")
        .filter(s => s !== "")[0]);
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
    if (filename_texts.length != filename_boxes.length) {
        console.log("LENGTH MISMATCH:", filename_boxes.length, filename_texts.length);
    }
    // Arrange and insert elements
    for (i=0; i<filename_boxes.length; i++) {
        var divr = document.createElement("div");
        divr.style = "float:right;width:10%;";
        filename_boxes[i].append(divr);
        divr.append(icon.cloneNode());
    }
}

// MAIN
place_person_icons(filter_to_scope([".gitignore", "browser-plugin/manifest.json", "browser-plugin/icons/person.png"]));
