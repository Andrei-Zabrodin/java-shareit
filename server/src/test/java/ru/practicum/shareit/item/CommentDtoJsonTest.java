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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Value("${client.timeZone}")
    private String timeZone;

    private DateTimeFormatter formatter;
    private Instant instant;
    private String timeStr;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of(timeZone));

        instant = Instant.parse("2026-06-21T18:56:51.882Z");
        timeStr = formatter.format(instant);
    }

    @Test
    void testSerializeCommentDto() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("comment");
        dto.setAuthorName("name");
        dto.setCreated(instant);

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(timeStr);
    }

    @Test
    void testDeserializeCommentDto() throws Exception {
        String jsonString = "{\"id\":1,\"text\":\"comment\",\"authorName\":\"name\",\"created\":\"" + timeStr + "\"}";

        CommentDto newDto = json.parseObject(jsonString);

        assertThat(newDto.getId()).isEqualTo(1L);
        assertThat(newDto.getText()).isEqualTo("comment");
        assertThat(newDto.getAuthorName()).isEqualTo("name");
        assertThat(newDto.getCreated()).isEqualTo(instant.truncatedTo(ChronoUnit.SECONDS));
    }
}