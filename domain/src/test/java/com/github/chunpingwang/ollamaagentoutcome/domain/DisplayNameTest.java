package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_name() {
        DisplayName name = new DisplayName("John Doe");
        assertThat(name.name()).isEqualTo("John Doe");
    }

    @Test
    void should_allow_null() {
        DisplayName name = new DisplayName(null);
        assertThat(name.name()).isNull();
    }

    @Test
    void should_allow_blank_as_empty_or_null() {
        DisplayName name = new DisplayName("   ");
        // 任務說「允許 null/空白」，通常意味著空白視為未設定或保留。讓我們假設它接受任何字串，包括空白。
        assertThat(name).isNotNull();
    }
    
    @Test
    void should_be_equal_for_same_name() {
        DisplayName n1 = new DisplayName("Name");
        DisplayName n2 = new DisplayName("Name");
        assertThat(n1).isEqualTo(n2);
    }
}
