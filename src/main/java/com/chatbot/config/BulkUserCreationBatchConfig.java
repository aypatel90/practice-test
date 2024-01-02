package com.chatbot.config;

import com.chatbot.domain.BlankLineRecordSeparatorPolicy;
import com.chatbot.entity.User;
import com.chatbot.entity.dto.RegisterRequest;
import com.chatbot.service.AuthenticationService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
public class BulkUserCreationBatchConfig {

    @Autowired
    AuthenticationService authenticationService;

    @Bean
    public FlatFileItemReader<RegisterRequest> reader() {

        FlatFileItemReader<RegisterRequest> reader= new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("/bulk-user-creation-data-dir/user_creation_data.csv"));

        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(DELIMITER_COMMA);
                setNames("firstname","lastname","email","userName","password");
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(RegisterRequest.class);
            }});
        }});

        reader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());

        return reader;
    }

    //Writer class Object
    @Bean
    public ItemWriter<User> writer(){
        return chunk -> {
            System.out.println("Saving Invoice Records: " + chunk);
            authenticationService.writingRegistration(chunk);
        };
    }

    //Processor class Object
    @Bean
    public ItemProcessor<RegisterRequest,User> processor(){
        return item -> {
            System.out.println("Processing Invoice Records: " + item);
            return authenticationService.processingRegistration(item);
        };
    }

    //Listener class Object
    @Bean
    public JobExecutionListener listener() {
        return new UserListener();
    }


    //Step Object
    @Bean
    public Step stepA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        System.out.println("Preparing Step: ");
        return new StepBuilder("stepA", jobRepository)
                .<RegisterRequest,User>chunk(3, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    //Job Object
    @Bean
    public Job jobA(JobRepository jobRepository, Step stepA){
        return new JobBuilder("BulkUserCreationJob", jobRepository)
                .listener(listener())
                .start(stepA)
                .build();
    }

}
