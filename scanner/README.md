# People project QR scanner

This sub project contains the react code for interacting with the
people service on port 8000.

There is an nginx.conf that has been included in the people service.

## Deploying the gh-pages

For the github test, you can utilise this URL:
https://squireofsoftware.github.io/check-in/index.html

And it will use the built up gh-pages react built site.

To run this code simply do a `yarn run deploy`.

If the cache is messing up you need to check the following:
1. Clear your cache in the node_modules like so: 
   `rm -rf node_modules/.cache/gh-pages`
2. Enable public github API tokens here: https://github.com/settings/tokens
   and send in your public token instead of your password and it
   should just work
   
## Forming the docker image

I will be following some examples from here: https://medium.com/bb-tutorials-and-thoughts/how-to-serve-react-application-with-nginx-and-docker-9c51ac2c50ba
