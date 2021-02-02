import React, {Component} from 'react';
import './QrScanner.css'

import QrReader from 'react-qr-reader'
import StartButton from './StartButton.js'

class QrScanner extends Component {
  constructor(props) {
      super(props);
      this.state = {
        lastReadResult: "Test",
        opened: false,
        scanner: null
      };
  }

  handleError = (event) => {
//    console.log(event);
  }

  handleScan = (event) => {
    console.log(event);
    this.setState({
      lastReadResult: event
    });
    if (event !== null && event !== undefined) {
      this.props.addLog(event);
    }
  }

  onOpen = () => {
    this.setState({
      opened: true,
      scanner: <QrReader
                  delay={this.props.delayRate}
                  onError={this.handleError}
                  onScan={this.handleScan}
                  facingMode={this.props.currentCamera}
                  style={{ width: '500px'}}
                />
    });
    // for some strange reason the qr camera is not redrawn
  }

  render() {
    console.log("redrawing scanner");
    return (
      <div>
        <div className="scanner">
          <div className="scanner_container">
            {this.state.scanner}
          </div>
          <p>{this.state.lastReadResult}</p>
          <p>Now using {this.props.currentCamera} camera</p>
          <p>Delay rate is {this.props.delayRate}</p>
        </div>
        <StartButton onOpen={this.onOpen} />
      </div>
    )
  }
}

export default QrScanner;