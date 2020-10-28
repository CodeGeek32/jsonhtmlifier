function prettify() {
	var index = event.target.id;
	var button = document.getElementById(index);
	var element = document.getElementById('json ' + index);

	var jsonTree = jsonToHtml(JSON.parse(element.textContent), document);
	jsonTree.className += "root";
	attachHandlers(jsonTree);

	var parentElement = button.parentElement;
	parentElement.appendChild(jsonTree);

	parentElement.removeChild(button);
	parentElement.removeChild(document.getElementById('json ' + index));
}
