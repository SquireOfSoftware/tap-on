package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.*;
import com.squireofsoftware.peopleproject.services.CheckinLogService;
import com.squireofsoftware.peopleproject.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/checkin")
@CrossOrigin
public class CheckinController {
    @Autowired
    private final PersonService personService;
    @Autowired
    private final CheckinLogService checkinLogService;
    static final String CSV_HEADER = "timestamp,message,person_id,given_name,family_name,other_names,\n";
    static final String CSV_BODY_FORMAT = "%s,%s,%s,%s,%s,%s";
    static final String CSV_REQUEST_HEADER = PersonController.CSV_MEDIA_TYPE + "; charset=utf-16";

    public CheckinController(PersonService personService,
                             CheckinLogService checkinLogService) {
        this.personService = personService;
        this.checkinLogService = checkinLogService;
    }

    @GetMapping(value = "/hash/{hash}")
    public PersonObject findPersonByHash(@PathVariable String hash) {
        return personService.findPersonByHash(hash);
    }

    @GetMapping(value = "/person/hash/{personId}")
    public String getHashByPersonId(@PathVariable Integer personId) {
        return personService.getPerson(personId).getHash();
    }

    @PostMapping(value = "/signin")
    public CheckinLogObject signIn(@Valid @RequestBody SignInObject signInObject) {
        return checkinLogService.checkin(signInObject);
    }

    @PostMapping(value = "/signin/people/")
    public List<CheckinLogObject> bulkSignIn(@Valid @RequestBody BulkSignInObject bulkSignInObject) {
        return checkinLogService.bulkCheckIn(bulkSignInObject);
    }

    @PostMapping(value = "/signin/hash/{hash}")
    public CheckinLogObject signIn(@PathVariable String hash, @RequestParam(value = "message", required = false) String message) {
        return checkinLogService.checkin(hash, message);
    }

    @GetMapping(value = "/people/log/hash/{hash}")
    public List<CheckinLogObject> getLogs(@PathVariable String hash,
                                          @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                          @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        if (fromDate != null && toDate != null) {
            return checkinLogService.getPersonsLogsFromTo(hash, fromDate, toDate);
        } else if (fromDate == null && toDate != null) {
            return checkinLogService.getPersonsLogsTo(hash, toDate);
        } else if (fromDate != null) {
            return checkinLogService.getPersonsLogsFrom(hash, fromDate);
        } else {
            return checkinLogService.getAllPersonsLogs(hash);
        }
    }

    @GetMapping(value = "/signins/today")
    public List<CheckinLogObject> getSigninsForToday() {
        return checkinLogService.getSignInsForToday();
    }

    @GetMapping(value = "/signins/from/{from}")
    public List<CheckinLogObject> getSigninsFrom(@PathVariable(value = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        return checkinLogService.getSignInsFrom(from);
    }

    @GetMapping(value = "/signins/from/{from}/csv")
    public void exportSigninsForTodayInCSV(@PathVariable(value = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                        HttpServletResponse response) throws IOException {
        // convert the sign ins for today into csv string
        List<CheckinLogObject> signins = checkinLogService.getSignInsFrom(from);

        response.setContentType(CSV_REQUEST_HEADER);

        if (signins != null && !signins.isEmpty()) {
            String body = buildCheckinLogCSVBody(signins);
            response.getWriter().write(CSV_HEADER + body);
        }
        // if there are no sign ins it should just return an empty csv
    }

    private String buildCheckinLogCSVBody(List<CheckinLogObject> checkinLogs) {
        return checkinLogs.stream()
                .map(log -> String.format(CSV_BODY_FORMAT,
                        log.getTimestamp(),
                        log.getMessage() == null ? "" : log.getMessage(),
                        log.getPerson().getId(),
                        log.getPerson().getGivenName(),
                        log.getPerson().getFamilyName() == null ?
                            "" : log.getPerson().getFamilyName(),
                        log.getPerson().getOtherNames() != null ?
                            log.getPerson().getOtherNames()
                                    .stream()
                                    .map(NameObject::getName)
                                    .collect(Collectors.joining("|")) :
                            null
                        ))
                .collect(Collectors.joining("\n"));
    }
}
