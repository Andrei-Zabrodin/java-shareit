package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.config.JacksonConfig;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Value("${client.timeZone}")
    private String timeZone;

    private DateTimeFormatter formatter;
    private Instant instant1;
    private Instant instant2;
    private String timeStr1;
    private String timeStr2;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of(timeZone));

        instant1 = Instant.parse("2026-06-21T18:56:51.882Z");
        instant2 = Instant.parse("2026-06-21T19:56:51.882Z");
        timeStr1 = formatter.format(instant1);
        timeStr2 = formatter.format(instant2);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user");
        userDto.setEmail("test@mail.ru");
    }

    @Test
    void testSerializeBookingResponseDto() throws Exception {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(instant1);
        dto.setEnd(instant2);
        dto.setItem(itemDto);
        dto.setBooker(userDto);
        dto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(timeStr1);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(timeStr2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(dto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(dto.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(dto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(dto.getBooker().getName());
    }
}