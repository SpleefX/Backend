function parseMarkdown(text) {
    return marked.parse(
        text.replace(/&gt;+/g, ">").replace(/&lt;+/g, "<"),
        {sanitize: true}
    );
}

document.getElementById("sidebar_title").outerHTML = ""
const sidebar = document.getElementById("sidebar")

const wiki = document.createElement("p")
wiki.className = "sidebar_title"
wiki.innerHTML = "Wiki"
sidebar.appendChild(wiki)

const anchor = document.createElement("a")
anchor.className = "markdown"
anchor.innerHTML = parseMarkdown(document.getElementById("_sidebar").innerHTML)
sidebar.appendChild(anchor)
