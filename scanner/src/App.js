import logo from './logo.svg';
import './App.css';

import Settings from './components/Settings.js'
import QrScanner from './components/QrScanner.js'
import ScanLog from './components/ScanLog.js'
import Report from './components/Report.js'

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Settings />
        <QrScanner />
        <ScanLog />
        <Report />
      </header>
    </div>
  );
}

export default App;
