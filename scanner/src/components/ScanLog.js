import React, {Component} from 'react';

class ScanLog extends Component {
  render() {
    console.log("REDRAWING SCANLOG");

    let logs = [];
    for (let i = 0; i < this.props.logs.length; i++) {
      let log = this.props.logs[i];
      if (log === null) {
        log = "null";
      }
      logs.push(<div>{log}</div>)
    }

    return (
      <div>
        <p>ScanLog</p>
        {logs}
      </div>
    )
  }
}

export default ScanLog;