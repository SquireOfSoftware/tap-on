import React, {Component} from 'react';

import './PersonPopup.css'
import './YesNoPopup.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCheck, faTimes, faSyncAlt } from '@fortawesome/free-solid-svg-icons'

class YesNoPopup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoading: false
    }
  }

  runYesOption = () => {
    if (!this.state.isLoading) {
      this.setState({
        isLoading: true
      });
      this.props.yesCallback(() => {
        this.setState({
          isLoading: false
        });
      });
    }
  }

  render () {
    let confirmationIcon = this.state.isLoading ? <FontAwesomeIcon className="loadingFrame" icon={faSyncAlt} /> : <FontAwesomeIcon icon={faCheck} />;
    return (
      <div className="overlay yesNoOverlay">
        <div className="yesNoPopup">
          <div className="message">
            {this.props.message}
          </div>
          <div className="buttons">
            <div className="yesOption clickable" onClick={this.runYesOption}>
              {confirmationIcon} Yes
            </div>
            <div className="noOption clickable" onClick={this.props.noCallback}>
              <FontAwesomeIcon icon={faTimes} /> No
            </div>
          </div>
        </div>
      </div>
    )
  }
}

export default YesNoPopup;