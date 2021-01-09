// poll the sign ins for today
let continuePollingTimer;

let server = "http://localhost:8080";
let getTodaysSigninsUrl = server + "/checkin/signins/today";
let getSigninsFromUrl = server + "/checkin/signins/from/";

let lastPolledTime;

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
        () => getSigninsFrom(lastDate, (evt) => {
            let events = JSON.parse(evt.target.responseText);
            let lastEventDate = new Date(events[events.length - 1].timestamp);

            if (lastPolledTime > lastEventDate) {
                lastEventDate = lastPolledTime;
            }

            console.log("hello, continued from: " + lastEventDate);
            pollSignins(lastEventDate);
        }), 5000);
}

getTodaysSignins((evt) => {
    console.log(evt);
    lastPolledTime = new Date();
    pollSignins(lastPolledTime);
});

// TODO: when you have time, try and get it to work with server side events
