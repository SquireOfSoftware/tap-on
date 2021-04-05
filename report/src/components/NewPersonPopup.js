import React, {Component} from 'react';

import './NewPersonPopup.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faWindowClose, faCheck, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons'

class NewPersonPopup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      givenName: "",
      familyName: "",
      otherNames: [{name: "", language: "English"}],
      phoneNumbers: [{number: "", description: ""}],
      emailAddresses: [{email: "", description: ""}],
      baptised: false,
      member: false
    }
    this.createPerson = this.createPerson.bind(this);
    this.addOtherName = this.addOtherName.bind(this);
    this.removeOtherName = this.removeOtherName.bind(this);
  }

  createPerson = () => {
    // extract the values from the form and pass it to the callback

    let newPerson = {
      givenName: this.state.givenName,
      familyName: this.state.familyName,
      otherNames: this.state.otherNames,
      phoneNumbers: this.state.phoneNumbers,
      emailAddresses: this.state.emailAddresses,
      isBaptised: this.state.baptised,
      isMember: this.state.member
    };

    this.props.createPersonCallback(newPerson);
    this.props.closeNewPersonPopupCallback();
  }

  addOtherName = () => {
    let otherNames = this.state.otherNames;
    otherNames.push({name: "", language: "English"});
    this.setState({
      otherNames
    });
  }

  removeOtherName = (id) => {
    let otherNames = this.state.otherNames;
    if (otherNames !== undefined) {
      if (otherNames.length > 1) {
        otherNames.splice(id, 1); // remove one item at id
        this.setState({
          otherNames
        });
      } else if (otherNames.length === 1) {
        this.setState({
          otherNames: [{name: "", language: "AU"}]
        });
      }
    }
  }

  updateOtherName = (id, event) => {
    let otherNames = this.state.otherNames;
    otherNames[id].name = event.target.value;
    if (event.target.value.match(/[\u3400-\u9FBF]/)) {
      otherNames[id].language = "Chinese";
    } else {
      otherNames[id].language = "English";
    }
    this.setState({
      otherNames
    });
  }

  render() {
    // build a list of other names
    let otherNames = [];
    for (let i = 0; i < this.state.otherNames.length; i++) {
      let id = "other_name" + i;
      let value = this.state.otherNames[i].name;
      otherNames.push(
            <div key={id} className="otherNameField">
              <input
                  id={i}
                  className="field"
                  name="other_name"
                  type="text"
                  value={value}
                  placeholder="The other name/s of the person"
                  onInput={(event) => this.updateOtherName(i, event)}
                  required/>
              <div className="clickable" onClick={() => this.removeOtherName(i)}>
                <FontAwesomeIcon icon={faTimes} />
              </div>
            </div>
      );
    }

    return (
      <div className="overlay">
        <div className="newPersonForm">
          <div className="heading">
            <div className="title">Add a new person</div>
            <div className="clickable"
                 onClick={this.props.closeNewPersonPopupCallback}>
              <FontAwesomeIcon icon={faWindowClose}/>
            </div>
          </div>
          <div className="nameForm formSection">
            <div className="collapsibleTitle">Name</div>
            <div className="inputField">
              <label htmlFor="given_name" className="fieldLabel">Given Name</label>
              <input
                  className="field"
                  id="given_name"
                  name="given_name"
                  type="text"
                  placeholder="The first/given name of the person"
                  onInput={event => this.setState({givenName: event.target.value})}
                  required
                  autoFocus/>
            </div>
            <div className="inputField">
              <label htmlFor="family_name" className="fieldLabel">Family Name</label>
              <input
                  className="field"
                  id="family_name"
                  name="family_name"
                  type="text"
                  placeholder="The last/family name of the person"
                  onInput={event => this.setState({familyName: event.target.value})}
                  required/>
            </div>
            <div className="inputField">
              <label htmlFor="other_name0" className="fieldLabel">Other Name/s</label>
              <div className="collectedOtherNames">
                {otherNames}
              </div>
              <div className="clickable" onClick={this.addOtherName}>
                <FontAwesomeIcon icon={faPlus} />
              </div>
            </div>
          </div>

          <div className="createButton"
               onClick={this.createPerson}>
            <FontAwesomeIcon icon={faCheck}/> Create
          </div>
        </div>
      </div>
    );
  }
}

export default NewPersonPopup;