import React, {Component} from 'react';
import './ScanLog.css'
import ScanLogStates from './ScanLogStates.js'
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';

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
      successfulScans: []
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
        let successfulScans = this.state.successfulScans;
        person.signInTime = checkinLog.timestamp;
        successfulScans.push(checkinLog);
        successfulScans.sort((checkinLog1, checkinLog2) => {
          if (checkinLog1.person.familyName > checkinLog2.person.familyName) {
            return 1;
          } else if (checkinLog1.person.familyName < checkinLog2.person.familyName) {
            return -1;
          } else {
            return 0;
          }
        });

        this.setState({
          successfulScans: successfulScans,
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
      let key = "rawLog" + i;
      if (log.state !== undefined) {
        logCSS += " " + log.state.cssName;
      }
      logs.unshift(<div className={logCSS} key={key}>{log.message}</div>);
    }

    let reportLogs = [];
    for (let i = 0; i < this.state.successfulScans.length; i++) {
      let checkinLog = this.state.successfulScans[i];
      let key = "person" + i;
      reportLogs.push(<tr>
                        <td>{checkinLog.person.givenName}</td>
                        <td>{checkinLog.person.familyName}</td>
                        <td>{checkinLog.signInTime}</td>
                      </tr>);
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
        <Tabs>
          <TabList>
            <Tab>
              Report
            </Tab>
            <Tab>
              Logs
            </Tab>
          </TabList>
          <TabPanel>
            <table className="reportLogs">
              <thead>
                <tr>
                  <th>Given Name</th>
                  <th>Family Name</th>
                  <th>Sign in time</th>
                </tr>
              </thead>
              <tbody>
                {reportLogs}
              </tbody>
            </table>
          </TabPanel>
          <TabPanel>
            <div className="rawLogs">
              {logs}
            </div>
          </TabPanel>
        </Tabs>
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