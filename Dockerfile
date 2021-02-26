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

FROM nginx:alpine

COPY ./nginx.conf /etc/nginx/nginx.conf

## Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Copy from the stage 1
COPY --from=qr-builder /qr-scanner/build /usr/share/nginx/html/qr-scanner

RUN chown nginx:nginx /usr/share/nginx/html/*

RUN chmod 755 -R /usr/share/nginx/html/

EXPOSE 8000 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]