import React, {Component} from 'react';

import './PersonPopup.css'
import './QrReport.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faWindowClose } from '@fortawesome/free-solid-svg-icons'

class QrReport extends Component {
  constructor(props) {
    super(props);

    this.state = {
      peopleMap: this.props.peopleMap
    };
  }

  render() {
    let body = [];

    this.state.peopleMap.forEach(person => {
      let givenName = person.givenName;

      let familyNameSection = undefined;
      if (person.familyName !== undefined &&
          person.familyName !== null &&
          person.familyName.length > 0) {
        familyNameSection = <span className="familyName">
                              {person.familyName.toLocaleUpperCase()}
                            </span>
      }

      let otherNamesSection = undefined;
      if (person.otherNames !== undefined &&
          person.otherNames.length > 0) {
        let otherNames = [];
        person.otherNames.forEach(name => {
          if (name !== undefined) {
            otherNames.push(
              <span key={person.hash}>{name.name}</span>
            );
          }
        });
        otherNamesSection = <div className="otherNames">{otherNames}</div>
      }

      body.push(
        <div className="idBlock" key={person.id + person.hash}>
          <div className="nameSection">
            <span className="givenName">
              {givenName}
            </span>
            {familyNameSection}
            {otherNamesSection}
          </div>
          <div className="qrCodeSection">
            <img src={this.props.getQrCodeLink(person.id)} />
          </div>
        </div>
      );
    });

    return (
      <div className="overlay">
        <div className="qrReportPopup">
          <div className="heading qrReportHeading">
            <div className="title">Qr Code Report</div>
            <div className="clickable"
                 onClick={this.props.closeQrReportCallback}>
              <FontAwesomeIcon icon={faWindowClose}/>
            </div>
          </div>
          <div className="body">
            {body}
          </div>
        </div>
      </div>
    );
  }
}

export default QrReport;