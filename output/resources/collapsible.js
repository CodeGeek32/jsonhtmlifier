function attachHandlers(node) {

  var array = node.getElementsByClassName("collapsibleButton");
  var i;

  for (i = 0; i < array.length; i++) {

    if (array[i].tagName.toLowerCase() == "button") {
      array[i].addEventListener("click", function () {
        this.classList.toggle("active");

        // var content = this.nextElementSibling;
        var content = getElementToCollapse(this);
        if (content == null) return;

        if (content.style.display == "" || content.style.display == "block") {
          content.style.display = "none";

          this.textContent = '+';
        }
        else {
          content.style.display = "block";

          this.textContent = '-';
        }
      }
      );
    }

    if (array[i].tagName.toLowerCase() == "span") {

      array[i].addEventListener("click", function () {

        this.classList.toggle("active");

        var content = getElementToCollapse(this);
        if (content == null) return;

        if (content.style.display == "" || content.style.display == "block") {
          content.style.display = "none";

          var button = getButtonThatCollapses(this);
          button.textContent = '+';
        }
        else {
          content.style.display = "block";

          var button = getButtonThatCollapses(this);
          button.textContent = '-';
        }
      });
    }

  }
}

function getElementToCollapse(currObj) {
  var parentofSelected = currObj.parentNode; // gives the parent DIV

  var children = parentofSelected.childNodes;
  for (var i = 0; i < children.length; i++) {
    if (children[i].classList.contains("toCollapse")) {
      return children[i];
    }

  }
}

function getButtonThatCollapses(currObj) {
  var parentofSelected = currObj.parentNode; // gives the parent DIV

  var children = parentofSelected.childNodes;
  for (var i = 0; i < children.length; i++) {
    if (children[i].classList.contains("collapsibleButton") && children[i].tagName.toLowerCase() == "button") {
      return children[i];
    }

  }
}
