package ifmo.service;

import ifmo.dto.CharacteristicsDto;
import ifmo.dto.EventEntityDto;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.model.EventEntity;
import ifmo.repository.CharacteristicRepository;
import ifmo.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableRabbit
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CharacteristicRepository characteristicRepository;

    private static final String findAllQueue = "find-all-queue";
    private static final String findAllChildQueue = "find-child-queue";
    private static final String filterQueue = "filter-queue";
    private static final String idQueue = "id-queue";
    private static final String addQueue = "add-queue";
    private static final String updaterQueue = "update-queue";
    private static final String setQueue = "set-queue";
    private static final String removeQueue = "remove-queue";

    @RabbitListener(queues = findAllChildQueue, returnExceptions = "true")
    public List<EventEntityDto> findAllChild() {
        return eventRepository.getEventEntitiesByAgeLimitBefore(18).stream().map(EventEntityDto::new).collect(Collectors.toList());
    }

    @RabbitListener(queues = findAllQueue, returnExceptions = "true")
    public Page<EventEntityDto> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventEntityDto::new);
    }

    @RabbitListener(queues = idQueue, returnExceptions = "true")
    public EventEntityDto findEventById(Long id) {
        var eventEntity = eventRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        return new EventEntityDto(eventEntity);
    }

    @RabbitListener(queues = filterQueue, returnExceptions = "true")
    public List<EventEntityDto> getEventsByCharacteristics(CharacteristicsDto characteristicsDto) {
        var filter = characteristicsDto.getChars().stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .collect(Collectors.toSet());
        return eventRepository.getEventEntitiesByCharacteristicsIn(filter)
                .stream().map(EventEntityDto::new)
                .collect(Collectors.toList());
    }

    @RabbitListener(queues = setQueue, returnExceptions = "true")
    @Transactional
    public boolean setCharacteristicsToEvent(CharacteristicsDto characteristicsDto) {
        var eventEntity = eventRepository.findById(characteristicsDto.getEventId()).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        characteristicsDto.getChars().stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .forEach(eventEntity::addCharacteristic);
        eventRepository.save(eventEntity);
        return true;
    }

    @RabbitListener(queues = removeQueue, returnExceptions = "true")
    @Transactional
    public boolean removeCharacteristicsFromEvent(CharacteristicsDto characteristicsDto) {
        var eventEntity = eventRepository.findById(characteristicsDto.getEventId()).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        characteristicsDto.getChars().stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .forEach(eventEntity::removeCharacteristic);
        eventRepository.save(eventEntity);
        return true;
    }

    @RabbitListener(queues = addQueue, returnExceptions = "true")
    public EventEntityDto addEvent(EventEntityDto eventDTO) {
        var addedEvent = eventRepository.save(new EventEntity(eventDTO.getName(), eventDTO.getAgeLimit(), eventDTO.getImage(), eventDTO.getDescription(), eventDTO.getMaxOfVisitors(), eventDTO.getStartDate()));
        return new EventEntityDto(addedEvent);
    }

    @RabbitListener(queues = updaterQueue, returnExceptions = "true")
    public EventEntityDto updateEvent(EventEntityDto eventDTO) {
        var oldEvent = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        oldEvent.setAgeLimit(eventDTO.getAgeLimit());
        oldEvent.setDescription(eventDTO.getDescription());
        oldEvent.setName(eventDTO.getName());
        oldEvent.setImage(eventDTO.getImage());
        oldEvent.setMaxOfVisitors(eventDTO.getMaxOfVisitors());
        oldEvent.setStartDate(eventDTO.getStartDate());

        eventRepository.save(oldEvent);
        return new EventEntityDto(oldEvent);
    }

}
