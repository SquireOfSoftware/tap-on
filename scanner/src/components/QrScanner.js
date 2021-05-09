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
        scannedResults: [],
        currentCamera: this.props.currentCamera,
        delayRate: this.props.delayRate
      };
      this.updateDelayRate = this.updateDelayRate.bind(this);
      this.updateCamera = this.updateCamera.bind(this);
      this.handleScan = this.handleScan.bind(this);
  }

  componentDidMount() {
    this.props.updateDelayRate(this.updateDelayRate);
    this.props.updateCamera(this.updateCamera)
  }

  handleError = (event) => {
  }

  handleScan = (event) => {
    // log the scan attempt
    let scannedResult = {
      value: event,
      timestamp: new Date()
    };

    let scannedResults = this.state.scannedResults;
    let scanExists = false;
    let storedResult;
    for (let i = 0; i < scannedResults.length; i++) {
      storedResult = scannedResults[i];
      if (storedResult.value === scannedResult.value) {
        storedResult.count = storedResult.count !== undefined ?
          storedResult.count + 1 : 0;
        scanExists = true;
        break;
      }
    }

    this.setState({
      lastReadResult: event
    });

    if (scanExists) {
      // do nothing
    } else {
      // add the scan to the array
      scannedResults.push(scannedResult);
      this.setState({
        scannedResults: scannedResults
      });
    }

    if (event !== null && event !== undefined) {
      this.props.addLog({message: "Scanned in: " + event});
      // process the scan
      this.props.processScan(event);
    }
  }

  onOpen = () => {
    this.setState({
      opened: true,
      scanner: <QrReader
                  delay={this.state.delayRate}
                  onError={this.handleError}
                  onScan={this.handleScan}
                  facingMode={this.state.currentCamera}
                  style={{ width: '500px'}}
                />
    });
    // for some strange reason the qr camera is not redrawn
  }

  updateDelayRate = (newDelayRate) => {
    if (this.state.opened) {
      this.setState({
        delayRate: newDelayRate,
        scanner: <QrReader
                    delay={newDelayRate}
                    onError={this.handleError}
                    onScan={this.handleScan}
                    facingMode={this.state.currentCamera}
                    style={{ width: '500px'}}
                  />
      });
    }
  }

  updateCamera = (newCamera) => {
    if (this.state.opened) {
      this.setState({
        currentCamera: newCamera,
        scanner: <QrReader
                    delay={this.state.delayRate}
                    onError={this.handleError}
                    onScan={this.handleScan}
                    facingMode={newCamera}
                    style={{ width: '500px'}}
                  />
      });
    }
  }

  render() {
    return (
      <div>
        <div className="scanner">
          <div className="scanner_container">
            {this.state.scanner}
          </div>
        </div>
        <StartButton onOpen={this.onOpen} />
      </div>
    )
  }
}

export default QrScanner;