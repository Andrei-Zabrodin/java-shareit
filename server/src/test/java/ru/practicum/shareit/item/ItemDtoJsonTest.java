package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.config.JacksonConfig;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Value("${client.timeZone}")
    private String timeZone;

    private DateTimeFormatter formatter;
    private Instant instant1;
    private Instant instant2;
    private Instant instant3;
    private String timeStr1;
    private String timeStr2;
    private String timeStr3;
    private CommentDto comment;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of(timeZone));

        instant1 = Instant.parse("2026-06-20T18:56:51.882Z");
        instant2 = Instant.parse("2026-06-21T18:56:51.882Z");
        instant3 = Instant.parse("2026-06-22T18:56:51.882Z");
        timeStr1 = formatter.format(instant1);
        timeStr2 = formatter.format(instant2);
        timeStr3 = formatter.format(instant3);

        comment = new CommentDto();
        comment.setId(1L);
        comment.setText("comment");
        comment.setAuthorName("name");
        comment.setCreated(instant3);
    }

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("item");
        dto.setDescription("description");
        dto.setAvailable(true);
        dto.setLastBooking(instant1);
        dto.setNextBooking(instant2);
        dto.setComments(Arrays.asList(comment));
        dto.setRequestId(1L);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(timeStr1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(timeStr2);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(dto.getRequestId().intValue());
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(comment.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(comment.getText());
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String jsonString = "{\"id\":1,\"name\":\"item\",\"description\":\"description\",\"available\":true," +
                "\"lastBooking\":\"" + timeStr1 + "\",\"nextBooking\":\"" + timeStr2 + "\",\"requestId\":1}";

        ItemDto dto = json.parseObject(jsonString);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("item");
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(1L);
        assertThat(dto.getLastBooking()).isEqualTo(instant1.truncatedTo(ChronoUnit.SECONDS));
        assertThat(dto.getNextBooking()).isEqualTo(instant2.truncatedTo(ChronoUnit.SECONDS));
    }
}