import React, {Component} from 'react';

import './App.css';

import Settings from './components/Settings.js';
import CheckList from './components/CheckList.js';
import ServerStates from './components/ServerStates.js';

class App extends Component {
  constructor(props) {
    super(props);
    let serverSetting = window.localStorage.getItem('serverSetting');
    if (serverSetting === undefined || serverSetting === null) {
      serverSetting = this.props.serverSetting;
    }

    this.state = {
      serverSetting: serverSetting,
      serverState: ServerStates.UNCHECKED
    }
    this.changeServerSetting = this.changeServerSetting.bind(this);
  }

  serverIsUp = () => {
    this.setState({
      serverState: ServerStates.UP
    });
    this.setServerSettingUp();
  }

  changeServerSetting = (changedServerSetting) => {
    this.setState({
      serverSetting: changedServerSetting
    });
    window.localStorage.setItem("serverSetting", changedServerSetting);
    this.updateServerUrl(changedServerSetting);
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
          <CheckList initialServerSetting={this.state.serverSetting}/>
          <Settings initialServerSetting={this.state.serverSetting}
                    changeServerSetting={this.changeServerSetting}
                    serverIsDown={serverIsDown => this.setServerSettingDown = serverIsDown}
                    serverIsUp={serverIsUp => this.setServerSettingUp = serverIsUp}
                    updateServerState={this.updateServerState}/>
        </header>
      </div>
    );
  }
}

App.defaultProps = {
 serverSetting: 'https://localhost:8000'
}

export default App;
