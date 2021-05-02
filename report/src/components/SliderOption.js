import React, {Component} from 'react';

import Toggle from 'react-toggle'
import 'react-toggle/style.css'

class SliderOption extends Component {
  constructor(props) {
    super(props);

    let defaultValue = this.props.defaultValue;
    if (defaultValue !== true) {
      defaultValue = false;
    }

    this.state = {
      sliderValue: defaultValue
    }
    this.toggleValue = this.toggleValue.bind(this);
  }

  toggleValue = () => {
    let newValue = !this.state.sliderValue;
    this.setState({
      sliderValue: newValue
    });
    this.props.changeValue(newValue);
  }

  render() {
    return (
      <div className={"inputField " + this.props.sliderClassName}>
        <label htmlFor={this.props.sliderHtmlId} className="fieldLabel">{this.props.fieldLabel}</label>
        <Toggle id={this.props.sliderHtmlId}
              defaultChecked={this.state.sliderValue}
              onChange={this.toggleValue}/>
        <label htmlFor={this.props.sliderHtmlId} className="toggleLabel">{this.props.generateContentLabel(this.state.sliderValue)}</label>
      </div>
    );
  }
}

export default SliderOption;