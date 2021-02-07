import React, {Component} from 'react';

import logo from './logo.svg';
import './App.css';

import Settings from './components/Settings.js';
import QrScanner from './components/QrScanner.js';
import ScanLog from './components/ScanLog.js';
import Report from './components/Report.js';
import ServerStates from './components/ServerStates.js';

class App extends Component {
  constructor(props) {
    super(props);

    let currentCamera = window.localStorage.getItem('qrCamera');
    if (currentCamera === undefined || currentCamera === null) {
      currentCamera = this.props.currentCamera;
    }

    let delayRate = window.localStorage.getItem('qrDelayRate');
    if (delayRate === undefined || delayRate === null) {
      delayRate = this.props.delayRate;
    }
    delayRate = parseInt(delayRate);

    let serverSetting = window.localStorage.getItem('serverSetting');
    if (serverSetting === undefined || serverSetting === null) {
      serverSetting = this.props.serverSetting;
    }

    this.state = {
      logs: [],
      currentCamera: currentCamera,
      delayRate: delayRate,
      serverSetting: serverSetting,
      nextScan: null,
      serverState: ServerStates.UNCHECKED
    }

    this.addLog = this.addLog.bind(this);
    this.changeCamera = this.changeCamera.bind(this);
    this.changeDelayRate = this.changeDelayRate.bind(this);
    this.changeServerSetting = this.changeServerSetting.bind(this);
//    this.processScan = this.processScan.bind(this);
  }

  addLog = (log) => {
    let logs = this.state.logs;
    logs.push(log);
    if (logs.length > 10) {
      logs.shift();
    }

//    console.log(logs);

    this.setState({
      logs: logs
    });
  }

  addSuccessfulSignIn = (serverLog) => {

  }

  changeCamera = (changedCamera) => {
    this.setState({
      currentCamera: changedCamera
    });
    window.localStorage.setItem("qrCamera", changedCamera);
    this.updateCamera(changedCamera);
  }

  changeDelayRate = (changedDelayRate) => {
    this.setState({
      delayRate: changedDelayRate
    });
    window.localStorage.setItem("qrDelayRate", changedDelayRate);
    this.updateDelayRate(changedDelayRate);
  }

  changeServerSetting = (changedServerSetting) => {
    this.setState({
      serverSetting: changedServerSetting
    });
    window.localStorage.setItem("serverSetting", changedServerSetting);
    this.updateServerUrl(changedServerSetting);
  }

  serverIsUp = () => {
    this.setState({
      serverState: ServerStates.UP
    });
    this.setServerSettingUp();
  }

  serverIsDown = () => {
    this.setState({
      serverState: ServerStates.DOWN
    });
    this.setServerSettingDown();
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <div>Version {process.env.REACT_APP_VERSION}</div>
          <Settings initialCamera={this.state.currentCamera}
                    initialDelayRate={this.state.delayRate}
                    initialServerSetting={this.state.serverSetting}
                    changeCamera={this.changeCamera}
                    changeDelayRate={this.changeDelayRate}
                    changeServerSetting={this.changeServerSetting}
                    serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                    serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}/>
          <QrScanner addLog={this.addLog}
                      currentCamera={this.state.currentCamera}
                      delayRate={this.state.delayRate}
                      processScan={this.processScan}
                      updateDelayRate={newDelayRate => this.updateDelayRate = newDelayRate}
                      updateCamera={newCamera => this.updateCamera = newCamera}/>
          <ScanLog logs={this.state.logs}
                    initialServerSetting={this.state.serverSetting}
                    processScan={newScan => this.processScan = newScan}
                    updateServerUrl={newUrl => this.updateServerUrl = newUrl}
                    addLog={this.addLog}
                    addSuccessfulSignIn={this.addSuccessfulSignIn}
                    serverIsUp={this.serverIsUp}
                    serverIsDown={this.serverIsDown}
                    />
          <Report />
        </header>
      </div>
    );
  }
}

App.defaultProps = {
 currentCamera: 'environment',
 delayRate: 300,
 serverSetting: 'https://localhost:8000'
}

export default App;
