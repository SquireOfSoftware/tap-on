import React, {Component} from 'react';
import './Settings.css'
import { slide as Menu } from 'react-burger-menu'
import Select from 'react-select'

class Settings extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sliderVisible: true
    }
    this.handleFacingModeChange = this.props.handleFacingModeChange.bind(this);
  }

  render() {
    return (
      <div>
        <Menu isOpen={false}>
          <div>Settings</div>
          <div>
            <span>Camera</span>
            <select
              value={this.state.facingMode}
              onChange={this.handleFacingModeChange}
              >
              <option value="user">User Camera</option>
              <option value="environment">Environment Camera</option>
            </select>
            <p>Scan rate</p>
            <p>Server name</p>
          </div>
        </Menu>
      </div>
    )
  }
}

const Cameras = {
  user: 'user',
  environment: 'environment'
}

export default Settings;