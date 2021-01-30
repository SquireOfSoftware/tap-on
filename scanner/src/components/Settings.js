import React, {Component} from 'react';
import './Settings.css'

class Settings extends Component {
  constructor(props) {
    super(props);
    this.state = {
      wrapperYPosition: 10 - document.body.clientHeight,
      lastYPosition: wrapperYPosition,
      isClicked: false,
      isMoved: false
    }
  }

  onDrag = (event) => {
    console.log("clicked");
    this.setState({
      isClicked: true
    })
  }

  onLetGo = (event) => {
    console.log("let go");
    this.setState({
      isClicked: false
    })
  }

  onMove = (event) => {
    if (this.state.isClicked) {
      console.log(event.clientY + " " + event.screenY + " " + document.body.clientHeight);
      this.setState({
        wrapperYPosition: document.body.clientHeight - event.screenY
      });
    }
  }

  render() {
    return (
      <div className="settingsWrapper"
          onMouseDown={this.onDrag}
          onMouseUp={this.onLetGo}
          onMouseMove={this.onMove}
          onMouseOut={this.onLetGo}
          style={{bottom: this.state.wrapperYPosition}}
          >
        <div>Settings</div>
        <div>
          <p>Camera</p>
          <p>Scan rate</p>
          <p>Server name</p>
        </div>
      </div>
    )
  }
}

export default Settings;