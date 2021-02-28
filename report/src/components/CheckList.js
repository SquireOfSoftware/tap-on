import React, {Component} from 'react';

import { useTable, useRowSelect } from 'react-table'

import './CheckList.css'
import ServerStates from './ServerStates.js'

class CheckList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      people: [],
      serverGETState: ServerStates.UNCHECKED,
      startTime: this.props.initialStartTime,
      serverUrl: this.props.initialServerUrl
    }
    this.loadPeople = this.loadPeople.bind(this);
    this.loadPeople();
  }

  loadTodaysSignins = () => {
    this.setState({
      serverGETState: ServerStates.CHECKING
    });

    let successGET = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverGETState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverGETState);
        console.log(event.target.responseText);
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
    }

    let getRequest = new XMLHttpRequest();

    getRequest.addEventListener("load", successGET);
    getRequest.addEventListener("error", failedGET);
    console.log(this.state.serverUrl + "/people-service/checkin/signins/from/" + this.state.startTime);
    getRequest.open("GET", this.state.serverUrl + "/people-service/checkin/signins/from/" + this.state.startTime, true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();

    console.log(getRequest);
  }

  loadPeople = () => {
    this.setState({
      serverGETState: ServerStates.CHECKING
    });

    let successGET = (event) => {
      if (event.target.status === 200) {
        this.setState({
          serverGETState: ServerStates.UP
        });
        this.props.updateServerState(this.state.serverGETState);
        let people = JSON.parse(event.target.responseText);
        this.setState({
          people: people
        });
        console.log(people);
        this.loadTodaysSignins();
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
    }

    let getRequest = new XMLHttpRequest();

    getRequest.addEventListener("load", successGET);
    getRequest.addEventListener("error", failedGET);
    console.log(this.state.serverUrl + "/people-service/people/");
    getRequest.open("GET", this.state.serverUrl + "/people-service/people/", true);
    getRequest.setRequestHeader("Access-Control-Allow-Headers", "*");
    getRequest.setRequestHeader("Access-Control-Allow-Origin", "*");
    getRequest.setRequestHeader("Access-Control-Allow-Methods", "*");
    getRequest.setRequestHeader("Content-Type", "application/json");
    getRequest.send();

    console.log(getRequest);
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
            Header: 'Is Member',
            accessor: 'isAMember'
          },
          {
            Header: 'Has Signed In',
            accessor: 'hasSignedIn'
          }
        ],
      },
    ]

    return (
      <div>
        <Table columns={columns} data={this.state.people} />
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
          console.log(row);
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