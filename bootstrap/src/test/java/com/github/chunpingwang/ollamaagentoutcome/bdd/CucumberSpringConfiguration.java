package com.github.chunpingwang.ollamaagentoutcome.bdd;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaDeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaLoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.JpaSaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileRepository;
import com.github.chunpingwang.ollamaagentoutcome.bootstrap.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@Import({
    TestcontainersConfiguration.class,
    JpaSaveProfilePort.class,
    JpaLoadProfilePort.class,
    JpaDeleteProfilePort.class,
    ProfileRepository.class
})
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
