const json = JSON.parse(document.getElementById("payload").innerHTML)
const id = document.getElementById("gameID").innerHTML
const sheet = document.styleSheets[0];
const rules = sheet.cssRules || sheet.rules;

rules[1].style.backgroundImage = "url('" + json.bg + "')";

function getVictoryState(token) {
    switch (token) {
        case "$WIN":
            return {html: json.victory || "VICTORY", color: "rgb(0, 200, 0)"}
        case "$LOSS":
            return {html: json.eliminated || "ELIMINATED", color: "rgb(200, 10, 10)"}
        case "$CARRY":
            return {html: json.carried || "CARRIED", color: "rgb(255,141,2)"}
        default:
            return {html: token, color: "#808080"}
    }
}

document.getElementById("modename").innerHTML = json.modeName;
document.getElementById("game-summary").innerHTML = json.ip

createHeader(json.header);

function addValue(name, value, color = "") {
    const summary = document.getElementById("game-summary")
    if (summary.innerText)
        summary.innerHTML += "<br/>"
    const span = document.createElement("span")
    span.className = "value"
    span.innerHTML = value
    if (color)
        span.style.color = color
    summary.innerHTML += name + ": " + span.outerHTML + "<br/>"
}

function createHeader(header) {
    const table = document.getElementById("table");
    const row = document.createElement("tr");

    for (let i = 0; i < header.length; i++) {
        const tableHeader = document.createElement("th");
        tableHeader.innerHTML = header[i];
        row.appendChild(tableHeader);
    }

    table.appendChild(row);
}

function addRow(rowData) {
    const table = document.getElementById("table");
    const row = document.createElement("tr");

    for (let i = 0; i < rowData.length; i++) {
        const tableData = document.createElement("td");
        const victoryState = getVictoryState(rowData[i].toString())
        tableData.innerHTML = victoryState.html
        tableData.style.color = victoryState.color
        row.appendChild(tableData);
    }

    table.appendChild(row);
}

for (let index in json.rows) {
    addRow(json.rows[index]);
}

document.title = "Game #" + id + " @ " + json.svr
document.getElementById("og_url").innerHTML = "https://spleefx.net/stats?game=" + id
document.getElementById("og_title").innerHTML = "Game stats | " + json.svr + " | SpleefX Stats"
document.getElementById("og_desc").innerHTML = "View game statistics of game #" + id + " at " + json.ip + "."