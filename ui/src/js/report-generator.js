// poll the sign ins for today
let continuePollingTimer;

let server = "http://localhost:8080";
let getTodaysSigninsUrl = server + "/checkin/signins/today";
let getSigninsFromUrl = server + "/checkin/signins/from/";

let lastPolledTime;

// this is a list of ascending checkin logs
let signedInPeople;

let signedInIds = [];

function createDiv() {
    return document.createElement("div");
}

function createTable() {
    return document.createElement("table");
}

function createRow() {
    return document.createElement("tr");
}

function createCell() {
    return document.createElement("td")
}

function buildRow(data) {
    let rowDiv = createRow();
    rowDiv.className = "row";

    let nameDiv = createCell();
    nameDiv.innerHTML = data.person.givenName + " " + data.person.familyName;
    rowDiv.appendChild(nameDiv);
    nameDiv.className = "cell";

    let timestampDiv = createCell();
    timestampDiv.innerHTML = data.timestamp;
    timestampDiv.className = "cell";
    rowDiv.appendChild(timestampDiv);

    let mobileNumbersDiv = createCell();
    if (data.person.phoneNumbers != undefined) {
        data.person.phoneNumbers.forEach((phoneNumber, index) => {
            mobileNumbersDiv.innerHTML += phoneNumber + "\n";
        });
    }
    mobileNumbersDiv.className = "cell";
    rowDiv.appendChild(mobileNumbersDiv);

    let emailDiv = createCell();
    if (data.person.emails != undefined) {
        data.person.emails.forEach((email, index) => {
            emailDiv.innerHTML += email + "\n";
        });
    }
    emailDiv.className = "cell";
    rowDiv.appendChild(emailDiv);

    return rowDiv;
}

function buildHeader() {
    let header = {
        person: {
            givenName: "",
            familyName: "Name",
            phoneNumbers: ["Phone numbers"],
            emails: ["Emails"]
        },
        timestamp: "Timestamp"
    };
    let headerDiv = buildRow(header);
    headerDiv.className += " header";
    return headerDiv;
}

function buildTable(data) {
    let tableDiv = document.getElementById("table");
    tableDiv.innerHTML = "";
    tableDiv.appendChild(buildHeader());
    // create row
    data.forEach((item, index) => {
        tableDiv.appendChild(buildRow(item));
    });
    // then create individual elements
}

// we are merging new people into this list
// at the end of it we want to rebuild the table
function mergeList(data) {
    // data is a list of checkins
    let rebuildTable = false;
    data.forEach((log, index) => {
        if (log.person.id != undefined && signedInIds.indexOf(log.person.id) === -1) {
            rebuildTable = true;
            signedInIds.push(log.person.id);
            signedInPeople.push(log);
        }
    });
    // find the person
    if (rebuildTable) {
        buildTable(signedInPeople);
    }
}

function getTodaysSignins(callback) {
    var oReq = new XMLHttpRequest();

    oReq.addEventListener("load", transferComplete);

    function transferComplete(evt) {
      console.log("The transfer is complete.");
      console.log(evt.target);
      if (evt.target.status === 200) {
        callback(evt);
      }
    }

    oReq.open("GET", getTodaysSigninsUrl, true);
    oReq.send();
}

// once loaded, poll for sign ins after last signin
function getSigninsFrom(fromDate, callback) {
    var oReq = new XMLHttpRequest();

    oReq.addEventListener("load", transferComplete);

    function transferComplete(evt) {
      console.log("The transfer is complete.");
      console.log(evt.target);
      if (evt.target.status === 200) {
        callback(evt);
      }
    }

    oReq.open("GET", getSigninsFromUrl + fromDate, true);
    oReq.send();
}

function pollSignins(lastDate) {
    if (continuePollingTimer != undefined) {
        window.clearTimeout(continuePollingTimer);
    }
    continuePollingTimer = window.setTimeout(
        // you need to find a date time library to give you ISO time properly
        () => getSigninsFrom(lastDate.toISOString(), (evt) => {
            let events = JSON.parse(evt.target.responseText);
            let lastEventDate = new Date(events[events.length - 1].timestamp);

            if (lastPolledTime > lastEventDate) {
                lastEventDate = lastPolledTime;
            }
            mergeList(events);
            console.log("hello, continued from: " + lastEventDate);
            pollSignins(lastEventDate);
        }), 5000);
}

getTodaysSignins((evt) => {
    console.log(evt);
    signedInPeople = JSON.parse(evt.target.responseText);
    buildTable(signedInPeople);
    signedInPeople.forEach((log, index) => {
        let person = log.person;
        signedInIds.push(person.id);
    });
    lastPolledTime = new Date();
    pollSignins(lastPolledTime);
});

// TODO: when you have time, try and get it to work with server side events
