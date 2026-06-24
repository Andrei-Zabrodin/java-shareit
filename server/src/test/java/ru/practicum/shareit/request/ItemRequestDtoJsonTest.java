package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.config.JacksonConfig;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Value("${client.timeZone}")
    private String timeZone;

    private DateTimeFormatter formatter;
    private Instant instant;
    private String timeStr;
    private ItemShortDto itemShort;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of(timeZone));

        instant = Instant.parse("2026-06-21T18:56:51.882Z");
        timeStr = formatter.format(instant);

        itemShort = new ItemShortDto();
        itemShort.setId(1L);
        itemShort.setName("item");
        itemShort.setOwnerId(1L);
    }

    @Test
    void testSerializeItemRequestDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("description");
        dto.setCreated(instant);
        dto.setItems(Arrays.asList(itemShort));

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(timeStr);
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(1);
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        String jsonString = "{\"id\":1,\"description\":\"description\",\"created\":\"" + timeStr + "\"}";

        ItemRequestDto dto = json.parseObject(jsonString);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getCreated()).isEqualTo(instant.truncatedTo(ChronoUnit.SECONDS));
    }
}