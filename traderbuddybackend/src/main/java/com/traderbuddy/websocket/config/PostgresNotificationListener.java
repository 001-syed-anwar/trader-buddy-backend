package com.traderbuddy.websocket.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.traderbuddy.repositories.WorkspaceRepository;

//import com.demo.Message;
//import com.demo.PostgresNotificationListener;
//import com.demo.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class PostgresNotificationListener {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private WorkspaceRepository workspaceRepository;
	private Connection connection;
	private final AtomicBoolean isListening = new AtomicBoolean(false);
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void init() {
		startListening();
	}

	@PreDestroy
	public void cleanup() {
		stopListening();
		executor.shutdown();
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("Error closing database connection", e);
		}
	}

	private void startListening() {
		executor.schedule(this::listen, 0, TimeUnit.SECONDS);
	}

	private void stopListening() {
		isListening.set(false);
	}

	private void listen() {
		isListening.set(true);

		try {
			// Establish a dedicated connection for listening
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			// Set up the LISTEN command
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("LISTEN table_changes");
				log.info("Listening for PostgreSQL notifications on channel 'table_changes'");
			}

			// Cast to PGConnection to use PostgreSQL-specific features
			PGConnection pgConnection = connection.unwrap(PGConnection.class);

			// Keep checking for notifications until stopped
			while (isListening.get() && !connection.isClosed()) {
				// Get any pending notifications
				PGNotification[] notifications = pgConnection.getNotifications(500);

				if (notifications != null) {
					for (PGNotification notification : notifications) {
						handleNotification(notification);
					}
				}

				// Avoid hogging CPU
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		} catch (SQLException e) {
			log.error("Error in PostgreSQL notification listener", e);
			// Attempt reconnection after delay if still supposed to be listening
			if (isListening.get()) {
				executor.schedule(this::listen, 5, TimeUnit.SECONDS);
			}
		}
	}

	private void handleNotification(PGNotification notification) {
		String payload = notification.getParameter();
		String tableName = payload.substring(0, payload.indexOf('|'));
		log.info("got the notification : {}", payload);
		switch (tableName) {
		case "workspace": {
			messagingTemplate.convertAndSend("/topic/tableUpdates/workspace", workspaceRepository.findAll());
			break;
		}
		}

	}

	// Keep the connection alive
	@Scheduled(fixedRate = 30000)
	public void keepConnectionAlive() {
		try {
			if (connection != null && !connection.isClosed()) {
				try (Statement stmt = connection.createStatement()) {
					stmt.execute("SELECT 1");
					connection.commit();
				}
			}
		} catch (SQLException e) {
			log.error("Error in keepConnectionAlive", e);
		}
	}
}
