const DATASOURCE = {
    LIVE: 1,
    TEST: 2
}

var currentDatasource = DATASOURCE.TEST;

function validateConnection() {
    let request = new XMLHttpRequest();
    request.onreadystatechange = () => {
        if(this.readState === 4 &&
            this.status === 200) {
            currentDatasource = DATASOURCE.LIVE;
        }
    }
    request.open("GET", "localhost/monitoring", true)
    request.send();
}

function getWeeksPromise(callback) {
    switch(currentDatasource) {
        case 1:
            return callback();
        default:
            return callback(dummyWeeks);
    }
}
