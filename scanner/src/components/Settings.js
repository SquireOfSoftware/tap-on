import React, {Component} from 'react';
import './Settings.css'
import { slide as Menu } from 'react-burger-menu'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCog } from '@fortawesome/free-solid-svg-icons'

class Settings extends Component {
  constructor(props) {
    super(props);
    this.state = {
      facingMode: this.props.initialCamera,
      delayRate: this.props.initialDelayRate,
      serverSetting: this.props.initialServerSetting
    }
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
                value={this.state.serverSetting}
                onChange={this.changeServerSetting} />
            </div>
          </div>
        </Menu>
      </div>
    )
  }
}

export default Settings;