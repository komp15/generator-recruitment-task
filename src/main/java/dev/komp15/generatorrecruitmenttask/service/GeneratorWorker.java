package dev.komp15.generatorrecruitmenttask.service;

import dev.komp15.generatorrecruitmenttask.entity.GeneratedString;
import dev.komp15.generatorrecruitmenttask.entity.Job;
import dev.komp15.generatorrecruitmenttask.entity.JobStatus;
import dev.komp15.generatorrecruitmenttask.repository.GeneratedStringRepository;
import dev.komp15.generatorrecruitmenttask.repository.JobRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@NoArgsConstructor
@EnableAsync
public class GeneratorWorker{

    @PersistenceContext
    private EntityManager entityManager;
    private Random random;
    private JobRepository jobRepository;
    private GeneratedStringRepository stringRepository;

    @Autowired
    public GeneratorWorker(JobRepository jobRepository, GeneratedStringRepository stringRepository) {
        this.jobRepository = jobRepository;
        this.stringRepository = stringRepository;
        this.random = new Random();
    }

    @Async
    @Transactional
    public CompletableFuture<Job> execute(Job job) {
//        job = jobRepository.findById(job.getId()).orElseThrow();
        Job doneJob = doJob(job);
        finishJob(job);
        return CompletableFuture.completedFuture(doneJob);
    }
    @Transactional
    void finishJob(Job job){
        setJobStatus(job, JobStatus.DONE);
        job.setFinishedTime(LocalDateTime.now());
    }

    @Transactional
    Job doJob(Job job){
        try{
            for(int i = 0; i < job.getJobSize(); i++){
                if(i % 10000 == 0) log.info("Generating strings for job id " + job.getId());
                generateStringForJob(job);
            }
        } catch(Throwable t){
            t.printStackTrace();
            log.error("Job " + job.getId() + " crashed!");
            job.setStatus(JobStatus.CRASHED);
        }
        return job;
    }

    @Transactional
    void setJobStatus(Job job, JobStatus jobStatus){
        job.setStatus(jobStatus);
    }

    private void generateStringForJob(Job job){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < generateStringLength(job); i++){
            builder.append(generateRandomCharacter(job));
        }
        addToJob(builder.toString(), job);
    }

    @Transactional
    void addToJob(String s, Job job){
        GeneratedString generatedString = new GeneratedString(s, job);
        job.getGeneratedStrings().add(generatedString);
        stringRepository.save(generatedString);
    }

    private long generateStringLength(Job job){
        return job.getMinLength() + random.nextLong(job.getMaxLength()-job.getMinLength());
    }

    private char generateRandomCharacter(Job job){
        return job.getChars()[random.nextInt(job.getChars().length)];
    }


}
