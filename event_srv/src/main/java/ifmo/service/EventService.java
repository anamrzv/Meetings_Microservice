package ifmo.service;

import ifmo.dto.EventEntityDto;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.model.EventEntity;
import ifmo.repository.CharacteristicRepository;
import ifmo.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CharacteristicRepository characteristicRepository;

    public List<EventEntityDto> findAllChild() {
        return eventRepository.getEventEntitiesByAgeLimitBefore(18).stream().map(EventEntityDto::new).collect(Collectors.toList());
    }

    public Page<EventEntityDto> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventEntityDto::new);
    }

    public EventEntityDto findEventById(Long id) {
        var eventEntity = eventRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        return new EventEntityDto(eventEntity);
    }

    public List<EventEntityDto> getEventsByCharacteristics(Set<String> chars) {
        var filter = chars.stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .collect(Collectors.toSet());
        return eventRepository.getEventEntitiesByCharacteristicsIn(filter)
                .stream().map(EventEntityDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void setCharacteristicsToEvent(Long id, Set<String> chars) {
        var eventEntity = eventRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        chars.stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .forEach(eventEntity::addCharacteristic);
        eventRepository.save(eventEntity);
    }

    @Transactional
    public void removeCharacteristicsFromEvent(Long id, Set<String> chars) {
        var eventEntity = eventRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
        chars.stream()
                .map(c -> characteristicRepository.getCharacteristicEntityByName(c).orElseThrow(() -> new CustomNotFoundException("Характеристики не найдено")))
                .forEach(eventEntity::removeCharacteristic);
        eventRepository.save(eventEntity);
    }

    public EventEntityDto addEvent(EventEntityDto eventDTO) {
        var addedEvent = eventRepository.save(new EventEntity(eventDTO.getName(), eventDTO.getAgeLimit(), eventDTO.getImage(), eventDTO.getDescription(), eventDTO.getMaxOfVisitors(), eventDTO.getStartDate()));
        return new EventEntityDto(addedEvent);
    }
    public EventEntityDto updateEvent(long id, EventEntityDto eventDTO) {
        var oldEvent = eventRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Мероприятия с таким id не существует"));
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
