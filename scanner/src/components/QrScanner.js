import React, {Component} from 'react';

import QrReader from 'react-qr-reader'
import StartButton from './StartButton.js'

class QrScanner extends Component {
  constructor(props) {
      super(props);
      this.state = {
        result: "Test",
        initialFacingMode: "user"
      };
  }

  handleError = (event) => {
    console.log(event);
  }

  handleScan = (event) => {
    console.log(event);
  }

  render() {
    return (
      <div>
        <div className="scanner">
          <QrReader
               delay={300}
               onError={this.handleError}
               onScan={this.handleScan}
               facingMode={this.state.initialFacingMode}
               style={{ width: '500px'}}
             />
          <p>{this.state.result}</p>
        </div>
        <StartButton />
      </div>
    )
  }
}

export default QrScanner;