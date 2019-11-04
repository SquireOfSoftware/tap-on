const serviceComponents = {
    chairperson: {
        id: 3,
        type: "chairperson",
        name: "Chairperson"
    },
    welcoming: {
        id: 2,
        type: "welcoming",
        name: "Welcoming"
    },
    music: {
        id: 4,
        type: "music",
        name: "Music"
    },
    speaker: {
        id: 1,
        type: "speaker",
        name: "Speaker"
    },
    announcements: {
        id: 5,
        type: "announcements",
        name: "Announcements"
    },
    prayer: {
        id: 7,
        type: "prayer",
        name: "Prayer"
    },
    tithing: {
        id: 6,
        type: "tithing",
        name: "Tithing"
    },
    prayer_meeting: {
        id: 8,
        type: "prayer_meeting",
        name: "Prayer Meeting"
    },
    bible_reading: {
        id: 9,
        type: "bible_reading",
        name: "Bible Reading"
    },
    tech_desk: {
        id: 10,
        type: "tech_desk",
        name: "Tech Desk",
        roles: [
            {
                id: 1,
                type: "sound",
                name: "Sound"
            },
            {
                id: 2,
                type: "computer",
                name: "Computer"
            }
        ]
    },
    communion: {
        id: 11,
        type: "communion",
        name: "Communion",
        roles: [
            {
                id: 1,
                type: "front_isle",
                name: "Front Isle",
                description: "Handling the front isle only"
            },
            {
                id: 2,
                type: "front_window",
                name: "Front Window",
                description: "Handling the front window and stage"
            },
            {
                id: 3,
                type: "back_isle",
                name: "Back Isle",
                description: "Handling the back isle and some of the people at the back"
            },
            {
                id: 4,
                type: "back_window",
                name: "Back Window",
                description: "Handling the back window and the tech desk"
            }
        ]
    },
    cooking: {
        id: 12,
        type: "cooking",
        name: "Cooking"
    }
}

// dates are MM/DD/YYYY
var genericServiceOrder = [
    "chairperson",
    "music",
    "bible_reading",
    "speaker",
    "music",
    "announcements",
    "prayer_meeting",
    "tithing"
]

var specialServiceDates = [
    {
        id: 1,
        date: "10/20/19",
        display: [
            "communion"
        ]
    },
    {
        id: 2,
        date: "10/27/19",
        display: [
            "default"
        ]
    }
]

var defaultDisplay = [
    "speaker",
    "chairperson",
    "welcoming",
    "bible_reading",
    "music",
    "tech_desk",
    "tithing",
    "prayer_meeting",
    "communion",
    "cooks"
]

var people = {
    1: {
        givenName: "Joseph",
        familyName: "Tran",
        isMember: true,
        isBaptise: true
    },
    2: {
        givenName: "Daniel",
        familyName: "Tran",
        isMember: true,
        isBaptise: true
    },
    3: {
        givenName: "Steven",
        familyName: "Lam",
        isMember: true,
        isBaptise: true
    },
    4: {
        givenName: "Annabel",
        familyName: "Lam",
        isMember: true,
        isBaptise: true
    }
}

var dummyWeeks = [
    {
        id: 1,
        date: "10/20/19",
        service: [
            {
                name: "chairperson",
                people: [1]
            },
            {
                name: "bible_reading",
                people: [3]
            },
            {
                name: "welcoming",
                people: [2, 3]
            },
            {
                name: "music",
                people: [3, 4]
            },
            {
                name: "tech_desk",
                people: [3]
            },
            {
                name: "prayer_meeting",
                people: [2]
            },
            {
                name: "tithing",
                people: [2, 3]
            },
            {
                name: "communion"
            }
        ]
    },
    {
        id: 1,
        date: "10/27/19",
        service: [
            {
                name: "chairperson",
                people: [1]
            },
            {
                name: "welcoming",
                people: [2, 3]
            }
        ]
    }
];

// a service can have many components
// a component can have many people

var constraints = {

}