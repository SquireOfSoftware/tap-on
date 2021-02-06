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
        scanner: null,
        scannedResults: []
      };
  }

  handleError = (event) => {
//    console.log(event);
  }

  handleScan = (event) => {
    // log the scan attempt
    console.log(event);
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
      // updated the scanned results
      this.setState({
        scannedResults: scannedResults
      });
    } else {
      // add the scan to the array
      scannedResults.push(scannedResult);
      this.setState({
        scannedResults: scannedResults
      });
    }

    if (event !== null && event !== undefined) {
      this.props.addLog("Scanned in: " + event);
      // process the scan
      this.props.processScan(event);
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