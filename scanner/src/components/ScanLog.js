import React, {Component} from 'react';

class ScanLog extends Component {
  constructor(props) {
    super(props);
    this.state = {
      logs: this.props.logs
    }
  }

  render() {
    return (
      <div>
        <p>ScanLog</p>
        {this.state.logs}
      </div>
    )
  }
}

export default ScanLog;