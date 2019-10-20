var serviceComponents = [
    {
        id: 3,
        type: chairperson,
        name: Chairperson
    },
    {
        id: 2,
        type: welcoming,
        name: Welcoming
    },
    {
        id: 4,
        type: music,
        name: Music
    },
    {
        id: 1,
        type: speaker,
        name: Speaker
    },
    {
        id: 5,
        type: announcements,
        name: Announcements
    },
    {
        id: 7,
        type: prayer,
        name: Prayer
    },
    {
        id: 6,
        type: tithing,
        name: Tithing
    },
    {
        id: 8,
        type: prayer_meeting,
        name: Prayer Meeting
    },
    {
        id: 9,
        type: bible_reading,
        name: Bible Reading
    },
    {
        id: 10,
        type: tech_desk,
        name: Tech Desk,
        roles: {
            {
                id: 1,
                type: sound,
                name: Sound
            },
            {
                id: 2,
                type: computer,
                name: Computer
            }
        }
    },
    {
        id: 11,
        type: communion,
        name: Communion,
        roles: {
            {
                id: 1,
                type: front_isle,
                name: Front Isle
                description: Handling the front isle only
            },
            {
                id: 2,
                type: front_window,
                name: Front Window
                description: Handling the front window and stage
            },
            {
                id: 3,
                type: back_isle,
                name: Back Isle
                description: Handling the back isle and some of the people at the back
            },
            {
                id: 4,
                type: back_window,
                name: Back Window
                description: Handling the back window and the tech desk
            }
        }
    },
    {
        id: 12,
        type: cooking
        name: Cooking
    }
]

// dates are MM/DD/YYYY
var genericServiceOrder = [
    chairperson,
    music,
    bible_reading,
    speaker,
    music,
    announcements,
    prayer_meeting,
    tithing
]

var specialServiceDates = [
    {
        id: 1,
        date: "10/20/19",
        display: [
            "communion"
        ]
    }
]

var defaultDisplay = [
    speaker,
    chairperson,
    welcoming,
    bible_reading,
    music,
    tech_desk,
    tithing,
    prayer_meeting,
    communion,
    cooks
]

