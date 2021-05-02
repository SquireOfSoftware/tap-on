package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.services.CheckinLogService;
import com.squireofsoftware.peopleproject.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.squireofsoftware.peopleproject.controllers.CheckinController.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CheckinControllerTest {
    @Mock
    private PersonService mockPersonService;
    @Mock
    private CheckinLogService mockCheckinLogService;

    private CheckinController checkinController;

    @BeforeEach
    public void setup() {
        openMocks(this);
        checkinController = new CheckinController(mockPersonService, mockCheckinLogService);
    }

    @ParameterizedTest
    @MethodSource("generateSignInData")
    public void exportSigninsForTodayInCSV(List<CheckinLogObject> injectedCheckinLogs,
                                           String expectedResponseBody) throws IOException {
        // given
        PrintWriter mockPrintWriter = mock(PrintWriter.class);

        HttpServletResponse capturedResponse = mock(HttpServletResponse.class);
        when(capturedResponse.getWriter())
                .thenReturn(mockPrintWriter);

        LocalDateTime dummyTime = LocalDateTime.now().minusHours(1L);

        when(mockCheckinLogService.getSignInsFrom(any()))
                .thenReturn(injectedCheckinLogs);

        // when
        checkinController.exportSigninsForTodayInCSV(dummyTime, capturedResponse);

        // then
        verify(capturedResponse, times(1))
                .setContentType(eq(CSV_REQUEST_HEADER));

        if (injectedCheckinLogs != null && !injectedCheckinLogs.isEmpty()) {
            verify(capturedResponse, times(1)).getWriter();

            ArgumentCaptor<String> responseBody = ArgumentCaptor.forClass(String.class);
            verify(mockPrintWriter, times(1))
                    .write(responseBody.capture());
            assertEquals(expectedResponseBody, responseBody.getValue());
        } else {
            verify(capturedResponse, times(0)).getWriter();
        }
    }

    private static Stream<Arguments> generateSignInData() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        null,
                        ""
                ),
                Arguments.of(
                        Collections.emptyList(),
                        ""
                ),
                Arguments.of(
                        Collections.singletonList(CheckinLogObject.builder()
                                .timestamp(now)
                                .person(PersonObject.builder()
                                        .givenName("Test")
                                        .familyName("Boy")
                                        .id(3)
                                        .build())
                                .build()
                        ),
                        CSV_HEADER +
                                String.format(CSV_BODY_FORMAT,
                                        now,
                                        "",
                                        3,
                                        "Test",
                                        "Boy",
                                        "")
                ),
                Arguments.of(
                        Arrays.asList(
                                CheckinLogObject.builder()
                                    .timestamp(now)
                                    .person(PersonObject.builder()
                                            .givenName("Test")
                                            .familyName("Boy")
                                            .id(3)
                                            .build())
                                    .build(),
                                CheckinLogObject.builder()
                                        .timestamp(now)
                                        .person(PersonObject.builder()
                                                .givenName("Another")
                                                .familyName("Test")
                                                .id(7)
                                                .build())
                                        .build(),
                                CheckinLogObject.builder()
                                        .timestamp(now.minusHours(1L))
                                        .person(PersonObject.builder()
                                                .givenName("人")
                                                .id(2)
                                                .build())
                                        .build()
                        ),
                        CSV_HEADER +
                                String.format(CSV_BODY_FORMAT,
                                        now,
                                        "",
                                        3,
                                        "Test",
                                        "Boy",
                                        "") + "\n" +
                                String.format(CSV_BODY_FORMAT,
                                        now,
                                        "",
                                        7,
                                        "Another",
                                        "Test",
                                        "") + "\n" +
                                String.format(CSV_BODY_FORMAT,
                                        now.minusHours(1L),
                                        "",
                                        2,
                                        "人",
                                        "",
                                        "")
                )
        );
    }
}