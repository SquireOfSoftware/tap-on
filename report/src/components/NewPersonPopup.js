import React, {Component} from 'react';

import './PersonPopup.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faWindowClose, faCheck, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons'

import SliderOption from './SliderOption.js'

class NewPersonPopup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      givenName: "",
      familyName: "",
      otherNames: [],
      phoneNumbers: [],
      emailAddresses: [],
      baptised: false,
      member: false,
      visitor: false,
      errors: []
    }
    this.createPerson = this.createPerson.bind(this);
    this.addOtherName = this.addOtherName.bind(this);
    this.removeOtherName = this.removeOtherName.bind(this);
  }

  createPerson = () => {
    // extract the values from the form and pass it to the callback

    let otherNames = [];
    this.state.otherNames.forEach((otherName, index) => {
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
      isMember: this.state.member,
      isVisitor: this.state.visitor
    };

    // make sure you sanitise the inputs here!!!!
    let isGivenNameValid = this.nameIsValid(this.state.givenName);
    let isFamilyNameValid = this.nameIsValid(this.state.familyName);

    if (isGivenNameValid && isFamilyNameValid) {
      this.props.createPersonCallback(newPerson);
      this.props.closeNewPersonPopupCallback();
      this.props.reloadPeople();
    } else {
      let givenNameError;
      let familyNameError;
      if (!isGivenNameValid) {
        givenNameError = "The given name must have at least one character.";
      }
      if (!isFamilyNameValid) {
        familyNameError = "The family name must have at least one character.";
      }
      this.setState({
        errors: [givenNameError, familyNameError]
      });
      console.error("Please review your inputs before proceeding");
    }
  }

  nameIsValid = (name) => {
    return name !== undefined &&
        name !== "" &&
        name.length > 0;
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
      otherNames.splice(id, 1); // remove one item at id
      this.setState({
        otherNames
      });
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
      phoneNumbers.splice(id, 1); // remove one item at id
      this.setState({
        phoneNumbers
      });
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
      let value = this.state.phoneNumbers[i].number;
      let description = this.state.phoneNumbers[i].description;
      phoneNumbers.push(
            <div key={id} className="phoneNumberField">
              <input
                  id={id}
                  className="field"
                  name="phone_number"
                  type="text"
                  value={value}
                  placeholder="The phone number of the person"
                  onInput={(event) => this.updatePhoneNumber(i, event)}
                  required/>
              <input
                  id={id + "label"}
                  className="field"
                  name="phone_number_label"
                  type="text"
                  value={description}
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
    let emailAddresses = this.state.emailAddresses;
    emailAddresses.push(this.createBlankEmailAddress());
    this.setState({
      emailAddresses
    });
  }

  removeEmailAddress = (id) => {
    let emailAddresses = this.state.emailAddresses;
    if (emailAddresses !== undefined) {
      emailAddresses.splice(id, 1); // remove one item at id
      this.setState({
        emailAddresses
      });
    }
  }

  updateEmailAddress = (id, event) => {
    let emailAddresses = this.state.emailAddresses;
    emailAddresses[id].email = event.target.value;

    this.setState({
      emailAddresses
    });
  }

  updateEmailAddressDescription = (id, event) => {
    let emailAddresses = this.state.emailAddresses;
    emailAddresses[id].description = event.target.value;

    this.setState({
      emailAddresses
    });
  }

  buildEmailAddressList = () => {
    let emailAddresses = [];
    for (let i = 0; i < this.state.emailAddresses.length; i++) {
      let id = "email_address" + i;
      let value = this.state.emailAddresses[i].email;
      let description = this.state.emailAddresses[i].description;
      emailAddresses.push(
            <div key={id} className="emailAddressField">
              <input
                  id={id}
                  className="field"
                  name="email_address"
                  type="text"
                  value={value}
                  placeholder="The email address of the person"
                  onInput={(event) => this.updateEmailAddress(i, event)}
                  required/>
              <input
                  id={id + "label"}
                  className="field"
                  name="email_address_label"
                  type="text"
                  value={description}
                  placeholder="Description of the email"
                  onInput={(event) => this.updateEmailAddressDescription(i, event)}/>
              <div className="clickable" onClick={() => this.removeEmailAddress(i)}>
                <FontAwesomeIcon icon={faTimes} />
              </div>
            </div>
      );
    }
    return emailAddresses;
  }

  buildErrors = () => {
    if (this.state.errors.length > 0) {
      let errors = [];
      for (let i = 0; i < this.state.errors.length; i++) {
        errors.push(
          <div key={i}>
            {this.state.errors[i]}
          </div>
        );
      }
      return (
        <div className="errorMessage">
          <div>Please review your errors before proceeding</div>
          {errors}
        </div>
      );
    }
    return undefined;
  }

  render() {
    // build a list of other names
    let otherNames = this.buildOtherNameList();
    let phoneNumbers = this.buildPhoneNumberList();
    let emailAddresses = this.buildEmailAddressList();

    let isGivenNameFieldValid = this.nameIsValid(this.state.givenName);
    let isFamilyNameFieldValid = this.nameIsValid(this.state.familyName);

    let errors = this.buildErrors();

    return (
      <div className="overlay">
        <div className="newPersonForm">
          <div className="warningLabel">
            This information will only be used for attendance and will not be shared with any other services.
          </div>
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
                  className={"field" + (isGivenNameFieldValid ? "" : " fieldError")}
                  id="given_name"
                  name="given_name"
                  type="text"
                  placeholder="The first/given name of the person"
                  onInput={event =>
                      this.setState({givenName: event.target.value, errors: []})}
                  required
                  autoFocus/>
            </div>
            <div className="inputField">
              <label htmlFor="family_name" className="fieldLabel">Family Name</label>
              <input
                  className={"field" + (isFamilyNameFieldValid ? "" : " fieldError")}
                  id="family_name"
                  name="family_name"
                  type="text"
                  placeholder="The last/family name of the person"
                  onInput={event =>
                      this.setState({familyName: event.target.value, errors: []})}
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
          <div className="contactForm formSection">
            <div className="collapsibleTitle">Other</div>
            <SliderOption defaultValue={false}
                          sliderHtmlId="baptised_status"
                          sliderClassName="baptisedField"
                          fieldLabel="Is Baptised?"
                          generateContentLabel={value => {return "This person is " + (value ? "baptised" : "not baptised")}}
                          changeValue={newValue => {
                            this.setState({baptised: newValue});
                          }}
            />
            <SliderOption defaultValue={false}
                          sliderHtmlId="member_status"
                          sliderClassName="memberField"
                          fieldLabel="Is a Member?"
                          generateContentLabel={value => {return "This person is " + (value ? "a member" : "not a member")}}
                          changeValue={newValue => {
                            this.setState({member: newValue});
                          }}
            />
            <SliderOption defaultValue={false}
                          sliderHtmlId="visitor_status"
                          sliderClassName="visitorField"
                          fieldLabel="Is a Visitor?"
                          generateContentLabel={value => {return "This person is " + (value ? "a visitor" : "not a visitor")}}
                          changeValue={newValue => {
                            this.setState({visitor: newValue});
                          }}
            />
          </div>
          {errors}
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