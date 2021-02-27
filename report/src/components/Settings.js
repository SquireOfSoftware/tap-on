import React, {Component} from 'react';
import './Settings.css'
import { slide as Menu } from 'react-burger-menu'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCog, faSyncAlt } from '@fortawesome/free-solid-svg-icons'
import ServerStates from './ServerStates.js';

class Settings extends Component {
  constructor(props) {
    super(props);
    this.state = {
      serverGETState: ServerStates.UNCHECKED,
      serverUrl: this.props.initialServerSetting
    }
    this.verifyServerGETRequest = this.verifyServerGETRequest.bind(this);
    this.verifyServerGETRequest();
    this.changeServerSetting = this.changeServerSetting.bind(this);
  }

  componentDidMount() {
    this.props.serverIsUp(this.setServerUp);
    this.props.serverIsDown(this.setServerDown)
  }

  changeServerSetting = (event) => {
    let serverSetting = event.target.value;
    this.setState({
      serverSetting: serverSetting
    });
    this.props.changeServerSetting(serverSetting);
  }

  setServerUp = () => {
    this.setState({
      serverGETState: ServerStates.UP
    });
  }

  setServerDown = () => {
    this.setState({
      serverGETState: ServerStates.DOWN
    });
  }

  verifyServerGETRequest = () => {
    this.setState({
      serverGETState: ServerStates.CHECKING
    });

    let successGET = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverGETState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverGETState);
      } else {
        this.setState({
          serverGETState: ServerStates.DOWN
        });
        this.props.updateServerState(this.state.serverGETState);
      }
    }

    let failedGET = (event) => {
      console.error("An error occurred while checking the server health.");
      this.setState({
        serverGETState: ServerStates.DOWN
      });
      this.props.updateServerState(this.state.serverGETState);
    }

    let getRequest = new XMLHttpRequest();

    getRequest.addEventListener("load", successGET);
    getRequest.addEventListener("error", failedGET);
    console.log(this.state.serverUrl + "/people-service/actuator/health");
    getRequest.open("GET", this.state.serverUrl + "/people-service/actuator/health", true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();
    console.log("trying to ping the service");

    console.log(getRequest);
  }

  render() {
    return (
      <div>
        <Menu isOpen={false}>
          <div className="settings_header">
            <FontAwesomeIcon icon={faCog}/>
            Settings
          </div>
          <div className="settings_body">
            <div className="server_settings">
              <label>Server name</label>
              <input
                id="server_setting"
                type="url"
                value={this.props.initialServerSetting}
                onChange={this.changeServerSetting} />
            </div>
            <div onClick={() => this.verifyServerGETRequest()}>
              Server is: {this.state.serverGETState.name}
              <FontAwesomeIcon icon={faSyncAlt}/>
            </div>
            <div>
              <a href="/qr-scanner" target="_self">QR Scanner</a>
            </div>
            <div>
              Version {process.env.REACT_APP_VERSION}
            </div>
          </div>
        </Menu>
      </div>
    )
  }
}

export default Settings;