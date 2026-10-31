package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class CreateProfileUseCaseContractTest {

    @Test
    void should_have_execute_method_with_name_and_email_parameters() throws NoSuchMethodException {
        // Given & When
        Method method = CreateProfileUseCase.class.getMethod("execute", Name.class, Email.class);

        // Then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(void.class);
    }
}
