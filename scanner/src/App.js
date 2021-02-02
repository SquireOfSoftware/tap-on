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

    this.state = {
      logs: [],
      currentCamera: this.props.currentCamera,
      delayRate: this.props.delayRate
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
    })
  }

  changeDelayRate = (changedDelayRate) => {
    this.setState({
      delayRate: changedDelayRate
    })
  }

  changeServerSetting = (changedServerSetting) => {
    this.setState({
      serverSetting: changedServerSetting
    })
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <Settings initialCamera={this.props.currentCamera}
                    initialDelayRate={this.props.delayRate}
                    initialServerSetting={this.props.serverSetting}
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
