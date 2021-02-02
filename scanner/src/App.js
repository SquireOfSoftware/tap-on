import React, {Component} from 'react';

import logo from './logo.svg';
import './App.css';

import Settings from './components/Settings.js'
import QrScanner from './components/QrScanner.js'
import ScanLog from './components/ScanLog.js'
import Report from './components/Report.js'

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
      serverSetting: serverSetting
    }
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

  changeCamera = (changedCamera) => {
    this.setState({
      currentCamera: changedCamera
    });
    window.localStorage.setItem("qrCamera", changedCamera);
  }

  changeDelayRate = (changedDelayRate) => {
    this.setState({
      delayRate: changedDelayRate
    });
    window.localStorage.setItem("qrDelayRate", changedDelayRate);
  }

  changeServerSetting = (changedServerSetting) => {
    this.setState({
      serverSetting: changedServerSetting
    });
    window.localStorage.setItem("serverSetting", changedServerSetting);
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <Settings initialCamera={this.state.currentCamera}
                    initialDelayRate={this.state.delayRate}
                    initialServerSetting={this.state.serverSetting}
                    changeCamera={this.changeCamera}
                    changeDelayRate={this.changeDelayRate}
                    changeServerSetting={this.changeServerSetting}/>
          <QrScanner addLog={this.addLog}
                      currentCamera={this.state.currentCamera}
                      delayRate={this.state.delayRate}/>
          <ScanLog logs={this.state.logs}/>
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
