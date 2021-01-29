// hash -> timestamp
let signedInPeople = new Map();
let error = {
    message: ""
};
let FIVE_MIN=5*60*1000;
let server = "http://localhost:8080/people-service"
let signInUrl = server + "/checkin/signin"
let getLogsForPersonUrl = server + "/people/id/";
let getCsrfTokenUrl = server + "/csrf";
let CSRF_TOKEN;

function isInt(value) {
  return !isNaN(value) &&
         parseInt(Number(value)) == value &&
         !isNaN(parseInt(value, 10));
}

function getCsrfToken(callback) {
    var oReq = new XMLHttpRequest();

    oReq.addEventListener("load", transferComplete);

    function transferComplete(evt) {
      console.log("The transfer is complete.");
      console.log(evt);
      if (evt.target.status === 200) {
        callback(evt);
      }
    }

    oReq.open("GET", getCsrfTokenUrl, true);
    oReq.send();
}

function getLogsForPerson(id, callback) {
    var oReq = new XMLHttpRequest();

    oReq.addEventListener("progress", updateProgress);
    oReq.addEventListener("load", transferComplete);

    // progress on transfers from the server to the client (downloads)
    function updateProgress (oEvent) {
      if (oEvent.lengthComputable) {
        var percentComplete = oEvent.loaded / oEvent.total * 100;
      } else {
        // Unable to compute progress information since the total size is unknown
      }
    }

    function transferComplete(evt) {
      console.log("The transfer is complete.");
      console.log(evt.target);
      if (evt.target.status === 200) {
        callback(evt);
      }
    }

    oReq.open("GET", getLogsForPersonUrl + id, true);
    oReq.send();
}

function signInPerson(hash, callback) {
    var oReq = new XMLHttpRequest();

    oReq.addEventListener("progress", updateProgress);
    oReq.addEventListener("load", transferComplete);
    oReq.addEventListener("error", transferFailed);
    oReq.addEventListener("abort", transferCanceled);

    // progress on transfers from the server to the client (downloads)
    function updateProgress (oEvent) {
      if (oEvent.lengthComputable) {
        var percentComplete = oEvent.loaded / oEvent.total * 100;
      } else {
        // Unable to compute progress information since the total size is unknown
      }
    }

    function transferComplete(evt) {
      console.log("The transfer is complete.");
      console.log(evt);
      if (evt.target.status === 200) {
        callback(evt);
      }
    }

    function transferFailed(evt) {
      console.log("An error occurred while transferring the file.");
    }

    function transferCanceled(evt) {
      console.log("The transfer has been canceled by the user.");
    }

    oReq.open("POST", signInUrl, true);
    oReq.setRequestHeader("Access-Control-Allow-Headers", "*");
    oReq.setRequestHeader("Content-Type", "application/json");
//    oReq.setRequestHeader(CSRF_TOKEN.headerName, CSRF_TOKEN.token);
    oReq.send(JSON.stringify({"hash": hash, "message": "signing in today again"}));
}

function clearErrorMessage() {
    document.getElementById("message").innerHTML = "";
}

let errorMessageTimeout;

function displayError(message) {
    document.getElementById("message").innerHTML = message;
    console.error(message);
    if (errorMessageTimeout != undefined) {
        window.clearTimeout(errorMessageTimeout);
    }
    errorMessageTimeout = window.setTimeout(() => clearErrorMessage(), 1000);
}

function displaySuccess(message) {
    document.getElementById("message").innerHTML = message;
    console.log(message);
}

// assume that the scannedData is a hashcode
function processScan(scannedData) {
    if (isInt(scannedData)) {
        if (signedInPeople.has(scannedData) &&
            (new Date() - signedInPeople.get(scannedData).timestamp < FIVE_MIN)) {
            displayError("This person has signed in already, please try again in 5 minutes.");
        } else if (!signedInPeople.has(scannedData)) {
            // sign in the person
            signInPerson(scannedData, (evt) => {
                let checkinLog = JSON.parse(evt.target.responseText);
                let person = checkinLog.person;
                signedInPeople.set(scannedData, {"timestamp": new Date(), "person": person});
                console.log(signedInPeople);
                displaySuccess(person.givenName + " " + person.familyName + " has signed in!");
            });
            // on success add person to the map
        }
    } else {
        console.error("The scanned data is not an integer value: " + scannedData);
    }
}

function onScanSuccess(qrMessage) {
	// handle the scanned code as you like
	console.log(`QR matched = ${qrMessage}`);
	// we want to verify that we have the right
	processScan(qrMessage);
	// parse the code, check the hash and make sure the person has not signed in again for at least 5 minutes

}

function onScanFailure(error) {
	// handle scan failure, usually better to ignore and keep scanning
	//console.warn(`QR error = ${error}`);
}

//getCsrfToken((event) => {
//
//    CSRF_TOKEN = JSON.parse(event.target.responseText);
//    console.log(CSRF_TOKEN);
//    document.cookie = "XSRF-TOKEN=" + CSRF_TOKEN.token + ";";
//});

let html5QrcodeScanner = new Html5QrcodeScanner(
	"reader", { fps: 10, qrbox: 600 }, /* verbose= */ false);
html5QrcodeScanner.render(onScanSuccess, onScanFailure);