# inkey should be server.key
# in should be server.crt
openssl pkcs12 -export -inkey $1 -in $2 -out src/main/resources/keystore/dummy-keystore.p12