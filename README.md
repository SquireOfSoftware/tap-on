# check-in
An internal online checkin system for people entering a building or room

There are some core requirements:
1. The names must never disappear for a year, after that, you can remove them, this applies only to when people do not show up for at least a year

The smaller requirements are:
1. Must work with mobile devices
2. Should not be accessible to the outside world
3. Potentially storing contact details
4. Names should be sorted in an alphabetical order

Some would like to haves are:
1. Custom domain name (as the users are not technical) - perhaps use of BIND - an open source DNS to assist with url redirection on the local network

TODO:
[x] create db schema
[ ] test out creation db schema - use inserts, check default values are being set correctly
[ ] test out update db schema mechanism - use updates, check default values change to time of change
[ ] test python django api - get some end point tests set up
[ ] write python django api to get data
[ ] write front end interaction to python api