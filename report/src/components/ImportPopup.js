import React, {Component} from 'react';

import './PersonPopup.css'
import './ImportPopup.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faWindowClose, faCheck, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons'

import { useTable, useRowSelect } from 'react-table'


class ImportPopup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showImportButton: false,
      headerPreview: undefined,
      dataPreview: undefined
    }
  }

  importUsers = () => {

    // on success close the popup
    this.props.closeImportPopupCallback();
  }

  readFile = (event) => {
    console.log(event.target.files);
    // assume that there is one file always
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.readAsText(file);
    reader.onload = (event) => {
      let csv = event.target.result;
      if (csv !== undefined && csv.length > 0) {
        let splitData = csv.split("\n");

        let header = splitData[0].split(",");
        let rawData = splitData.slice(1);

        let parsedData = [];
        for (let i = 0; i < rawData.length; i++) {
          parsedData.push(this.formObject(header, rawData[i]));
        }

        this.setState({
          headerPreview: this.formHeader(header),
          dataPreview: parsedData,
          showImportButton: true
        });
      }
    };
    reader.onerror = () => {
      alert('Unable to read ' + file.fileName);
      this.setState({
        headerPreview: undefined,
        dataPreview: undefined,
        showImportButton: false
      });
    };
  }

  formObject = (splitHeader, row) => {
    // split the row
    let splitRow = row.split(",");

    // for each of the split headers for the object
    let dataObject = {};
    for (let i = 0; i < splitHeader.length; i++) {
      dataObject[splitHeader[i]] = splitRow[i];
    }
    return dataObject;
  }

  formHeader = (splitHeader) => {
    return splitHeader.map(item => {
      return {
        "Header": item,
        "accessor": item
      };
    });
  }

  render() {
    let errors = [];
    let importButton = undefined;
    let csvPreview = undefined;

    if (this.state.showImportButton) {
      importButton = <div className="importButton"
                          onClick={this.importUsers}>
                       <FontAwesomeIcon icon={faCheck}/> Import {this.state.dataPreview.length} people
                     </div>
      csvPreview =
          <div className="tableForm formSection">
            <div className="collapsibleTitle">Data preview</div>
            <div className="overflowWrapper">
              <Table data={this.state.dataPreview}
                      columns={this.state.headerPreview} />
            </div>
          </div>
    }

    return (
      <div className="overlay">
        <div className="importForm">
          <div className="heading">
            <div className="title">Import new people</div>
            <div className="clickable"
                 onClick={this.props.closeImportPopupCallback}>
              <FontAwesomeIcon icon={faWindowClose}/>
            </div>
          </div>
          <div className="importForm formSection">
            <div className="collapsibleTitle">Select a file</div>
            <div className="inputField">
              <input
                  className=""
                  id="file_importer"
                  name="file_importer"
                  type="file"
                  placeholder="Pick a CSV file to import"
                  onInput={this.readFile}
                  required
                  autoFocus/>
            </div>
          </div>
          {csvPreview}
          {errors}
          {importButton}
          <div className="warningLabel">
            Please take note that this import will only <strong>add</strong> people.
            This will not update any existing information.
          </div>
        </div>
      </div>
    );
  }
}

function Table({ columns, data }) {
  // Use the state and functions returned from useTable to build your UI
  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow,
  } = useTable({
    columns,
    data,
  })

  // Render the UI for your table
  return (
    <table {...getTableProps()}>
      <thead>
        {headerGroups.map(headerGroup => (
          <tr {...headerGroup.getHeaderGroupProps()}>
            {headerGroup.headers.map(column => (
              <th {...column.getHeaderProps()}>{column.render('Header')}</th>
            ))}
          </tr>
        ))}
      </thead>
      <tbody {...getTableBodyProps()}>
        {rows.map((row, i) => {
          prepareRow(row)
          return (
            <tr {...row.getRowProps()}>
              {row.cells.map(cell => {
                return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
              })}
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}

export default ImportPopup;