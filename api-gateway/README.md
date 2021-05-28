## api gateway

I will be trying to tinker with Apollo Federation in getting an API gateway
working for signins and displaying varying degrees granularity from it.

Once the gateway is up you should be able to visit:
http://localhost:4000/graphql

Once inside here is an example query you can send:
```
query {
  person (id: 1) {
    name
  }
  film (id: 1) {
    title,
    actors {
      name
    }
  }
}
```

Here are some stuff to help me get started:
- https://dev.to/mandiwise/getting-started-with-apollo-federation-and-gateway-4739
- https://www.baeldung.com/spring-graphql <-- spring boot service
- https://principledgraphql.com/integrity#3-track-the-schema-in-a-registry 
- https://www.pluralsight.com/guides/querying-data-with-graphql
- https://hasura.io/learn/graphql/intro-graphql/graphql-mutations/
- https://graphql.org/learn/queries/
- https://netflix.github.io/dgs/