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
      logs: []
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
      logs
    });
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <Settings />
          <QrScanner onScan={this.addLog}/>
          <ScanLog onLog={this.state.logs}/>
          <Report />
        </header>
      </div>
    );
  }
}

export default App;
