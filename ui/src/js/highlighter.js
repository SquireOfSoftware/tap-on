const HIGHLIGHTED_CLASS_NAME = "highlighted";

function highlightMe() {
    let name = document.getElementById("highlighted-name").value;

    let components = document.getElementsByClassName("name_component");

    for(let i = 0; i < components.length; i++) {
        let component = components[i];
        if (name === "") {
            component.classList.remove(HIGHLIGHTED_CLASS_NAME);
        } else if(component.innerText.toLowerCase().indexOf(name.toLowerCase()) > -1) {
            component.classList.add(HIGHLIGHTED_CLASS_NAME);
        } else if (component.className.indexOf(HIGHLIGHTED_CLASS_NAME) > -1) {
            component.classList.remove(HIGHLIGHTED_CLASS_NAME);
        }
    }
}