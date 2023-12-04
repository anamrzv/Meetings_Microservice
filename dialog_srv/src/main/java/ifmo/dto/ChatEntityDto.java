package ifmo.dto;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public record ChatEntityDto(Long id, LocalDateTime creationDate) implements Serializable {
}