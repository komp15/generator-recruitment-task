package dev.komp15.generatorrecruitmenttask.controller;

import dev.komp15.generatorrecruitmenttask.dto.JobCreationRequestDTO;
import dev.komp15.generatorrecruitmenttask.dto.JobDTO;
import dev.komp15.generatorrecruitmenttask.entity.Job;
import dev.komp15.generatorrecruitmenttask.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class JobController {

    private final JobService service;

    @PostMapping("/job")
    public ResponseEntity<JobDTO> createJob(@RequestBody JobCreationRequestDTO creationRequest){
        return ResponseEntity.ok(service.addJob(creationRequest));
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJob(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.findJobById(id));
    }

    @GetMapping("/job/running")
    public ResponseEntity<List<JobDTO>> getRunningJobs(){
        return ResponseEntity.ok(service.getRunningJobs());
    }

}
