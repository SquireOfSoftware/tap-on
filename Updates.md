## 16/01/2021
- This project received new enthusiasm as there was a desire to make the whole sign in process from Covid-19
  easier for the old folks, so you give an older person a qr code with a hash value, you decode the hash, check
  this against the hash in the database and generate a "sign in" entry
  
Take note that as of last week, I have managed to get some raw html and javascript files working with the spring
boot application, though a proper database is needed for this.
I plan to also use either use:
1. nginx
2. spring boot static resources
To redirect calls to the static html files and reroute them appropriately.
   
The scanner will read a QR code, assume that it is an integer and attempt to parse it. If it is invalid it will
generate an error. But if it is valid it then attempts to check in the person in accordance with the hash.

It should be noted that the architecture of this system is:
- loose browser clients
- spring boot middleware that generates and talks to the database
- database

The browser clients talk to the middleware and the middleware talks to the database

The middleware ought to control the schema of the database (mainly due to convenience mechanisms).

And the database of choice is postgres (mainly due to a lack of expertise in anything else and I just wanted 
something up and running).

The hash is just a hashcode, but I am thinking that it needs to be extended to support anything really.

I did test for the following:
1. Chinese character QR code encoding - this is not possible or at least I would need to get knee deep in javascript
encoding which I don't really want to touch since there is no real benefit to doing this, using numbers is just fine
2. Using nginx with aliases work on the network - I tested this at home using my laptop as the web server and I hooked
up an nginx server with some basic configuration with logs and stuff, and I was able to visit `http://jarvis:8000` and
it would redirect correctly.
3. Returning chinese characters via Springboot - this was tested by returning arbitrary chinese strings in Javascript,
this proves that alternative chinese names can work (I will commit the change then revert the change so its in the 
change log)

The following things are the things I would like to implement:
1. QR code generation - currently you have to generate a QR code externally outside of this system, it would be good
for this system to regenerate qr codes upon demand when needed, I am not entirely sure if this particular endpoint
needs to be locked down OR alternatively, its a one shot hash/qrcode generation? Each new generation will alter the
hash value and you can't query for previous qr code hashes?
2. Supporting alternative names - this needs to be properly fleshed out with name id links to the appropriate tables,
the main users will have chinese names and so this whole architecture needs to support chinese characters both in 
Javascript but also in Java and PostgreSQL
3. Supporting contact information - this needs to be implemented
4. Clean up the database and trial a network spanning demo at church

## 11/06/2018
- Rewrote everything to use spring and java

## 12/11/2017
- Added in the database files for mysql

## 27/11/2017
- Tinkering with postgres to see if something can be set up that way
- Have been having a lot of thoughts in regards to how it might go together, was thinking that you hit a URL 
  which provides a JSON object with all the org.squire.checkin.services and required parameters to be filled in, 
  this could be like: GET /org.squire.checkin.services, returns 
  ```
    {
    results: [
        {
            name: "Services", 
            description: "Provides an endpoint to get all the provisioned endpoints are, ideally 
            this endpoint should not change at all", 
            use: "", 
            options: "", 
            url: "/org.squire.checkin.services", 
            methods: ['GET']
        }
      ]
    }
  ```
- There should be: read, update and create, delete should not be allowed at all and should only executed when the 
  database admin carries out the requested action
- I am thinking that perhaps I am overthinking the issue and perhaps something simple ought to be set up first, 
  like a node server with a basic query front end with MySQL backend
- Discovered that you need to run `pip3 install psycopg2` to install the postgres connector is the interface to postgres 
  and `python3 manage.py migrate` to migrate stuff over to the postgres database.