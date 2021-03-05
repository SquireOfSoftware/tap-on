import React, {Component} from 'react';

import { useTable, useRowSelect } from 'react-table'

import './CheckList.css'
import ServerStates from './ServerStates.js'

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSyncAlt } from '@fortawesome/free-solid-svg-icons'

class CheckList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      peopleMap: undefined,
      serverGETState: ServerStates.UNCHECKED,
      startTime: this.props.initialStartTime,
      serverUrl: this.props.initialServerUrl,
      autoRefreshPeople: this.props.initialAutoRefreshPeople
    }
    this.loadPeople = this.loadPeople.bind(this);
    this.loadTodaysSignins = this.loadTodaysSignins.bind(this);
    this.queryServer = this.queryServer.bind(this);
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

  queryServer = (url, successCallback, failureCallback) => {
    this.setState({
      serverGETState: ServerStates.CHECKING
    });

    let successGET = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverGETState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverGETState);
        successCallback(event);
      } else {
        this.setState({
          serverGETState: ServerStates.DOWN
        });
        this.props.updateServerState(this.state.serverGETState);
      }
    }

    let failedGET = (event) => {
      console.error("An error occurred while checking the server health.");
      this.setState({
        serverGETState: ServerStates.DOWN
      });
      this.props.updateServerState(this.state.serverGETState);
      failureCallback(event);
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
            console.log(person);
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
          console.log(this.state.peopleMap);
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
        <div className="refreshPeopleButton" onClick={() => this.loadPeople()}>
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
            selectedClassName = "test";
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