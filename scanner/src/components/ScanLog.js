import React, {Component} from 'react';

class ScanLog extends Component {
  constructor(props) {
    super(props);

    // for a single scan we want to first store it
    // the scan has a state of: processing, rejected, signedin
    // this will be a hashmap of the hash, with the timestamp and the result
    // the scan will happen quickly so this needs to be a fast look up
    this.state = {
      uniqueScans: 0,
      totalScans: new Map(),
      serverUrl: this.props.initialServerSetting
    }
    this.addScan = this.addScan.bind(this);
    this.updateServerUrl = this.updateServerUrl.bind(this);
  }

  componentDidMount() {
    this.props.processScan(this.addScan);
    this.props.updateServerUrl(this.updateServerUrl)
  }

  updateServerUrl = (serverUrl) => {
    this.setState({
      serverUrl: serverUrl
    })
  }

  addScan = (newScan) => {
    // scan should just be a number
    // we add the timestamp when we add it into the state
    if (this.scanIsLegit(newScan)) {
      let hash = Number.parseInt(newScan);
      let scan = {
        hash: hash,
        state: ScanStates.PROCESSING,
        scanTimestamp: new Date()
      }

      if (!this.state.totalScans.has(hash)) {
        let scans = this.state.totalScans;
        let scanCount = this.state.uniqueScans + 1;
        scans.set(hash, scan);
        this.setState({
          uniqueScans: scanCount,
          totalScans: scans
        });
//        console.log(scan);
        this.signIn(hash);
      } else if (this.state.totalScans.has(hash) &&
              this.state.totalScans.get(hash).state === ScanStates.FAILED) {
        // then we want to try again
        this.signIn(hash);
      }
    }
  }

  scanIsLegit = (newScan) => {
    return newScan !== undefined &&
      newScan !== null &&
      !Number.isNaN(Number.parseInt(newScan));
  }

  signIn = (hash) => {
    let signInComplete = (event) => {
      if (event.target.status === 200) {
        let checkinLog = JSON.parse(event.target.responseText);
        console.log(checkinLog);
        let person = checkinLog.person;
        this.state.totalScans.get(hash).state = ScanStates.SIGNED_IN;
        this.props.addLog(person.givenName + " has just been signed in.");
        this.props.addSuccessfulSignIn(checkinLog);
      } else {
        this.state.totalScans.get(hash).state = ScanStates.FAILED;

        let scan = this.state.totalScans.get(hash);
        console.log(scan);
      }
    }

    let signInFailed = (event) => {
      console.error("An error occurred while signing in the person.");
      this.state.totalScans.get(hash).state = ScanStates.FAILED;

      let scan = this.state.totalScans.get(hash);
      console.log(scan);
    }

    let signInRequest = new XMLHttpRequest();

    signInRequest.addEventListener("load", signInComplete);
    signInRequest.addEventListener("error", signInFailed);

    signInRequest.open("POST", this.state.serverUrl + "/people-service/checkin/signin", true);
    signInRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    signInRequest.setRequestHeader("Content-Type", "application/json");
    signInRequest.send(JSON.stringify({"hash": hash, "message": "signing in today again"}));

    console.log(signInRequest);
  }

  render() {
    let logs = [];
    for (let i = 0; i < this.props.logs.length; i++) {
      let log = this.props.logs[i];
      if (log === null) {
        log = "null";
      }
      logs.push(<div key={i}>{log}</div>);
    }

    return (
      <div>
        <p>Total unique scans: {this.state.uniqueScans}</p>
        {logs}
      </div>
    )
  }
}

const ScanStates = Object.freeze({
  PROCESSING: {
    name: "PROCESSING"
  },
  SIGNED_IN: {
    name: "SIGNED_IN"
  },
  FAILED: {
    name: "FAILED"
  }
});

export default ScanLog;