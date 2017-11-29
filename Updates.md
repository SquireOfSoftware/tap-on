## 12/11/2017
- Added in the database files for mysql

## 27/11/2017
- Tinkering with postgres to see if something can be set up that way
- Have been having a lot of thoughts in regards to how it might go together, was thinking that you hit a URL which provides a JSON object with all the services and required parameters to be filled in, this could be like: GET /services, returns {results: [{name: "Services", description: "Provides an endpoint to get all the provisioned endpoints are, ideally this endpoint should not change at all", use: "", options: "", url: "/services", methods: ['GET']}]}
- There should be: read, update and create, delete should not be allowed at all and should only executed when the database admin carries out the requested action
- I am thinking that perhaps I am overthinking the issue and perhaps something simple ought to be set up first, like a node server with a basic query front end with MySQL backend
- Discovered that you need to run `pip3 install psycopg2` to install the postgres connector is the interface to postgres and `python3 manage.py migrate` to migrate stuff over to the postgres database.