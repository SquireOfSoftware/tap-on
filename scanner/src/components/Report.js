import React, {Component} from 'react';

class Report extends Component {
  constructor(props) {
    super(props);
    this.state = {
      total: 0,
      scannedPeople: []
    }
  }

  render() {
    return (
      <div>
        Report
      </div>
    )
  }
}

export default Report;