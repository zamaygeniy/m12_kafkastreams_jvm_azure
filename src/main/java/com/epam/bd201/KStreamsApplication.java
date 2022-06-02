package com.epam.bd201;

import com.epam.bd201.utils.Utils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class KStreamsApplication {

    public static void main(String[] args) {

        final String APP_ID = "stay-calculate-app";
        final String BOOTSTRAP_SERVERS = "kafka-0.kafka.confluent.svc.cluster.local:9092,kafka-1.kafka.confluent.svc.cluster.local:9071,kafka-1.kafka.confluent.svc.cluster.local:9092";
        final String INPUT_TOPIC_NAME = "expedia";
        final String OUTPUT_TOPIC_NAME = "expedia_ext";
        final String SCHEMA_REGISTRY_URL = "http://schemaregistry.confluent.svc.cluster.local:8081";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put("schema.registry.url", SCHEMA_REGISTRY_URL);

        final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, String> input_records = builder.stream(INPUT_TOPIC_NAME, Consumed.with(Serdes.String(), Serdes.String()));

        input_records.map((key, value) -> new KeyValue<>(key, Utils.addStayField(value))).to(OUTPUT_TOPIC_NAME);

        final Topology topology = builder.build();
        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
