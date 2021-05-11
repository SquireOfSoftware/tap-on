import React, {Component} from 'react';

import { useTable, useRowSelect } from 'react-table'

import './CheckList.css'
import ServerStates from './ServerStates.js'
import NewPersonPopup from './NewPersonPopup.js'
import EditPersonPopup from './EditPersonPopup.js'
import ImportPopup from './ImportPopup.js'
import QrReport from './QrReport.js'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt, faSignature, faUserEdit,
         faUserPlus, faFileUpload, faFileDownload,
         faIdCard } from '@fortawesome/free-solid-svg-icons'
import moment from 'moment'

class CheckList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      peopleMap: undefined,
      serverState: ServerStates.UNCHECKED,
      startTime: this.props.initialStartTime,
      serverUrl: this.props.initialServerUrl,
      autoRefreshPeople: this.props.initialAutoRefreshPeople,
      showNewPersonPopup: false,
      showEditPersonPopup: false,
      showImportPopup: false,
      showQrReport: false,
      signInCount: 0
    }
    this.loadPeople = this.loadPeople.bind(this);
    this.loadTodaysSignins = this.loadTodaysSignins.bind(this);
    this.queryServer = this.queryServer.bind(this);
    this.postToServer = this.postToServer.bind(this);
    this.showNewPersonPopup = this.showNewPersonPopup.bind(this);
    this.closeNewPersonPopupCallback = this.closeNewPersonPopupCallback.bind(this);
    this.createPersonCallback = this.createPersonCallback.bind(this);
    this.loadPeople();
  }

  componentDidMount() {
    this.props.disableAutoRefreshPeople(this.disableAutoRefreshPeople);
    this.props.enableAutoRefreshPeople(this.enableAutoRefreshPeople);
    this.props.changeStartTime(this.changeStartTime);

    if (this.state.autoRefreshPeople === true) {
      this.enableAutoRefreshPeople();
    } else {
      this.disableAutoRefreshPeople();
    }
  }

  componentWillUnmount() {
    if (this.timer !== undefined) {
      clearInterval(this.timer);
    }
    this.timer = null;
  }

  changeStartTime = (startTime) => {
    this.setState({
      startTime: startTime
    });
  }

  enableAutoRefreshPeople = () => {
    this.setState({
      autoRefreshPeople: true
    });
    if (this.timer !== undefined) {
      clearInterval(this.timer);
    }

    this.timer = setInterval(()=> this.loadPeople(), 5000);
  }

  disableAutoRefreshPeople = () => {
    this.setState({
      autoRefreshPeople: false
    });

    if (this.timer !== undefined) {
      clearInterval(this.timer);
    }

    this.timer = null;
  }

  queryServer = (url, successCallback) => {
    this.setState({
      serverState: ServerStates.CHECKING
    });

    let successGET = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverState);
        successCallback(event);
      } else {
        this.setState({
          serverState: ServerStates.DOWN
        });
        this.props.updateServerState(this.state.serverState);
      }
    }

    let failedGET = (event) => {
      console.error("An error occurred while checking the server health.");
      this.setState({
        serverState: ServerStates.DOWN
      });
      this.props.updateServerState(this.state.serverState);
    }

    let getRequest = new XMLHttpRequest();

    getRequest.addEventListener("load", successGET);
    getRequest.addEventListener("error", failedGET);
    console.debug(url);
    getRequest.open("GET", url, true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();

    console.debug(getRequest);
  }

  postToServer = (url, successCallback, userErrorCallback, postBody) => {
    return this.uploadToServer("POST", url, successCallback, userErrorCallback, postBody, {"Content-Type": "application/json"}, true);
  }

  putToServer = (url, successCallback, userErrorCallback, putBody) => {
    return this.uploadToServer("PUT", url, successCallback, userErrorCallback, putBody, {"Content-Type": "application/json"}, true);
  }

  uploadToServer = (method, url, successCallback, userErrorCallback, body, customHeaders, stringifyBody) => {
    this.setState({
      serverState: ServerStates.CHECKING
    });

    let successfulRequest = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverState);
        successCallback(event);
      } else if (event.target.status >= 400 || event.target.status < 500) {
        this.setState({
          serverState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverState);
        userErrorCallback(event);
      } else {
        this.setState({
          serverState: ServerStates.DOWN
        });
        this.props.updateServerState(this.state.serverState);
      }
    }

    let failedRequest = (event) => {
      console.error("An error occurred while checking the server health.");
      this.setState({
        serverState: ServerStates.DOWN
      });
      this.props.updateServerState(this.state.serverState);
    }

    let request = new XMLHttpRequest();

    request.addEventListener("load", successfulRequest);
    request.addEventListener("error", failedRequest);
    console.debug(url);
    request.open(method, url, true);
    request.setRequestHeader("Access-Control-Allow-Headers", "*");

    for (let i = 0; i < Object.keys(customHeaders).length; i++) {
      request.setRequestHeader(Object.keys(customHeaders)[i],
                               Object.entries(customHeaders)[i][1]);
    }

    if (body !== undefined && body !== null) {
      if (stringifyBody) {
        request.send(JSON.stringify(body));
      } else {
        request.send(body);
      }
    } else {
      request.send();
    }

    console.debug(request);
  }

  loadTodaysSignins = () => {
    this.queryServer(
      this.state.serverUrl + "/people-service/checkin/signins/from/" + this.state.startTime,
      (event) => {
        // here we want to parse the results and find the right people in the array
        // then set their hasSignedIn to true
        // if no applicable person is found the person is then just left alone

        // {timestamp:..., person: {}}

        let checkinLogs = JSON.parse(event.target.responseText);
        this.setState({
          checkinLogs: checkinLogs
        });

        let newPeopleMap = this.state.peopleMap;

        if (newPeopleMap !== undefined && newPeopleMap.size > 0) {
          let signInCount = 0;
          checkinLogs.forEach(log => {
            let person = log.person;
            // find the person in the state via the peopleMap
            let personEntry = newPeopleMap.get(person.id);
            if (personEntry !== undefined || personEntry !== null) {
              personEntry.hasSignedIn = "true";
              personEntry.firstSignIn = moment(log.timestamp).format("hh:mm:ss A");
              newPeopleMap[person.id] = personEntry;
              signInCount++;
            }
          });

          this.setState({
            ...this.state,
            peopleMap: newPeopleMap,
            signInCount: signInCount
          });
        }
      });
  }

  loadPeople = () => {
    this.queryServer(
      this.state.serverUrl + "/people-service/people/",
      (event) => {
        let people = JSON.parse(event.target.responseText);

        // need to store this as a map, id -> person
        // also convert all http requests over to https
        // if the main source is https
        let peopleMap = new Map(people.map(person => {
          if (window.location.protocol === 'https:') {
            let links = person.links.map(link => {
              return {
                rel: link.rel,
                href: link.href.replace("http:", "https:")
              }
            });
            person.links = links;
          }

          return [person.id, person];
        }));

        this.setState({
          peopleMap: peopleMap
        });
        this.loadTodaysSignins();
      });
  }

  bulkSignInHandler = (selectedHashes) => {
    this.postToServer(
        this.state.serverUrl + "/people-service/checkin/signin/people/",
        (event) => {
          this.loadTodaysSignins();
        },
        (event) => {
          let error = JSON.parse(event.target.responseText);
          console.error(error.message);
          // most likely someones hash got changed
          console.error("Most likely someone's hash got changed, reload the page and try again");
        },
        {
          hashes: selectedHashes,
          message: "bulk manual sign in"
        }
    )
  }

  showNewPersonPopup = () => {
    this.setState({
      showNewPersonPopup: true
    });
  }

  closeNewPersonPopupCallback = () => {
    this.setState({
      showNewPersonPopup: false
    });
  }

  createPersonCallback = (personToBeCreated) => {
    console.log(personToBeCreated);
    // submit the actual call to create the person
    // then reload the whole table
    // people-service/people
    this.postToServer(
      this.state.serverUrl + "/people-service/people/",
      (event) => {
        this.loadPeople();
      },
      (event) => {
        let error = JSON.parse(event.target.responseText);
        console.error(error.message);
        // most likely someones hash got changed
        console.error("Failed to create the person");
      },
      [personToBeCreated]
    )
  }

  showEditPersonPopup = (partialPerson) => {
    this.queryServer(
        this.state.serverUrl + "/people-service/people/id/" + partialPerson.id,
        (event) => {
          let person = JSON.parse(event.target.responseText);
          if (person !== undefined) {
            this.setState({
              showEditPersonPopup: true,
              personToBeEdited: person
            });
          }
        });
  }

  updatePerson = (personId, personToBeEdited, successCallback, errorCallback) => {
    this.putToServer(
      this.state.serverUrl + "/people-service/people/id/" + personId,
      successCallback,
      errorCallback,
      personToBeEdited
    )
  }

  closeEditPersonPopupCallback = () => {
    this.setState({
      showEditPersonPopup: false,
      personToBeEdited: undefined
    });
  }

  getQrCodeLink = (personId) => {
    return this.state.serverUrl + "/people-service/people/id/" + personId + "/qrcode";
  }

  regenerateQrCode = (personId, callback) => {
    this.postToServer(
      this.getQrCodeLink(personId) + ":recreate",
      callback,
      event => {
        console.error("There was an issue with regenerating the QR code");
        let error = JSON.parse(event.target.responseText);
        console.error(error.message);
      }
    );
  }

  showImportPopup = () => {
    this.setState({
      showImportPopup: true
    });
  }

  closeImportPopupCallback = () => {
    this.setState({
      showImportPopup: false
    });
    this.loadPeople();
  }

  importPeopleCallback = (file, successCallback, errorCallback) => {
    let data = new FormData();
    data.append("file", file);

    this.uploadToServer(
      "POST",
      this.state.serverUrl + "/people-service/people/import",
      (successEvent) => { successCallback(successEvent); },
      (errorEvent) => { errorCallback(errorEvent); },
      data,
      {},
      false
    );
  }

  showQrReport = () => {
    this.setState({
      showQrReport: true
    });
  }

  closeQrReportCallback = () => {
    this.setState({
      showQrReport: false
    });
  }

  render() {
    let columns = [
      {
        Header: 'Name',
        columns: [
          {
            Header: 'Given Name',
            accessor: 'givenName',
          },
          {
            Header: 'Family Name',
            accessor: 'familyName',
          },
          {
            Header: 'Other Names',
            accessor: 'otherNames',
            className: 'otherNameColumn',
            Cell: ({row, value}) => {
              let otherNames;
              if (value !== undefined && value.length > 0) {
                 otherNames = value
                    .map(name => name.name)
                    .join(', ');
              }

              return (
                <div>
                  {otherNames}
                </div>
              );
            }
          },
        ],
      },
      {
        Header: 'Info',
        columns: [
          {
            Header: 'Signed In At',
            accessor: 'firstSignIn',
            className: "infoColumn"
          },
          {
            Header: 'Manual sign in',
            accessor: 'manualSignIn',
            className: "infoColumn",
            Cell: ({row}) => {
              let clickPostCallback = (event) => {
                this.postToServer(
                  this.state.serverUrl + "/people-service/checkin/signin/hash/" + row.original.hash + "?message=manual sign in",
                  (event) => {
                    this.loadTodaysSignins();
                  });
              }

              return (
                <div className="clickable" onClick={clickPostCallback}>
                  <span>Sign in </span>
                  <FontAwesomeIcon icon={faSignature}/>
                </div>
              );
            }
          },
          {
            Header: 'Account',
            className: "infoColumn",
            Cell: ({row}) => {
              return (
                <div className="clickable" onClick={() => this.showEditPersonPopup(row.original)}>
                  <FontAwesomeIcon icon={faUserEdit}/>
                </div>
              );
            }
          }
        ],
      },
    ]

    let dataArray = [];
    if (this.state.peopleMap !== undefined) {
      dataArray = [...this.state.peopleMap.values()];
    }

    let table = <Table
      columns={columns}
      data={dataArray}
      bulkSignInHandler={this.bulkSignInHandler}
    />

    let popup = undefined;
    if (this.state.showNewPersonPopup) {
      popup = <NewPersonPopup createPersonCallback={this.createPersonCallback}
                                    reloadPeople={this.loadPeople}
                                    closeNewPersonPopupCallback={this.closeNewPersonPopupCallback}/>
    } else if (this.state.showEditPersonPopup) {
      let qrCodeLink = this.getQrCodeLink(this.state.personToBeEdited.id);
      popup = <EditPersonPopup person={this.state.personToBeEdited}
                                     updatePerson={this.updatePerson}
                                     closeEditPersonPopupCallback={this.closeEditPersonPopupCallback}
                                     reloadPeople={this.loadPeople}
                                     qrCodeLink={qrCodeLink}
                                     regenerateQrCode={this.regenerateQrCode}/>
    } else if (this.state.showImportPopup) {
      popup = <ImportPopup closeImportPopupCallback={this.closeImportPopupCallback}
                            importPeopleCallback={this.importPeopleCallback}/>
    } else if (this.state.showQrReport && this.state.peopleMap !== undefined) {
      popup = <QrReport peopleMap={this.state.peopleMap}
                        closeQrReportCallback={this.closeQrReportCallback}
                        getQrCodeLink={this.getQrCodeLink}/>
    }

    return (
      <div>
        <div className="adminBar">
          <div className={this.state.peopleMap !== undefined ? "clickable adminButton" : "disabled adminButton"}
               onClick={() => {
                if (this.state.peopleMap !== undefined) {
                  this.showQrReport()
                }
               }
             }>
            <FontAwesomeIcon icon={faIdCard}/>
          </div>
          <a className="clickable adminButton" href={this.state.serverUrl + "/people-service/checkin/signins/from/" + this.state.startTime + "/csv"}
            target="_blank" rel="noreferrer">
            <FontAwesomeIcon icon={faFileDownload}/>
          </a>
          <div className="clickable adminButton" onClick={() => this.showImportPopup()}>
            <FontAwesomeIcon icon={faFileUpload}/>
          </div>
          <div className="clickable adminButton" onClick={() => this.showNewPersonPopup()}>
            <FontAwesomeIcon icon={faUserPlus}/>
          </div>
          <div className="clickable adminButton" onClick={() => this.loadPeople()}>
            <FontAwesomeIcon icon={faSyncAlt}/>
          </div>
        </div>
        <div className="stats">
          <div className="quickStats">
            <div className="signInCounter">
              <div className="counter">
                {this.state.signInCount}
              </div>
              <div className="counterBlurb">
                {this.state.signInCount === 1 ? "Person" : "People"} signed in
              </div>
            </div>
            <div className="totalCounter">
              <div className="counter">
                {this.state.peopleMap !== undefined ? this.state.peopleMap.size : 0}
              </div>
              <div className="counterBlurb">
                Total people
              </div>
            </div>
          </div>
          <div className="infoTime">
            Info from: {moment(this.state.startTime).format("hh:mm A")}
          </div>
        </div>
        {table}
        {popup}
      </div>
    )
  }
}

const IndeterminateCheckbox = React.forwardRef(
  ({ indeterminate, ...rest }, ref) => {
    const defaultRef = React.useRef()
    const resolvedRef = ref || defaultRef

    React.useEffect(() => {
      resolvedRef.current.indeterminate = indeterminate
    }, [resolvedRef, indeterminate])

    return (
      <>
        <input type="checkbox" ref={resolvedRef} {...rest} />
      </>
    )
  }
)

function Table({columns, data, bulkSignInHandler}) {
  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow,
    selectedFlatRows,
    state: { selectedRowIds },
  } = useTable({
    columns,
    data,
  },
  useRowSelect,
  hooks => {
    hooks.visibleColumns.push(columns => [
      // Let's make a column for selection
      {
        id: 'selection',
        // The header can use the table's getToggleAllRowsSelectedProps method
        // to render a checkbox
        Header: ({ getToggleAllRowsSelectedProps }) => (
          <div>
            <IndeterminateCheckbox {...getToggleAllRowsSelectedProps()} />
          </div>
        ),
        // The cell can use the individual row's getToggleRowSelectedProps method
        // to the render a checkbox
        Cell: ({ row }) => (
          <div>
            <IndeterminateCheckbox {...row.getToggleRowSelectedProps()} />
          </div>
        ),
      },
      ...columns,
    ])
  })

  let bulkSignIn = (event) => {
    let selectedHashes = undefined;
    if (selectedFlatRows !== undefined && selectedFlatRows.length > 0) {
      selectedHashes = selectedFlatRows.map(row => row.original.hash);
    }

    if (selectedHashes !== undefined) {
      bulkSignInHandler(selectedHashes);
    }
  }

  let bulkSignInButton;

  if (selectedRowIds !== undefined &&
      Object.keys(selectedRowIds).length > 0) {
    bulkSignInButton =
      <div className="clickable bulkSignInButton" onClick={bulkSignIn}>
        <span>Bulk sign in </span>
        <FontAwesomeIcon icon={faSignature}/>
      </div>
  }

  return (
    <div className="table">
      <table {...getTableProps()}>
        <thead>
          {headerGroups.map(headerGroup => (
            <tr {...headerGroup.getHeaderGroupProps()}>
              {headerGroup.headers.map(column => (
                <th className={column.className} {...column.getHeaderProps()}>{column.render('Header')}</th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody {...getTableBodyProps()}>
          {rows.map((row, i) => {
            prepareRow(row)
            let selectedClassName;
            if (row.isSelected) {
              selectedClassName = "selectedRow";
            }
            return (
              <tr className={selectedClassName} {...row.getRowProps()}>
                {row.cells.map(cell => {
                  return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                })}
              </tr>
            )
          })}
        </tbody>
      </table>
      {bulkSignInButton}
    </div>
  )
}

export default CheckList;