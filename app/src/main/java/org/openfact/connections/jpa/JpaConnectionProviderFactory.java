package org.openfact.connections.jpa;

import java.sql.Connection;

public interface JpaConnectionProviderFactory {

    // Caller is responsible for closing connection
    Connection getConnection();

    String getSchema();

}