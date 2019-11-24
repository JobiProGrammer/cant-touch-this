function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
}

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
        editors.push(changes[i]["username"]);
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
    let ffiles = filter_to_scope(files);
    // Create icon
    var icon = document.createElement("img");
    icon.src = browser.runtime.getURL("icons/person.png");
    icon.style = "text-align:right; height:1em; width:auto";
    // Find respective table cells
    var filename_texts = Array.from(document.querySelectorAll("#tree-slider > tbody > tr > td.tree-item-file-name > a"))
    .filter(el => Object.keys(ffiles).includes(el.title));
    var filename_boxes = filename_texts.map(el => el.parentElement);
    // Arrange and insert elements
    for (i=0; i<filename_boxes.length; i++) {
        // Number of people working
        let num = document.createElement("span");
        num.className = "badge badge-pill count issue-counter";
        num.innerText = ffiles[filename_texts[i].title];
        // Person icon
        let divr = document.createElement("div");
        divr.id = uuidv4();
        person_icon_ids.push(divr.id);
        divr.style = "float:right; display: inline-block";
        // Hover Tooltip
        let hovertext = document.createElement("span");
        hovertext.className = "tooltiptext tooltip-top";
        hovertext.innerText = "Editor names, files, ...";
        divr.className = "tooltip-container";
        filename_boxes[i].append(divr);
        divr.append(icon.cloneNode(), num, hovertext);
    }
}

function remove_person_icons() {
    old_person_icon_ids.forEach(id => {
        var el = document.getElementById(id);
        el.parentNode.removeChild(el);
    });
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
    console.log("Banner placed.");
    banner_placed = true;
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

// Poll for new files
function refresh_files() {
    // Get list of file changes
    var url = new URL("http://52.236.180.203:8080/api/file/"),
        params = {
            "project": "git@gitlab.lrz.de:aDogCalledSpot/cant-touch-this.git",
            "dict": true
        }
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    fetch(url).then(res => {
        if (res.status >= 200 && res.status < 300) {
            return Promise.resolve(res);
        } else {
            return Promise.reject(new Error(res.statusText));
        }
    }).then(res => res.json()).then(data => {
        delete data.project;
        files = data;
        console.log("Is URL file?:", is_url_file(), "Browser dir:", get_curr_browser_dir(), "Files:", files);
        if (is_url_file() && get_curr_browser_dir() in files) {
            // Get changes and update visuals
            refresh_changes(get_curr_browser_dir());
        }
    })
    .catch(err => console.log("Fetch error: ", err));

    if (!is_url_file()) {
        update_frontend_dir(files);
    }

    // Schedule function again after timeout
    setTimeout(refresh_files, 500);
}

// Poll for changes within a file
function refresh_changes(filename) {
    // Get list of changes within file
    var url = new URL("http://52.236.180.203:8080/api/change/"),
        params = {
            "project": "git@gitlab.lrz.de:aDogCalledSpot/cant-touch-this.git",
            "path": filename
        }
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    fetch(url).then(res => {
        if (res.status >= 200 && res.status < 300) {
            return Promise.resolve(res);
        } else {
            return Promise.reject(new Error(res.statusText));
        }
    }).then(res => res.json()).then(changes => {
        changes = changes.changes;
        if (!banner_placed) {
            place_warning_banner(changes);
        }
        place_line_edits(changes);
    })
    .catch(err => console.log("Fetch error: ", err));
}

// Refresh the view of the directory explorer
function update_frontend_dir(files) {
    old_person_icon_ids = person_icon_ids;
    person_icon_ids = [];
    remove_person_icons();
    place_person_icons(files);
}

// ========== MAIN ==========
// Load styles
{
    let link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = browser.runtime.getURL("style.css");
    document.head.append(link);
}

var files = {};
var changes = [];
var person_icon_ids = [];
var old_person_icon_ids = [];
var banner_placed = false;

refresh_files();