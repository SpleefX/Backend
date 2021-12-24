let pasteID = document.getElementById("pasteTitle").innerHTML.replace("/", ""); // thank you thymeleaf, very cool!
document.title = "Paste #" + pasteID + " - SpleefX Paste"

function getPastingURL() {
    return $(location).attr("href").toString().replace("/" + pasteID, "")
}

function beautify(text) {
    try {
        return JSON.stringify(JSON.parse(text), null, 2);
    } catch (e) {
        return text;
    }
}

const response = document.getElementById("cont").innerHTML
document.getElementById("pasteBox").style.fontFamily = "monospace"
document.getElementById("pasteBox").innerHTML = beautify(response.toString())
if (response.toString() === ("Invalid paste: " + pasteID)) {
    document.getElementById("raw").outerHTML = ""
    document.getElementById("copyText").outerHTML = ""
    document.getElementById("copyURL").outerHTML = ""
}

function viewRaw() {
    window.location.href = window.location.href.toString().replace("/" + pasteID, "/raw/" + pasteID)
}

function copyText() {
    const copyText = document.getElementById("pasteBox");

    /* Select the text field */
    copyText.select();
    copyText.setSelectionRange(0, 99999); /*For mobile devices*/

    /* Copy the text inside the text field */
    document.execCommand("copy")
}

function copyURL() {
    const text = $(location).attr("href")
    const dummy = document.createElement('input');
    document.body.appendChild(dummy);
    dummy.value = text;
    dummy.select();
    document.execCommand('copy');
    document.body.removeChild(dummy);
}

function createNew() {
    window.location.href = window.location.href.toString().replace("/" + pasteID, "")
}