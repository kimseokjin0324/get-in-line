package com.uno.getinline.service;

import com.uno.getinline.constant.EventStatus;
import com.uno.getinline.dto.EventDTO;
import com.uno.getinline.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService sut;
    @Mock
    private EventRepository eventRepository;

    //리스트 조회
    @DisplayName("검색 조건없이 이벤트 검색하면, 전체결과를 출력하여 보여줌")
    @Test
    void givenNothing_whenSearchingEvent_thenReturnEntireEventList() {
        //Given
        given(eventRepository.findEvents(null,null,null,null,null))
                .willReturn(List.of(
                        createEventDTO(1L,"오전 운동",true),
                        createEventDTO(1L,"오후 운동",false)
                ));
        //When
        List<EventDTO> list = sut.getEvents(null, null, null, null, null);
        //Then
        assertThat(list).hasSize(2);

//        then(eventRepository).should().findEvents(null,null,null,null,null); //-아래와 동일한 역할을 한다.
        verify(eventRepository).findEvents(null,null,null,null,null);
    }

    @DisplayName("검색 조건과 이벤트 검색하면, 검색된 결과를 출력하여 보여줌")
    @Test
    void givenSearchParams_whenSearchingEvent_thenReturnEventList() {
        //Given
        Long placeId = 1L;
        String eventName = "오전 운동";
        EventStatus eventStatus = EventStatus.OPENED;
        LocalDateTime eventStartDatetime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime eventEndDatetime = LocalDateTime.of(2022, 1, 2, 0, 0, 0);

        given(eventRepository.findEvents(placeId,eventName,eventStatus,eventStartDatetime,eventEndDatetime))
                .willReturn(List.of(
                        createEventDTO(1L,"오전 운동",eventStatus,eventStartDatetime,eventEndDatetime)
                ));
        //When
        List<EventDTO> list = sut.getEvents(placeId, eventName, eventStatus, eventStartDatetime, eventEndDatetime);
        //Then
        assertThat(list)
                .hasSize(1)
                .allSatisfy(event -> {
                    assertThat(event)
                            .hasFieldOrPropertyWithValue("placeId", placeId)
                            .hasFieldOrPropertyWithValue("eventName", eventName)
                            .hasFieldOrPropertyWithValue("eventStatus", eventStatus);
                    assertThat(event.eventStartDatetime()).isAfterOrEqualTo(eventStartDatetime);
                    assertThat(event.eventStartDatetime()).isBeforeOrEqualTo(eventStartDatetime);
                });
        then(eventRepository).should().findEvents(placeId,eventName,eventStatus,eventStartDatetime,eventEndDatetime);
    }

    /**
     * 조회
     */
    @DisplayName("이벤트 ID로 존재하는 이벤트를 조회하면, 해당 이벤트 정보를 보여준다.")
    @Test
    void givenEventId_whenSearchingExistingEvent_thenReturnsEvent() {
        //Given
        Long eventId = 1L;
        EventDTO eventDTO = createEventDTO(1L, "오전운동", true);
        given(eventRepository.findEvent(eventId)).willReturn(Optional.of(eventDTO));
        //When
        Optional<EventDTO> result = sut.findEvent(eventId);

        //Then
        assertThat(result).hasValue(eventDTO);
        then(eventRepository).should().findEvent(eventId);
    }

    @DisplayName("이벤트 ID로  이벤트를 조회하면, 빈 정보를 출력하여 보여준다.")
    @Test
    void givenEventId_whenSearchingNonexistentEvent_thenReturnsEmptyOptional() {
        //Given
        Long eventId = 2L;
        given(eventRepository.findEvent(eventId)).willReturn(Optional.empty());

        //When
        Optional<EventDTO> result = sut.findEvent(eventId);

        //Then
        assertThat(result).isEmpty();
        verify(eventRepository).findEvent(eventId);

    }


    @DisplayName("이벤트 정보를 주면, 이벤트를 생성하고 결과를 true 로 보여준다.")
    @Test
    void givenEvent_whenCreating_thenCreatesEventAndReturnsTrue() {
        // Given
        EventDTO dto = createEventDTO(1L, "오후 운동", false);
        given(eventRepository.insertEvent(dto)).willReturn(true);

        // When
        boolean result = sut.createEvent(dto);

        // Then
        assertThat(result).isTrue();
        verify(eventRepository).insertEvent(dto);
    }

    @DisplayName("이벤트 정보를 주지 않으면, 생성 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNothing_whenCreating_thenAbortCreatingAndReturnsFalse() {
        // Given
        given(eventRepository.insertEvent(null)).willReturn(false);

        // When
        boolean result = sut.createEvent(null);

        // Then
        assertThat(result).isFalse();
        verify(eventRepository).insertEvent(null);
    }

    @DisplayName("이벤트 ID와 정보를 주면, 이벤트 정보를 변경하고 결과를 true 로 보여준다.")
    @Test
    void givenEventIdAndItsInfo_whenModifying_thenModifiesEventAndReturnsTrue() {
        //Given
        Long eventId = 1L;
        EventDTO dto = createEventDTO(1L, "오후운동", false);
        given(eventRepository.updateEvent(eventId,dto)).willReturn(true);
        //When
        boolean result = sut.modifyEvent(eventId, dto);
        //Then
        assertThat(result).isTrue();
        verify(eventRepository).updateEvent(eventId,dto);
    }

    @DisplayName("이벤트 ID와 정보를 주지 않으면, 이벤트 정보를 변경 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNoEventIdAndItsInfo_whenModifying_thenAbortModifyingEventAndReturnsFalse() {
        //Given

        EventDTO dto = createEventDTO(1L, "오후운동", false);
        given(eventRepository.updateEvent(null,dto)).willReturn(false);

        //When
        boolean result = sut.modifyEvent(null, dto);
        //Then
        assertThat(result).isFalse();
        then(eventRepository).should().updateEvent(null,dto);
    }

    @DisplayName("이벤트 ID만 주고 변경할 정보를 주지않으면, 이벤트 정보를 변경 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenEventIdOnly_whenModifying_thenAbortModifyingEventAndReturnsFalse() {
        //Given
        Long eventId = 1L;
        given(eventRepository.updateEvent(eventId,null)).willReturn(false);
        //When
        boolean result = sut.modifyEvent(eventId, null);
        //Then
        assertThat(result).isFalse();
        then(eventRepository).should().updateEvent(eventId,null);
    }

    /**
     * 삭제
     **/
    @DisplayName("이벤트 Id를 주면, 이벤트 정보를 삭제하고 결과를 true로 보여진다.")
    @Test
    void givenEventId_whenDeleting_thenDeletesEventAndReturnsTrue() {
        //Given
        Long eventId = 1L;
        given(eventRepository.deleteEvent(eventId)).willReturn(true);
        //When
        boolean result = sut.removeEvent(eventId);

        //Then
        assertThat(result).isTrue();
        then(eventRepository).should().deleteEvent(eventId);
    }

    @DisplayName("이벤트 Id를 주지 않으면,삭제 중단하고 결과를 false로 보여진다.")
    @Test
    void givenNothing_whenDeleting_thenAbortsDeletingAndReturnsFalse() {
        //Given
        given(eventRepository.deleteEvent(null)).willReturn(false);

        //When
        boolean result = sut.removeEvent(null);

        //Then
        assertThat(result).isFalse();
        then(eventRepository).should().deleteEvent(null);
    }


    private EventDTO createEventDTO(long placeId, String eventName, boolean isMorning) {
        String hourStart = isMorning ? "09" : "13";
        String hourEnd = isMorning ? "12" : "16";

        return createEventDTO(
                placeId,
                eventName,
                EventStatus.OPENED,
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourStart)),
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourEnd))
        );
    }

    private EventDTO createEventDTO(
            long placeId,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDateTime,
            LocalDateTime eventEndDateTime
    ) {
        return EventDTO.of(
                placeId,
                eventName,
                eventStatus,
                eventStartDateTime,
                eventEndDateTime,
                0,
                24,
                "마스크 꼭 착용하세요",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


}