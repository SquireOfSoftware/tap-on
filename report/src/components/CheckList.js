import React, {Component} from 'react';

import { useTable, useRowSelect } from 'react-table'

import './CheckList.css'
import ServerStates from './ServerStates.js'
import NewPersonPopup from './NewPersonPopup.js'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt, faSignature, faUserEdit, faUserPlus } from '@fortawesome/free-solid-svg-icons'
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
      showNewPersonPopup: false
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
    this.setState({
      serverState: ServerStates.CHECKING
    });

    let successPOST = (event) => {
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

    let failedPOST = (event) => {
      console.error("An error occurred while checking the server health.");
      this.setState({
        serverState: ServerStates.DOWN
      });
      this.props.updateServerState(this.state.serverState);
    }

    let postRequest = new XMLHttpRequest();

    postRequest.addEventListener("load", successPOST);
    postRequest.addEventListener("error", failedPOST);
    console.debug(url);
    postRequest.open("POST", url, true);
    postRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    postRequest.setRequestHeader("Content-Type", "application/json");
    if (postBody !== undefined || postBody !== null) {
      postRequest.send(JSON.stringify(postBody));
    } else {
      postRequest.send();
    }

    console.debug(postRequest);
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
          checkinLogs.forEach(log => {
            let person = log.person;
            // find the person in the state via the peopleMap
            let personEntry = newPeopleMap.get(person.id);
            if (personEntry !== undefined || personEntry !== null) {
              personEntry.hasSignedIn = "true";
              personEntry.firstSignIn = moment(log.timestamp).format("hh:mm:ss A");
              newPeopleMap[person.id] = personEntry;
            }
          });

          this.setState({
            ...this.state,
            peopleMap: newPeopleMap
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
              let signInLink = row.original.links.find(link => link.rel === 'sign_in_request');

              let clickPostCallback = (event) => {
                if (signInLink !== undefined ||
                    signInLink !== null ||
                    signInLink.length > 0) {
                  this.postToServer(
                    signInLink.href + "?message=manual sign in",
                    (event) => {
                      this.loadTodaysSignins();
                    });
                }
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
                <div className="clickable" onClick={() => console.error("This has not been implemented yet")}>
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

    let newPersonPopup = undefined;
    if (this.state.showNewPersonPopup) {
      newPersonPopup = <NewPersonPopup createPersonCallback={this.createPersonCallback}
                                    closeNewPersonPopupCallback={this.closeNewPersonPopupCallback}/>
    }

    return (
      <div>
        <div className="adminBar">
          <div className="infoTime">
            Info from: {moment(this.state.startTime).format("hh:mm A")}
          </div>
          <div className="clickable adminButton" onClick={() => this.showNewPersonPopup()}>
            <FontAwesomeIcon icon={faUserPlus}/>
          </div>
          <div className="clickable adminButton" onClick={() => this.loadPeople()}>
            <FontAwesomeIcon icon={faSyncAlt}/>
          </div>
        </div>
        {table}
        {newPersonPopup}
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

  return (
    <div>
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
      <div className="clickable bulkSignInButton" onClick={bulkSignIn}>
        <span>Bulk sign in </span>
        <FontAwesomeIcon icon={faSignature}/>
      </div>
    </div>
  )
}

export default CheckList;