# People service

This is the middleware between the database and the html front end.

In this package I have included some postman requests that can help
expose the various endpoints and stuff. I hope one day to use something
like Swagger to generate the API documentation.

I try and keep the project up to date when I can with the latest
Spring boot releases.

Take note that I also use an nginx front end (with an ssl cert) to 
host the service AND the html front end. My hope is to redirect the
calls from the html front end over to the service when necessary.

The SSL certificate is necessary due to a constraint that Chrome
introduced in 2017: https://stackoverflow.com/questions/47995355/chrome-is-not-letting-http-hosted-site-to-access-camera-microphone
As such since this html end point will be hosted on some machine,
that machine or webserver needs to have an SSL certificate that is
partially valid.

You also need to install the certificate as well, instructions are
as follows:
https://deliciousbrains.com/ssl-certificate-authority-for-local-https-development/

In essence, you need to:
1. Generate a root certificate for a CA. 
   
2. Install the root certificate on the machine.

3. Create a new certificate for the domain (using the CA cert).

4. Load up the site.

The database schema is maintained by Spring boot itself, so all
the service really needs is write and read access to the database
and Spring boot will configure everything else.