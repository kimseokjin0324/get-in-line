package com.uno.getinline.repository;

import com.uno.getinline.constant.EventStatus;
import com.uno.getinline.dto.EventDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//- JPA로 만들예정이므로 인터페이스로 만듦
// TODO:인스턴스 생성 편의를 위해 임시로 default 사용, repository layer 구현시 삭제
public interface EventRepository {
    default List<EventDTO> findEvents(
            Long placeId,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetiem
    ) {
        return List.of();
    }

    default Optional<EventDTO> findEvent(Long eventId) {
        return Optional.empty();
    }

    default boolean insertEvent(EventDTO eventDTO) {
        return false;
    }

    default boolean updateEvent(Long eventId, EventDTO eventDTO) {
        return false;
    }

    default boolean deleteEvent(Long eventId) {
        return false;
    }
}
