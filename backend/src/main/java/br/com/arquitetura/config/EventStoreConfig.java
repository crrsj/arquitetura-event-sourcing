package br.com.arquitetura.config;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import com.eventstore.dbclient.UserCredentials;
import jakarta.websocket.Endpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class EventStoreConfig {

    @Bean
    public EventStoreDBClient eventStoreDBClient() {

        String connectionString =
                "esdb://localhost:2113?tls=false";

        return EventStoreDBClient.create(
                EventStoreDBConnectionString.parseOrThrow(
                        connectionString
                )
        );
    }

}
