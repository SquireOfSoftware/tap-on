import React, {Component} from 'react';
import './StartButton.css'

class StartButton extends Component {
  constructor(props) {
    super(props);
    this.state = {
      enabled: true,
      startCssName: props.startButtonCss,
      modalOverlayCssName: props.modalOverlayCss
    }
  }

  toggleButton = (event) => {
    let buttonEnabled = !this.state.enabled;
    this.setState({
      enabled: buttonEnabled,
      startCssName: this.props.startButtonCss + (buttonEnabled ? "" : " " + this.props.startButtonExitCss),
      modalOverlayCssName: this.props.modalOverlayCss + (buttonEnabled ? "" : " " + this.props.modalOverlayExitCss)
    });
  }

  render() {
    return (
      <div>
        <div className={this.state.modalOverlayCssName}>
        </div>
        <div className={this.state.startCssName}
            onClick={this.toggleButton}>
          START
        </div>
      </div>
    )
  }
}

StartButton.defaultProps = {
  startButtonCss: 'startButton',
  modalOverlayCss: 'modalOverlay',
  hiddenCss: 'hidden',
  startButtonExitCss: 'startButtonExit',
  modalOverlayExitCss: 'modalOverlayExit'
}

export default StartButton;