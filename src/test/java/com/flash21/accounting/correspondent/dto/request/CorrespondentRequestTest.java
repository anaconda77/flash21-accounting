package com.flash21.accounting.correspondent.dto.request;

import static com.flash21.accounting.common.ErrorCodeAssertions.assertErrorCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flash21.accounting.common.ErrorCodeAssertions;
import com.flash21.accounting.common.GlobalExceptionHandler;
import com.flash21.accounting.correspondent.controller.CorrespondentController;
import com.flash21.accounting.correspondent.service.CorrespondentService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

class CorrespondentRequestTest {

    private MockMvc mockMvc;
    private CorrespondentService correspondentService;

    @BeforeEach
    void setUp() {
        correspondentService = mock(CorrespondentService.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet(); // validator 초기화

        mockMvc = MockMvcBuilders
            .standaloneSetup(new CorrespondentController(correspondentService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    @DisplayName("validation 동작 테스트, validation 위반")
    void correspondentRequestTest() throws Exception {
        String json = """
            {
            	"correspondentName" : "String",
            	"ownerId": 1,
            	"managerName":"String",
            	"managerPosition":"String",
                "managerPhoneNumber":"String",
            	"managerEmail":"String",
            	"taxEmail":"String",
            	"businessRegNumber":"",
            	"address":"String",
            	"detailedAddress":"String",
            	"memo":"String",
            	"categoryName" : "헬스장"
            }
            """;

        mockMvc.perform(multipart("/api/correspondent")
                .file(new MockMultipartFile("json", "", "application/json", json.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(MethodArgumentNotValidException.class))
            .andDo(print());
    }
}