package com.github.dgewiss.quarkusreactivehealthchecktest;

import java.util.Set;

import org.eclipse.microprofile.health.Readiness;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.datasource.runtime.DataSourceSupport;
import io.quarkus.reactive.datasource.runtime.ReactiveDatasourceHealthCheck;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;

/**
 * This is a adjusted copy of the original datasource health check {@code io.quarkus.reactive.pg.client.runtime.health.ReactivePgDataSourcesHealthCheck}.
 * 
 * It uses {@link io.vertx.mutiny.pgclient.PgPool} to select the Pool from the {@code ArcConainter}.
 */
@Readiness
@ApplicationScoped
public class MyReactivePgDataSourcesHealthCheck extends ReactiveDatasourceHealthCheck {

	  public MyReactivePgDataSourcesHealthCheck() {
	    super("My Reactive PostgreSQL connections health check", "SELECT 2");
	  }

	  @PostConstruct
	  protected void init() {
	    ArcContainer container = Arc.container();
	    DataSourceSupport support = container.instance(DataSourceSupport.class).get();
	    Set<String> excludedNames = support.getInactiveOrHealthCheckExcludedNames();

	    for (InstanceHandle<PgPool> handle :
	        container.select(PgPool.class, Any.Literal.INSTANCE).handles()) {
	      String poolName = getPoolName(handle.getBean());

	      if (!excludedNames.contains(poolName)) {
	        addPool(poolName, handle.get().getDelegate());
	      }
	    }
	  }
	}