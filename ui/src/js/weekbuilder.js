
function buildWeek(serviceObject) {
    var weekComponent = document.createElement("li");
    weekComponent.className = "week";

    var weekName = document.createElement("div");
    weekName.innerText = serviceObject.date;
    weekName.id = "week-" + serviceObject.id;

    var serviceComponents = document.createElement("div");
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
    var serviceComponent = document.createElement("div");
    serviceComponent.className = "service_component " + service.type;
    buildNames(serviceComponent, service.names);
    return serviceComponent;
}

function buildNames(parentComponent, names) {
    names.forEach(name => {
        let nameComponent = document.createElement("div");
        nameComponent.innerText = name;
        nameComponent.className = "name_component";

        parentComponent.appendChild(nameComponent);
    });
}

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

var weeks = [
    {
        id: 1,
        date: "10/20/19",
        service: [
            {
                name: "chairperson",
                people: [1]
            },
            {
                name: "welcoming",
                people: [2, 3]
            }
        ]
    },
    {
        id: 1,
        date: "10/27/19",
        service: [
            {
                name: "chairperson",
                people: [1]
            },
            {
                name: "welcoming",
                people: [2, 3]
            }
        ]
    }
];



weeks.forEach(weekObject => {
var week = buildWeek(weekObject);

document.getElementById("roster_table").appendChild(week);

});