package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.config.JacksonConfig;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Value("${client.timeZone}")
    private String timeZone;

    private DateTimeFormatter formatter;
    private Instant instant1;
    private Instant instant2;
    private String timeStr1;
    private String timeStr2;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of(timeZone));

        instant1 = Instant.parse("2026-06-21T18:56:51.882Z");
        instant2 = Instant.parse("2026-06-21T19:56:51.882Z");
        timeStr1 = formatter.format(instant1);
        timeStr2 = formatter.format(instant2);
    }

    @Test
    void testSerializeBookingRequestDto() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(instant1);
        dto.setEnd(instant2);
        dto.setItemId(1L);

        JsonContent<BookingRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(timeStr1);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(timeStr2);
    }

    @Test
    void testDeserializeBookingRequestDto() throws Exception {
        String jsonString = "{\"start\":\"" + timeStr1 + "\",\"end\":\"" + timeStr2 + "\",\"itemId\":1}";

        BookingRequestDto dto = json.parseObject(jsonString);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(instant1.truncatedTo(ChronoUnit.SECONDS));
        assertThat(dto.getEnd()).isEqualTo(instant2.truncatedTo(ChronoUnit.SECONDS));
    }
}
