package com.github.dgewiss.quarkusreactivehealthchecktest;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/connections")
public class ConnectionsResource {

	private static final String QUERY_OPEN_CLIENT_CONNECTIONS = "select count(*) as openconnections from pg_stat_activity where usename = 'appuser' and backend_type = 'client backend'";

	private static final String QUERY_CONNECTIONS_DETAILS = "select usename, application_name, text(client_addr) as addr, state, query from pg_stat_activity where usename = 'appuser' and backend_type = 'client backend'";

	@Inject
	private PgPool pool;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/count")
	public Uni<String> count() {
		return pool.query(QUERY_OPEN_CLIENT_CONNECTIONS).execute().map(this::extractOpenConnections);
	}

	private String extractOpenConnections(RowSet<Row> result) {
		var c = result.iterator().next().getInteger("openconnections");
		return "open connections " + c;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/details")
	public Multi<ConnectionDetails> details() {
		return pool.query(QUERY_CONNECTIONS_DETAILS).execute().onItem().transformToMulti(RowSet::toMulti)
				.map(this::readDetail);
	}

	private ConnectionDetails readDetail(Row row) {
		return new ConnectionDetails(row.getString("usename"), row.getString("application_name"),
				row.getString("addr"), row.getString("state"), row.getString("query"));
	}

	public record ConnectionDetails(String usename, String applicationName, String cientAddr, String state, String query) {}
}
