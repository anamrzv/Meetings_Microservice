package ifmo.integration.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import ifmo.dto.EventEntityDto;
import ifmo.model.CharacteristicEntity;
import ifmo.model.EventEntity;
import ifmo.repository.CharacteristicRepository;
import ifmo.repository.EventRepository;
import ifmo.service.EventService;
import ifmo.utils.JsonDeserializer;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private CharacteristicRepository characteristicRepository;

    @DisplayName("Get all event")
    @Test
    @WithMockUser(roles = {"ADULT"})
    public void getAllEventTest() {
        List<EventEntity> eventEntityList = new ArrayList<>();
        eventEntityList.add(new EventEntity("concert", 15, "meow.jpg", "music", 3, LocalDateTime.now()));
        eventEntityList.add(new EventEntity("the best day", 15, "meow.jpg", "wow", 4, LocalDateTime.now()));
        Page<EventEntity> page = new PageImpl<>(eventEntityList);
        Pageable pageable = PageRequest.of(0, 50);

        Mockito.when(eventRepository.findAll(pageable)).thenReturn(page);
        Page<EventEntityDto> eventEntityDtoPage = eventService.findAll(pageable);

        String expected = JsonDeserializer.objectToJson(page.map(EventEntityDto::new).getContent());
        String actual = JsonDeserializer.objectToJson(eventEntityDtoPage.getContent());

        Assertions.assertNotNull(eventEntityDtoPage);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Find by event id")
    @Test
    public void findEventById() {
        EventEntity eventEntity = new EventEntity("the best day", 14, "image.jpg", "description", 4, LocalDateTime.of(2023, 1, 1, 1, 1));
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));

        EventEntityDto eventEntityDto = eventService.findEventById(1L);

        String expected = JsonDeserializer.objectToJson(new EventEntityDto(eventEntity));
        String actual = JsonDeserializer.objectToJson(eventEntityDto);

        Assertions.assertNotNull(eventEntityDto);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Set event characteristics")
    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void setCharacteristics() {
        EventEntity eventEntity = new EventEntity("event", 15, "image.png", "wow", 23, LocalDateTime.now());
        Set<EventEntity> eventEntities = new HashSet<>();
        eventEntities.add(eventEntity);
        CharacteristicEntity characteristicEntity = new CharacteristicEntity(1L, "calm", eventEntities);
        Set<String> chars = new HashSet<>();
        chars.add("calm");

        Mockito.when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));
        Mockito.when(characteristicRepository.getCharacteristicEntityByName(Mockito.matches("calm"))).thenReturn(Optional.of(characteristicEntity));
        Mockito.when(eventRepository.save(eventEntity)).thenReturn(eventEntity);

        eventService.setCharacteristicsToEvent(1L, chars);

        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntity);
    }

    @DisplayName("findAllChild")
    @Test
    public void findAllChildTest() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setId(1L);
        eventEntity.setName("event");

        List<EventEntity> eventEntityList = new ArrayList<>();
        eventEntityList.add(eventEntity);

        Mockito.when(eventRepository.getEventEntitiesByAgeLimitBefore(18)).thenReturn(eventEntityList);
        Assertions.assertNotNull(eventService.findAllChild());
    }

    @DisplayName("Add event")
    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void addEventTest() {
        EventEntity eventEntity = new EventEntity("the best day", 14, "image.jpg", "description", 4, LocalDateTime.of(2023, 1, 1, 1, 1));

        Mockito.when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        EventEntityDto eventEntityDto1 = new EventEntityDto(eventEntity);
        EventEntityDto eventEntityDto = eventService.addEvent(eventEntityDto1);

        Assertions.assertEquals(new EventEntityDto(eventEntity), eventEntityDto);
    }
}
