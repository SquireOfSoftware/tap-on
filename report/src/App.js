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

    let parsedStartDate = moment.utc(startTime).startOf('day');
    let todaysDate = moment.utc().startOf('day');
    if (!todaysDate.isSame(parsedStartDate)) {
      startTime = moment().hour(9).minute(0).second(0).format('YYYY-MM-DDTHH:mm:ss');
      window.localStorage.setItem("startTime", startTime);
    }

    let autoRefreshPeople = (window.localStorage.getItem('autoRefreshPeople') === 'true');
    if (autoRefreshPeople === undefined || autoRefreshPeople === null) {
      autoRefreshPeople = this.props.autoRefreshPeople;
    }

    this.state = {
      serverSetting: serverSetting,
      serverState: ServerStates.UNCHECKED,
      startTime: startTime,
      autoRefreshPeople: autoRefreshPeople
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

  updateAutoRefreshPeople = (newAutoRefresh) => {
    this.setState({
      autoRefreshPeople: newAutoRefresh
    });

    window.localStorage.setItem("autoRefreshPeople", newAutoRefresh);

    if (newAutoRefresh === true) {
      this.enableAutoRefreshPeople();
    } else {
      this.disableAutoRefreshPeople();
    }
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
                      initialStartTime={this.state.startTime}
                      initialAutoRefreshPeople={this.state.autoRefreshPeople}
                      serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                      serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}
                      disableAutoRefreshPeople={disableAutoRefreshPeople => this.disableAutoRefreshPeople = disableAutoRefreshPeople}
                      enableAutoRefreshPeople={enableAutoRefreshPeople => this.enableAutoRefreshPeople = enableAutoRefreshPeople}
                      updateServerState={this.updateServerState}/>
          <Settings initialServerUrl={this.state.serverSetting}
                    initialStartTime={this.state.startTime}
                    initialAutoRefreshPeople={this.state.autoRefreshPeople}
                    changeServerSetting={this.changeServerSetting}
                    changeStartTime={this.changeStartTime}
                    serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                    serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}
                    updateServerState={this.updateServerState}
                    updateAutoRefreshPeople={this.updateAutoRefreshPeople}/>
        </header>
      </div>
    );
  }
}

App.defaultProps = {
  serverSetting: 'https://localhost:8000',
  startTime: new moment().hour(9).minute(0).second(0).format('YYYY-MM-DDTHH:mm:ss'),
  autoRefreshPeople: true
}

export default App;
