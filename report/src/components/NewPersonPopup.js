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

    let otherNames = [];
    this.state.otherNames.forEach(otherName => {
      if (otherName.name !== undefined &&
          otherName.name !== "" &&
          (otherName.language === "English" ||
            otherName.language === "Chinese")) {
        otherNames.push(otherName);
      }
    });

    let phoneNumbers = [];
    this.state.phoneNumbers.forEach(phoneNumber => {
      if (phoneNumber.number !== undefined &&
          phoneNumber.number !== "") {
        phoneNumbers.push(phoneNumber);
      }
    });

    let emailAddresses = [];
    this.state.emailAddresses.forEach(emailAddress => {
      if (emailAddress.email !== undefined &&
          emailAddress.email !== "" &&
          emailAddress.email.indexOf("@") > 0) {
        emailAddresses.push(emailAddress);
      }
    });

    let newPerson = {
      givenName: this.state.givenName,
      familyName: this.state.familyName,
      otherNames: otherNames,
      phoneNumbers: phoneNumbers,
      emailAddresses: emailAddresses,
      isBaptised: this.state.baptised,
      isMember: this.state.member
    };

    // make sure you sanitise the inputs here!!!!

    this.props.createPersonCallback(newPerson);
    this.props.closeNewPersonPopupCallback();
  }

  createBlankOtherName = () => {
    return {name: "", language: "English"};
  }

  addOtherName = () => {
    let otherNames = this.state.otherNames;
    otherNames.push(this.createBlankOtherName());
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
          otherNames: [this.createBlankOtherName()]
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

  buildOtherNameList = () => {
    let otherNames = [];
    for (let i = 0; i < this.state.otherNames.length; i++) {
      let id = "other_name" + i;
      let value = this.state.otherNames[i].name;
      otherNames.push(
            <div key={id} className="otherNameField">
              <input
                  id={id}
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
    return otherNames;
  }

  createBlankPhoneNumber = () => {
    return {number: "", description: ""};
  }

  addPhoneNumber = () => {
    let phoneNumbers = this.state.phoneNumbers;
    phoneNumbers.push(this.createBlankPhoneNumber());
    this.setState({
      phoneNumbers
    });
  }

  removePhoneNumber = (id) => {
    let phoneNumbers = this.state.phoneNumbers;
    if (phoneNumbers !== undefined) {
      if (phoneNumbers.length > 1) {
        phoneNumbers.splice(id, 1); // remove one item at id
        this.setState({
          phoneNumbers
        });
      } else if (phoneNumbers.length === 1) {
        this.setState({
          phoneNumbers: [this.createBlankPhoneNumber()]
        });
      }
    }
  }

  updatePhoneNumber = (id, event) => {
    let phoneNumbers = this.state.phoneNumbers;
    phoneNumbers[id].number = event.target.value;

    this.setState({
      phoneNumbers
    });
  }

  updatePhoneNumberDescription = (id, event) => {
    let phoneNumbers = this.state.phoneNumbers;
    phoneNumbers[id].description = event.target.value;

    this.setState({
      phoneNumbers
    });
  }

  buildPhoneNumberList = () => {
    let phoneNumbers = [];
    for (let i = 0; i < this.state.phoneNumbers.length; i++) {
      let id = "phone_number" + i;
      let value = this.state.phoneNumbers[i].name;
      phoneNumbers.push(
            <div key={id} className="phoneNumberField">
              <input
                  id={id}
                  className="field"
                  name="phone_number"
                  type="text"
                  value={value}
                  placeholder="The phone number/s of the person"
                  onInput={(event) => this.updatePhoneNumber(i, event)}
                  required/>
              <input
                  id={id + "label"}
                  className="field"
                  name="phone_number_label"
                  type="text"
                  value={value}
                  placeholder="Description of the number"
                  onInput={(event) => this.updatePhoneNumberDescription(i, event)}/>
              <div className="clickable" onClick={() => this.removePhoneNumber(i)}>
                <FontAwesomeIcon icon={faTimes} />
              </div>
            </div>
      );
    }
    return phoneNumbers;
  }

  createBlankEmailAddress = () => {
    return {email: "", description: ""};
  }

  addEmailAddress = () => {

  }

  removeEmailAddress = (id) => {

  }

  updateEmailAddress = (id, event) => {

  }

  updateEmailAddressDescription = (id, event) => {

  }

  buildEmailAddressList = () => {
    return undefined;
  }

  render() {
    // build a list of other names
    let otherNames = this.buildOtherNameList();
    let phoneNumbers = this.buildPhoneNumberList();
    let emailAddresses = this.buildEmailAddressList();

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
          <div className="contactForm formSection">
            <div className="collapsibleTitle">Contact</div>
            <div className="inputField">
              <label htmlFor="phone_number0" className="fieldLabel">Phone Numbers</label>
              <div className="collectedPhoneNumbers">
                {phoneNumbers}
              </div>
              <div className="clickable" onClick={this.addPhoneNumber}>
                <FontAwesomeIcon icon={faPlus} />
              </div>
            </div>
            <div className="inputField">
              <label htmlFor="email_address0" className="fieldLabel">Email Addresses</label>
              <div className="collectedEmailAddresses">
                {emailAddresses}
              </div>
              <div className="clickable" onClick={this.addEmailAddress}>
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