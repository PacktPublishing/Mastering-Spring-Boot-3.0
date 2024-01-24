package com.packt.ahmeric.bookstorebatch.config;

import com.packt.ahmeric.bookstorebatch.data.Publisher;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class BatchConfig {

    private final EntityManagerFactory entityManagerFactory;

    public BatchConfig(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public FlatFileItemReader<Publisher> reader() {
        return new FlatFileItemReaderBuilder<Publisher>()
                .name("bookItemReader")
                .resource(new ClassPathResource("publishers.csv"))
                .delimited()
                .names(new String[]{"name", "address"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Publisher.class);
                }}).linesToSkip(1)
                .build();
    }


    @Bean
    public ItemProcessor<Publisher, Publisher> processor() {
        return publisher -> {
            publisher.setAddress(publisher.getAddress().toUpperCase());
            publisher.setName(publisher.getName().toUpperCase());
            return publisher;
        };
    }

    @Bean
    public JpaItemWriter<Publisher> writer() {
        JpaItemWriter<Publisher> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Job importPublisherJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importPublisherJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ItemReader<Publisher> reader,
                      ItemProcessor<Publisher, Publisher> processor,
                      ItemWriter<Publisher> writer) {

        StepBuilder stepBuilder = new StepBuilder("step1",jobRepository);

        return stepBuilder.<Publisher, Publisher >chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
