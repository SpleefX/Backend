function createPaste() {
    const text = document.getElementById("pasteBox").value;
    if (!text) {
        alert("You must specify text!");
        return;
    }
    let request = new XMLHttpRequest();
    request.open("POST", "/paste", true);
    request.setRequestHeader("Content-Type", "application/json");
    request.send(
        JSON.stringify({
            paste: text,
        })
    );
    request.onreadystatechange = function () {
        // listen for state changes
        if (request.readyState === 4 && request.status === 200) {
            // when completed we can move away
            window.location.href = "/paste/" + JSON.parse(this.response).id
        }
    };
}