import React, {Component} from 'react';

import './RegenerateQrCodeButton.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt, faChevronUp, faChevronDown } from '@fortawesome/free-solid-svg-icons'

class RegenerateQrCodeButton extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expanded: false
    }
  }

  toggleDisplay = () => {
    let isExpanded = this.state.expanded;
    this.setState({
      expanded: !isExpanded
    });
  }

  render() {
    let isDisplayed = this.state.expanded ? "" : "hidden";
    return (
      <div className="formSection">
        <div className="collapsibleTitle expandable" onClick={this.toggleDisplay}>Qr Code options <FontAwesomeIcon icon={this.state.expanded ? faChevronUp : faChevronDown} /></div>
        <div className={isDisplayed}>
          <div className="clickable regenerateButton" onClick={this.props.regenerateQrCode}>
            <FontAwesomeIcon icon={faSyncAlt} />
            <span className="regenerateButtonText">Regenerate</span>
          </div>
          <div className="warningLabel">
            Please take note that QR code regeneration cannot be reversed
          </div>
        </div>
      </div>
    );
  }
}

export default RegenerateQrCodeButton;