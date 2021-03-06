#!/bin/sh
# copied from: https://deliciousbrains.com/ssl-certificate-authority-for-local-https-development/
# requires your local CA private key

if [ "$#" -ne 3 ]
then
  echo "Usage: Must supply a domain, the CA pem file and the CA key file"
  exit 1
fi

DOMAIN=$1
CA_PEM=$2
CA_KEY=$3

cd ~/certs

openssl genrsa -out $DOMAIN.key 2048
openssl req -new -key $DOMAIN.key -out $DOMAIN.csr

cat > $DOMAIN.ext << EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = $DOMAIN
EOF

openssl x509 -req -in $DOMAIN.csr -CA $CA_PEM -CAkey $CA_KEY -CAcreateserial \
-out $DOMAIN.crt -days 825 -sha256 -extfile $DOMAIN.ext