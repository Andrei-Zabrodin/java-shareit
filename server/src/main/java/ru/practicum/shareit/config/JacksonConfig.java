package ru.practicum.shareit.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Value("${client.timeZone}")
    private String timeZone;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            JavaTimeModule module = new JavaTimeModule();

            module.addDeserializer(Instant.class, new JsonDeserializer<Instant>() {
                @Override
                public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String dateStr = p.getText();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
                    return ldt.atZone(ZoneId.of(timeZone)).toInstant();
                }
            });

            module.addSerializer(Instant.class, new JsonSerializer<Instant>() {
                @Override
                public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                            .withZone(ZoneId.of(timeZone));
                    gen.writeString(formatter.format(value));
                }
            });

            builder.modules(module);
        };
    }
}
