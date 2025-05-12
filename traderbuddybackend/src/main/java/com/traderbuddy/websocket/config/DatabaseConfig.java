package com.traderbuddy.websocket.config;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initDatabase() {
        createTriggerFunction();
        createTrigger();
    }

    private void createTriggerFunction() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
        	 String createFunction = """
        	            CREATE OR REPLACE FUNCTION notify_table_change() RETURNS TRIGGER AS $$
        	            BEGIN
        	                PERFORM pg_notify('table_changes', TG_TABLE_NAME || '|' || TG_OP);
        	                RETURN NULL;
        	            END;
        	            $$ LANGUAGE plpgsql;
        	        """;
            stmt.execute(createFunction);
            log.info("Created simple notify_user_change() function");
        } catch (SQLException e) {
            log.error("Error creating trigger function", e);
        }
    }

    private void createTrigger() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TRIGGER IF EXISTS tables_notify_trigger ON workspace;");

            String createTrigger = """
                CREATE TRIGGER tables_notify_trigger
                AFTER INSERT OR UPDATE OR DELETE ON workspace
                FOR EACH STATEMENT EXECUTE FUNCTION notify_table_change();
                """;

            stmt.execute(createTrigger);
            log.info("Created users_notify_trigger (statement-level)");
        } catch (SQLException e) {
            log.error("Error creating trigger", e);
        }
    }
}
