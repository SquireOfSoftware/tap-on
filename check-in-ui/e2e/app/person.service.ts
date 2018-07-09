import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {PeriodicElement} from '../../src/app/app.component';
import {Observable} from 'rxjs/Observable';
import {catchError} from 'rxjs/operators';
import {of} from 'rxjs/internal/observable/of';

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  url = '//localhost:8080/persons';
  constructor(private http: HttpClient) { }

  getPeopleSource(): Observable<PeriodicElement[]> {
    return this.http.get<PeriodicElement[]>(this.url)
      .pipe(catchError(this.handleError('getPeople', [])));
    // return mockPeople;
  }

  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}

//
// const mockPeople = [
//   {
//     personId: 1,
//     givenName: 'Dave',
//     familyName: 'Johnson',
//     'memberSince': null,
//     'baptisedSince': null,
//     'lastSignIn': '2018-07-07T12:35:36.000+0000',
//     'alternativeNames': [
//       {
//         'alternativeName': 'Davey',
//         'language': 'ENGLISH'
//       }
//     ]
//   },
//   {
//     'personId': 2,
//     'givenName': 'Johnson',
//     'familyName': 'Smith',
//     'memberSince': null,
//     'baptisedSince': null,
//     'lastSignIn': null,
//     'alternativeNames': null
//   },
//   {
//     'personId': 14,
//     'givenName': 'Dave',
//     'familyName': 'Johnson',
//     'memberSince': null,
//     'baptisedSince': null,
//     'lastSignIn': '2018-07-07T13:00:09.000+0000',
//     'alternativeNames': [
//       {
//         'alternativeName': 'Mike',
//         'language': 'ENGLISH'
//       }
//     ]
//   },
//   {
//     'personId': 27,
//     'givenName': 'Dave',
//     'familyName': 'Johnson',
//     'memberSince': null,
//     'baptisedSince': null,
//     'lastSignIn': null,
//     'alternativeNames': [
//       {
//         'alternativeName': 'Mike',
//         'language': 'ENGLISH'
//       },
//       {
//         'alternativeName': 'John',
//         'language': 'CHINESE'
//       }
//     ]
//   }
// ];
