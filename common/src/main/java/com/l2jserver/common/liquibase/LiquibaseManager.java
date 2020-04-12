package com.l2jserver.common.liquibase;

import com.l2jserver.common.pool.impl.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiquibaseManager {

    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseManager.class);

    public static void executeDatabaseUpdates() {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new liquibase.Liquibase("liquibase/changelog.yaml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
            LOG.info("Successfully applied database updates");
        } catch (SQLException | LiquibaseException e) {
            LOG.error("Failed to execute database updates", e);
            throw new IllegalStateException(e);
        }
    }

}
