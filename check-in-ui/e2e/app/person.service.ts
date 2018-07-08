import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  constructor() { }

  getPeople() {
    return mockPeople;
  }
}

const mockPeople = [
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
