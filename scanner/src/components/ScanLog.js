import React, {Component} from 'react';
import './ScanLog.css'
import ScanLogStates from './ScanLogStates.js'

class ScanLog extends Component {
  constructor(props) {
    super(props);

    // for a single scan we want to first store it
    // the scan has a state of: processing, rejected, signedin
    // this will be a hashmap of the hash, with the timestamp and the result
    // the scan will happen quickly so this needs to be a fast look up
    this.state = {
      successfulScanCount: 0,
      totalScans: new Map(),
      serverUrl: this.props.initialServerSetting,
      successLogs: []
    }
    this.addScan = this.addScan.bind(this);
    this.updateServerUrl = this.updateServerUrl.bind(this);
    this.signIn = this.signIn.bind(this);
    this.scanIsLegit = this.scanIsLegit.bind(this);
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
      let hash = newScan;
      let scan = {
        hash: hash,
        state: ScanStates.PROCESSING,
        scanTimestamp: performance.now()
      }

      if (!this.state.totalScans.has(hash)) {
        let scans = this.state.totalScans;
        scans.set(hash, scan);
        this.setState({
          totalScans: scans
        });
        this.signIn(hash);
      } else {
        let existingScan = this.state.totalScans.get(hash);
        let timeSinceLastSignIn = performance.now() - existingScan.scanTimestamp;
        if (existingScan.state === ScanStates.FAILED) {
          // then we want to try again
          this.signIn(hash);
        } else if (timeSinceLastSignIn > 300000) {
          // if 5 mins (300,000ms) have passed
          // sign the person in again
          // as most likely its a "new" session
          this.signIn(hash);
        } else {
          let alreadySignedInMessage = "'" + hash + "' has signed in already";
          this.props.addLog({message: alreadySignedInMessage, state: ScanLogStates.WARNING});
        }
      }
    } else {
      let invalidScanMessage = "Scan: '" + newScan + "' is not legit";
      console.error(invalidScanMessage);
      this.props.addLog({message: invalidScanMessage, state: ScanLogStates.ERROR});
    }
  }

  scanIsLegit = (newScan) => {
    return newScan !== undefined &&
      newScan !== null;
  }

  signIn = (hash) => {
    let signInComplete = (event) => {
      if (event.target.status === 200) {
        let checkinLog = JSON.parse(event.target.responseText);
        let person = checkinLog.person;
        this.setState(prevState => {
          const newScans = new Map(prevState.totalScans);
          const scan = {
            ...newScans.get(hash),
            state: ScanStates.SIGNED_IN
          }
          return {
            totalScans: newScans.set(hash, scan)
          }
        });
        let successfulScanCount = this.state.successfulScanCount + 1;
        let successLog = person.givenName + " " + person.familyName + " has just been signed in.";
        let successLogs = this.state.successLogs;
        successLogs.unshift(successLog);
        this.setState({
          successLogs: successLogs,
          successfulScanCount: successfulScanCount
        });
        this.props.addLog({message: successLog, state: ScanLogStates.SUCCESS});
        this.props.addSuccessfulSignIn(checkinLog);
      } else {
        // every other scan state
        this.setState(prevState => {
          const newScans = new Map(prevState.totalScans);
          const scan = {
            ...newScans.get(hash),
            state: ScanStates.FAILED,
            scanTimestamp: performance.now()
          }
          return {
            totalScans: newScans.set(hash, scan)
          }
        });
        let response = JSON.parse(event.target.responseText);
        this.props.addLog({message: "Scan failed: " + response.message, state: ScanLogStates.ERROR});
      }
      this.props.serverIsUp();
    }

    let signInFailed = (event) => {
      console.error("An error occurred while signing in the person.");
      this.setState(prevState => {
        const newScans = new Map(prevState.totalScans);
        const scan = {
          ...newScans.get(hash),
          state: ScanStates.FAILED,
          scanTimestamp: performance.now()
        }
        return {
          totalScans: newScans.set(hash, scan)
        }
      });
      this.props.serverIsDown();
    }

    let signInRequest = new XMLHttpRequest();

    signInRequest.addEventListener("load", signInComplete);
    signInRequest.addEventListener("error", signInFailed);

    signInRequest.open("POST", this.state.serverUrl + "/people-service/checkin/signin", true);
    signInRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    signInRequest.setRequestHeader("Content-Type", "application/json");
    signInRequest.send(JSON.stringify({"hash": hash, "message": "qr code sign in"}));

    console.log(signInRequest);
  }

  render() {
    let logs = [];
    for (let i = 0; i < this.props.logs.length; i++) {
      let log = this.props.logs[i];
      let logCSS = "logLine";
      if (log === null || log === undefined) {
        log = {message: "null"};
      }
      if (this.state.successMessagesOnly &&
          log.state !== undefined &&
          log.state === ScanLogStates.SUCCESS) {
        logCSS += " " + log.state.cssName;
        logs.unshift(<div className={logCSS} key={i}>{log.message}</div>);
      } else {
        if (log.state !== undefined) {
          logCSS += " " + log.state.cssName;
        }
        logs.unshift(<div className={logCSS} key={i}>{log.message}</div>);
      }
    }

    let personWord;
    if (this.state.successfulScanCount === 1) {
      personWord = "person";
    } else {
      personWord = "people";
    }

    return (
      <div>
        <div>
          <div className="scanCount">
            {this.state.successfulScanCount}
          </div>
          <div className="scanCountText">
            {personWord} signed in.
          </div>
        </div>
        <div className="rawLogs">
          {logs}
        </div>
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