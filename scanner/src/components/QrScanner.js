import React, {Component} from 'react';
import './QrScanner.css'

import QrReader from 'react-qr-reader'
import StartButton from './StartButton.js'

class QrScanner extends Component {
  constructor(props) {
      super(props);
      this.state = {
        result: "Test",
        opened: false
      };
  }

  handleError = (event) => {
    console.log(event);
  }

  handleScan = (event) => {
    console.log(event);
    this.props.addLog(event);
  }

  onOpen = () => {
    this.setState({
      opened: true,
      scanner: <QrReader
                  delay={300}
                  onError={this.handleError}
                  onScan={this.handleScan}
                  facingMode={this.props.currentCamera}
                  style={{ width: '500px'}}
                />
    });

  }

  render() {
    return (
      <div>
        <div className="scanner">
          {this.state.scanner}
          <p>{this.state.result}</p>
          <p>Now using {this.props.currentCamera} camera</p>
        </div>
        <StartButton onOpen={this.onOpen} />
      </div>
    )
  }
}

export default QrScanner;