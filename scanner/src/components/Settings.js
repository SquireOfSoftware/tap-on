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
      facingMode: this.props.initialCamera,
      delayRate: this.props.initialDelayRate,
      serverUrl: this.props.initialServerSetting,
      serverGETState: ServerStates.UNCHECKED
    }
    this.verifyServerGETRequest = this.verifyServerGETRequest.bind(this);

    this.verifyServerGETRequest();
  }

  componentDidMount() {
    this.props.serverIsUp(this.setServerUp);
    this.props.serverIsDown(this.setServerDown)
  }

  changeDelayRate = (event) => {
    let delayRate = event.target.value;
    this.setState({
      delayRate: delayRate
    });
    this.props.changeDelayRate(delayRate);
  }

  changeCamera = (event) => {
    let camera = event.target.value;
    this.setState({
      facingMode: camera
    });
    this.props.changeCamera(camera);
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

    getRequest.open("GET", this.state.serverUrl + "/people-service/actuator/health", true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();

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
            <div className="camera_settings">
              <label>Camera</label>
              <select
                value={this.state.facingMode}
                onChange={this.changeCamera}
                id="camera_option">
                <option value="user">User Camera</option>
                <option value="environment">Environment Camera</option>
              </select>
            </div>
            <div className="scan_rate_settings">
              <label>Scan rate</label>
              <input
                id="delay_rate"
                type="number"
                value={this.state.delayRate}
                onChange={this.changeDelayRate} />
            </div>
            <div className="server_settings">
              <label>Server name</label>
              <input
                id="server_setting"
                type="url"
                value={this.state.serverUrl}
                onChange={this.changeServerSetting} />
            </div>
            <div onClick={() => this.verifyServerGETRequest()}>
              Server is: {this.state.serverGETState.name}
              <FontAwesomeIcon icon={faSyncAlt}/>
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