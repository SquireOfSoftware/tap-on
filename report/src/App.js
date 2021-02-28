import React, {Component} from 'react';

import './App.css';

import Settings from './components/Settings.js';
import CheckList from './components/CheckList.js';
import ServerStates from './components/ServerStates.js';
import moment from 'moment'

class App extends Component {
  constructor(props) {
    super(props);
    let serverSetting = window.localStorage.getItem('serverSetting');
    if (serverSetting === undefined || serverSetting === null) {
      serverSetting = this.props.serverSetting;
    }

    let startTime = window.localStorage.getItem('startTime');
    if (startTime === undefined || startTime === null) {
      startTime = this.props.startTime;
    }

    this.state = {
      serverSetting: serverSetting,
      serverState: ServerStates.UNCHECKED,
      startTime: startTime
    }
    this.changeServerSetting = this.changeServerSetting.bind(this);
  }

  changeServerSetting = (changedServerSetting) => {
    this.setState({
      serverSetting: changedServerSetting
    });
    window.localStorage.setItem("serverSetting", changedServerSetting);
  }

  changeStartTime = (changedStartTime) => {
    this.setState({
      startTime: changedStartTime
    });
    window.localStorage.setItem("startTime", changedStartTime);
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

  updateServerState = (newState) => {
    this.setState({
      serverState: newState
    });
  }

  render() {
    let serverStatus;
    let serverMessage;
    if (this.state.serverState === ServerStates.DOWN) {
      serverStatus = "The checkin service is down.";
      serverMessage = <div className="server-status server-down">{serverStatus}</div>;
    }

    return (
      <div className="App">
        <header className="App-header">
          {serverMessage}
          <CheckList initialServerUrl={this.state.serverSetting}
                      serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                      serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}
                      updateServerState={this.updateServerState}
                      initialStartTime={this.state.startTime}/>
          <Settings initialServerUrl={this.state.serverSetting}
                    initialStartTime={this.state.startTime}
                    changeServerSetting={this.changeServerSetting}
                    changeStartTime={this.changeStartTime}
                    serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                    serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}
                    updateServerState={this.updateServerState}/>
        </header>
      </div>
    );
  }
}

App.defaultProps = {
  serverSetting: 'https://localhost:8000',
  startTime: new moment().format()
}

export default App;
