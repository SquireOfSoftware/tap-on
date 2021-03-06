package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.BulkSignInObject;
import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;
import com.squireofsoftware.peopleproject.services.CheckinLogService;
import com.squireofsoftware.peopleproject.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/checkin")
@CrossOrigin
public class CheckinController {
    @Autowired
    private final PersonService personService;
    @Autowired
    private final CheckinLogService checkinLogService;

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
    public CheckinLogObject signIn(@RequestBody SignInObject signInObject) {
        return checkinLogService.checkin(signInObject);
    }

    @PostMapping(value = "/signin/people/")
    public List<CheckinLogObject> bulkSignIn(@RequestBody BulkSignInObject bulkSignInObject) {
//        return checkinLogService.bulkCheckIn(bulkSignInObject);
        return null;
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
}
