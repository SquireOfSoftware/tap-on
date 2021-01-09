package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;
import com.squireofsoftware.peopleproject.services.CheckinLogService;
import com.squireofsoftware.peopleproject.services.HashService;
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
    private final HashService hashService;
    @Autowired
    private final CheckinLogService checkinLogService;

    public CheckinController(HashService hashService,
                             CheckinLogService checkinLogService) {
        this.hashService = hashService;
        this.checkinLogService = checkinLogService;
    }

    @GetMapping(value = "/hash/{hash}")
    public PersonObject findPersonByHash(@PathVariable Integer hash) {
        return hashService.getPerson(hash);
    }

    @GetMapping(value = "/person/hash/{personId}")
    public Integer getHashByPersonId(@PathVariable Integer personId) {
        return hashService.getHash(personId);
    }

    @PostMapping(value = "/signin")
    public CheckinLogObject signIn(@RequestBody SignInObject signInObject) {
        return checkinLogService.checkin(signInObject);
    }

    @GetMapping(value = "/people/log/hash/{hash}")
    public List<CheckinLogObject> getLogs(@PathVariable Integer hash,
                                          @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                          @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        if (fromDate != null && toDate != null) {
            return checkinLogService.getLogsFromTo(hash, fromDate, toDate);
        } else if (fromDate == null && toDate != null) {
            return checkinLogService.getLogsTo(hash, toDate);
        } else if (fromDate != null) {
            return checkinLogService.getLogsFrom(hash, fromDate);
        } else {
            return checkinLogService.getAllLogs(hash);
        }
    }
}
