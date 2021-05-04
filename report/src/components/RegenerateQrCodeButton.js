import React, {Component} from 'react';

import './RegenerateQrCodeButton.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt, faChevronUp, faChevronDown } from '@fortawesome/free-solid-svg-icons'
import YesNoPopup from './YesNoPopup.js'

class RegenerateQrCodeButton extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expanded: false,
      displayYesNo: false
    }
  }

  toggleDisplay = () => {
    let isExpanded = this.state.expanded;
    this.setState({
      expanded: !isExpanded
    });
  }

  confirmAction = () => {
    this.setState({
      displayYesNo: true
    });
  }

  closeYesNoPopup = () => {
    this.setState({
      displayYesNo: false
    });
  }

  regenerateQrCode = (callback) => {
    this.props.regenerateQrCode(() => {
      this.closeYesNoPopup();
      callback();
    });
  }

  render() {
    let isDisplayed = this.state.expanded ? "" : "hidden";
    let yesNoPopup = undefined;
    if (this.state.displayYesNo) {
      yesNoPopup = <YesNoPopup message={"Are you sure you want to regenerate the Qr Code? " +
                               "You will not be able to change back to the old Qr Code."}
                               yesCallback={this.regenerateQrCode}
                               noCallback={this.closeYesNoPopup} />
    }

    return (
      <div className="formSection">
        <div className="collapsibleTitle expandable" onClick={this.toggleDisplay}>Qr Code options <FontAwesomeIcon icon={this.state.expanded ? faChevronUp : faChevronDown} /></div>
        <div className={isDisplayed}>
          <div className="clickable regenerateButton" onClick={this.confirmAction}>
            <FontAwesomeIcon icon={faSyncAlt} />
            <span className="regenerateButtonText">Regenerate</span>
          </div>
          <div className="warningLabel">
            Please take note that QR code regeneration cannot be reversed
          </div>
        </div>
        {yesNoPopup}
      </div>
    );
  }
}

export default RegenerateQrCodeButton;