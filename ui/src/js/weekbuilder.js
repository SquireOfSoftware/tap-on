
function buildWeek(serviceObject) {
    var weekComponent = document.createElement("li");
    weekComponent.className = "week";

    var weekName = document.createElement("div");
    weekName.innerText = serviceObject.date;
    weekName.id = "week-" + serviceObject.id;

    var serviceComponents = document.createElement("ul");
    serviceComponents.className = "service";

    var serviceModel = createServiceModel(serviceObject);

    for(let i = 0; i < serviceModel.display.length; i++) {
        let service = buildServiceComponent(serviceModel.display[i]);
        serviceComponents.append(service);
    }

    weekComponent.append(weekName);
    weekComponent.append(serviceComponents);

    return weekComponent;
}

function buildServiceComponent(service) {
    var serviceComponent = document.createElement("li");
    serviceComponent.className = "service_component " + service.type;
    serviceComponent.appendChild(buildServiceName(service.name));
    serviceComponent.appendChild(buildNames(service.names));
    return serviceComponent;
}

function buildNames(names) {
    let peopleComponent = document.createElement("ol");
    peopleComponent.classList.add("people_component");
    if (names != undefined &&
        names != null &&
        names.length > 0) {
        names.forEach(name => {
            let nameComponent = createNameComponent(name);
            peopleComponent.appendChild(nameComponent);
        });
//    } else {
//        // assume it is null, create a blank spot
//        peopleComponent.appendChild(createNameComponent(""));
    }

    return peopleComponent
}

function createNameComponent(name) {
    let nameComponent = document.createElement("li");
    nameComponent.innerText = name;
    nameComponent.className = "name_component";
    return nameComponent;
}

function buildServiceName(serviceType) {
    let serviceName = document.createElement("div");
    serviceName.className = "service_name"
    serviceName.innerText = serviceType;
    return serviceName;
}

// creates the json object for services
function createServiceModel(rawServiceObject) {
    var serviceObject = rawServiceObject;
    var display = [];
    if (isNotValid(serviceObject.service) ||
        isDefault(serviceObject.service)) {
        display = genericServiceOrder;
        for(let i = 0; i < genericServiceOrder.length; i++) {
            display.push({
                name: genericServiceOrder[i]
            });
        }
    } else {
        for(let i = 0; i < serviceObject.service.length; i++) {
            display.push(serviceObject.service[i]);
        }
    }

    serviceObject.display = [];

    for(let i = 0; i < display.length; i++) {
        let serviceType = serviceComponents[display[i].name];
        let peoples = [];
        let names = [];
        for(let j = 0; display[i].people !== undefined &&
            j < display[i].people.length; j++) {
            let person = people[display[i].people[j]];
            peoples.push(person);
            names.push(person.givenName);
        }

        serviceType.people = people;
        serviceType.names = names;
        if (isValid(serviceType)) {
            serviceObject.display.push(serviceType);
        }
    }

    return serviceObject;
}

function isNotValid(object) {
    return object == undefined ||
    object == null;
}

function isValid(object) {
    return !isNotValid(object);
}

function isDefault(object) {
    return Array.isArray(object) &&
        object.length === 1 &&
        object[0] === "default";
}

getWeeksPromise((weeks) => {
    weeks.forEach(weekObject => {
        let week = buildWeek(weekObject);
        document.getElementById("roster_table").appendChild(week);
    });
});