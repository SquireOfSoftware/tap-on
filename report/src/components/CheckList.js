import React, {Component} from 'react';

import { useTable, useRowSelect } from 'react-table'

import './CheckList.css'
import ServerStates from './ServerStates.js'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt, faSignature } from '@fortawesome/free-solid-svg-icons'

class CheckList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      peopleMap: undefined,
      serverState: ServerStates.UNCHECKED,
      startTime: this.props.initialStartTime,
      serverUrl: this.props.initialServerUrl,
      autoRefreshPeople: this.props.initialAutoRefreshPeople
    }
    this.loadPeople = this.loadPeople.bind(this);
    this.loadTodaysSignins = this.loadTodaysSignins.bind(this);
    this.queryServer = this.queryServer.bind(this);
    this.postToServer = this.postToServer.bind(this);
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
    console.log(url);
    getRequest.open("GET", url, true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();

    console.log(getRequest);
  }

  postToServer = (url, successCallback, userErrorCallback) => {
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
    console.log(url);
    postRequest.open("POST", url, true);
    postRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    postRequest.setRequestHeader("Content-Type", "application/json");
//    postRequest.send(JSON.stringify(postBody));
    postRequest.send();

    console.log(postRequest);
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
              personEntry.firstSignIn = log.timestamp;
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
        let peopleMap = new Map(people.map(person => [person.id, person]));

        this.setState({
          peopleMap: peopleMap
        });
        console.log(people);
        this.loadTodaysSignins();
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
        ],
      },
      {
        Header: 'Info',
        columns: [
          {
            Header: 'Signed In At',
            accessor: 'firstSignIn'
          },
          {
            Header: 'Manual sign in',
            accessor: 'manualSignIn',
            Cell: ({row}) => {
              let signInLink = row.original.links.find(link => link.rel === 'sign_in_request');

              let clickPostCallback = (event) => {
                console.log("CLICK POSTCALLBACK!!");
                if (signInLink !== undefined ||
                    signInLink !== null ||
                    signInLink.length > 0) {
                  this.postToServer(
                    signInLink.href + "?message=manual sign in",
                    (event) => {
                      console.log(event);
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
          }
        ],
      },
    ]

    let table = undefined;
    if (this.state.peopleMap !== undefined) {
      table = <Table columns={columns} data={[...this.state.peopleMap.values()]} />
    }

    return (
      <div>
        <div className="refreshPeopleButton clickable" onClick={() => this.loadPeople()}>
          <FontAwesomeIcon icon={faSyncAlt}/>
        </div>
        {table}
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

function Table({columns, data}) {
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
          let selectedClassName;
          if (row.isSelected) {
            selectedClassName = "selectedRow";
          }
//          console.log(row);
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
  )
}

export default CheckList;