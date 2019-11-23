// Infer current project directory from browser URL
function get_curr_browser_dir() {
    var url = window.location.pathname;  // e.g. /aDogCalledSpot/cant-touch-this/tree/master/your/path/here.txt
    var split = url.split("/").filter(sub => sub !== "");
    if (split.length <= 4) {
        return "";      // Root directory
    } else {
        return split.slice(4).join("/");
    }
}

// Check if current URL is a file
function is_url_file() {
    var url = window.location.pathname;
    return (["blob", "raw"].includes(url.split("/").filter(s => s !== "")[2]));
}

// Filter out all filepaths that are not relevant for the current page
// and format them to what is shown on gitlab
function filter_to_scope (files) {
    var prefix = get_curr_browser_dir();
    var filtered_dict = {};
    for (key of Object.keys(files)) {
        if (key.startsWith(prefix)) {
            cut_key = key.substring(prefix.length).split("/").filter(s => s !== "")[0];
            if (cut_key in filtered_dict) {
                filtered_dict[cut_key] += files[key];
            } else {
                filtered_dict[cut_key] = files[key];
            }
        }
    }
    return filtered_dict;
}

// Changes list to listing of emails, e.g. "e@ma.il, sec@on.d and 2 others"
function get_editors(changes, max_explicit_len=2) {
    var editors = [];
    for (var i=0; i<max_explicit_len; i++) {
        if (i >= changes.length) {
          break;
        };
        editors.push(changes[i]["email"]);
        if ((changes.length <= max_explicit_len && i == changes.length-2)
        || (changes.length > max_explicit_len && i == max_explicit_len-1)) {
            editors.push(" and ");
        } else if (i < Math.min(changes.length-1, max_explicit_len)) {
            editors.push(", ");
        }
    }
    if (changes.length > max_explicit_len) {
        editors.push((changes.length-max_explicit_len) + " other")
    }
    return editors.join("");
}

// Place person icons next to all files on the current page with the file names specified
function place_person_icons(files) {
    files = filter_to_scope(files);
    // Create icon
    var icon = document.createElement("img");
    icon.src = browser.runtime.getURL("icons/person.png");
    icon.style = "text-align:right; height:1em; width:auto";
    // Find respective table cells
    var filename_texts = Array.from(document.querySelectorAll("#tree-slider > tbody > tr > td.tree-item-file-name > a"))
    .filter(el => Object.keys(files).includes(el.title));
    var filename_boxes = filename_texts.map(el => el.parentElement);
    // Arrange and insert elements
    for (i=0; i<filename_boxes.length; i++) {
        // Number of people working
        var num = document.createElement("span");
        num.className = "badge badge-pill count issue-counter";
        num.innerHTML = files[filename_texts[i].title];
        // Person icon
        var divr = document.createElement("div");
        divr.style = "float:right; display: inline-block";
        filename_boxes[i].append(divr);
        divr.append(icon.cloneNode(), num);
    }
}

// Place fading warning banner on top of files
function place_warning_banner(changes) {
    let div = document.createElement("div");
    let span = document.createElement("span");
    div.className = "banner";
    div.id = "fader";
    span.className = "banner_text";
    document.body.prepend(div);
    div.append(span);
    let key = get_curr_browser_dir();
    span.innerText = "This file is currently being edited by " + get_editors(changes) + ".\nProceed with care!";
}

// Place line highlights etc.
function place_line_edits(changes) {
    var unq_changes = [...new Set(changes.map(c => c["lines"]).flat())];
    for (change of unq_changes) {
        let line_con = document.getElementById("LC" + change);
        line_con.display = "block";
        line_con.style.backgroundColor = "#e3ceee";
    }
}

// Poll for new changes
function refresh_files() {
    // TODO: get changes and trigger actions
    // files = Object.fromEntries(entries);
    setTimeout(refresh_files, 5000);
}

// ========== MAIN ==========
// Load styles
var link = document.createElement("link");
link.rel = "stylesheet";
link.href = browser.runtime.getURL("style.css");
document.head.append(link);

// TODO: Get real files
let files = {
    ".gitignore": 1,
    "browser-plugin/manifest.json": 2,
    "browser-plugin/icons/person.png": 1,
    "backend/backend/settings.py": 3,
    "backend/main/admin.py": 1
}

// refresh_files();

if (!is_url_file()) {
    place_person_icons(files);
} else if (get_curr_browser_dir() in files){
    // TODO: Get real changes
    let changes = [
        {"email": "someone@web.de", "lines": [3,4,5]},
        {"email": "mea@cul.pa", "lines": [5,8,9]},
        {"email": "sand@l.en", "lines": [0]},
        {"email": "sel@e.na", "lines": [1,2]}
    ];
    // Highlight lines
    setTimeout(place_line_edits, 250, changes);
    // place_line_edits(changes);
    // Display warning
    place_warning_banner(changes);
}
