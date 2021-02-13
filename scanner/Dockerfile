# stage1 as builder
FROM node:alpine3.10 as builder

# copy the package.json to install dependencies
COPY package.json yarn.lock ./

# Install the dependencies and make the folder
RUN yarn && mkdir /react-ui && mv ./node_modules ./react-ui

WORKDIR /react-ui

COPY . .

# Build the project and copy the files
RUN yarn run build


FROM nginx:alpine

#!/bin/sh

COPY ./nginx.conf /etc/nginx/nginx.conf

## Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Copy from the stage 1
COPY --from=builder /react-ui/build /usr/share/nginx/html/

RUN chown nginx:nginx /usr/share/nginx/html/*

RUN chmod 755 -R /usr/share/nginx/html/

EXPOSE 8000 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]