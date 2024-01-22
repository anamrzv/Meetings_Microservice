package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class CharacteristicsDto implements Serializable {
    Set<String> chars;
    Long eventId;
}
