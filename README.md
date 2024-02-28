# quarkus-reactive-health-check-test

If you run this application with datasource healthcheck switched on, you can see that the number of connections used increases to twice the maximum number configured.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Endpoints

### Number of open connections

`select count(*) as openconnections from pg_stat_activity where usename = 'appuser' and backend_type = 'client backend'`

http://localhost:8080/connections/count

### Some details about the the connections

`select usename, application_name, text(client_addr) as addr, state, query from pg_stat_activity where usename = 'appuser' and backend_type = 'client backend'`

http://localhost:8080/connections/details

### Health Enpdoint

http://localhost:8080/q/health
