# stage1 as builder
FROM node:alpine3.10 as qr-builder

# copy the package.json to install dependencies
COPY scanner/package.json scanner/yarn.lock ./

# Install the dependencies and make the folder
RUN yarn && mkdir /qr-scanner

WORKDIR /qr-scanner

COPY scanner/ .

# Build the project and copy the files
RUN yarn run build

# stage2 as builder
FROM node:alpine3.10 as report-builder

# copy the package.json to install dependencies
COPY report/package.json report/yarn.lock ./

# Install the dependencies and make the folder
RUN yarn && mkdir /checkin-report

WORKDIR /checkin-report

COPY report/ .

# Build the project and copy the files
RUN yarn run build

FROM nginx:alpine

#!/bin/sh

COPY ./nginx.conf /etc/nginx/nginx.conf

## Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Copy from stage 1
COPY --from=qr-builder /qr-scanner/build /usr/share/nginx/html/qr-scanner

RUN ls -l /usr/share/nginx/html

# Copy from stage 2
COPY --from=report-builder /checkin-report/build /usr/share/nginx/html/checkin-report

COPY index.html /usr/share/nginx/html/

RUN ls -l /usr/share/nginx/html

RUN chown nginx:nginx /usr/share/nginx/html/*

RUN chmod 755 -R /usr/share/nginx/html/

EXPOSE 8000 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]