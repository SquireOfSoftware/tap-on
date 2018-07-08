import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSort, MatTableDataSource} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})

export class AppComponent implements OnInit {
  dataSource = new MatTableDataSource(ELEMENT_DATA);
  columnsToDisplay = ['select', 'givenName', 'familyName', 'lastSignIn', 'signInBtn'];
  expandedElement: PeriodicElement;
  selection = new SelectionModel<PeriodicElement>(true, []);

  @ViewChild(MatSort) sort: MatSort;

  ngOnInit() {
    this.dataSource.sort = this.sort;
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim(); // Remove whitespace
    filterValue = filterValue.toLowerCase(); // Datasource defaults to lowercase matches
    this.dataSource.filter = filterValue;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  signSelectedIn() {
    console.log(this.selection.selected);
  }

  signIn(id: number): any {
    console.log(id);
  }
}

export interface PeriodicElement {
  personId: number;
  givenName: string;
  familyName: string;
  memberSince: string; // needs to be parsed
  baptisedSince: string; // needs to be parsed
  lastSignIn: string; // needs to be parsed
  alternativeNames: AlternativeName[];
}

export interface AlternativeName {
  alternativeName: string;
  language: string; // needs to be parsed
}

export enum Language {
  ENGLISH,
  CHINESE,
  MALAY
}

const ELEMENT_DATA: PeriodicElement[] = [
  {
    personId: 1,
    givenName: 'Dave',
    familyName: 'Johnson',
    'memberSince': null,
    'baptisedSince': null,
    'lastSignIn': '2018-07-07T12:35:36.000+0000',
    'alternativeNames': [
      {
        'alternativeName': 'Davey',
        'language': 'ENGLISH'
      }
    ]
  },
  {
    'personId': 2,
    'givenName': 'Johnson',
    'familyName': 'Smith',
    'memberSince': null,
    'baptisedSince': null,
    'lastSignIn': null,
    'alternativeNames': null
  },
  {
    'personId': 14,
    'givenName': 'Dave',
    'familyName': 'Johnson',
    'memberSince': null,
    'baptisedSince': null,
    'lastSignIn': '2018-07-07T13:00:09.000+0000',
    'alternativeNames': [
      {
        'alternativeName': 'Mike',
        'language': 'ENGLISH'
      }
    ]
  },
  {
    'personId': 27,
    'givenName': 'Dave',
    'familyName': 'Johnson',
    'memberSince': null,
    'baptisedSince': null,
    'lastSignIn': null,
    'alternativeNames': [
      {
        'alternativeName': 'Mike',
        'language': 'ENGLISH'
      },
      {
        'alternativeName': 'John',
        'language': 'CHINESE'
      }
    ]
  }
];
