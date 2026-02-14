package com.example.hibernaterelationshipslab.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientInsuranceCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateInsuranceCardAndRejectSecondCardForSamePatient() throws Exception {
        String email = "patient-" + UUID.randomUUID() + "@example.com";
        String createPatientPayload = """
                {
                  "fullName": "Test Patient",
                  "email": "%s"
                }
                """.formatted(email);

        MvcResult patientResult = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPatientPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long patientId = readId(patientResult, "id");

        String firstCardPayload = """
                {
                  "providerName": "ShieldCare",
                  "policyNumber": "POL-%s",
                  "validTill": "%s"
                }
                """.formatted(UUID.randomUUID(), LocalDate.now().plusDays(20));

        mockMvc.perform(post("/api/patients/{patientId}/insurance-card", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstCardPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.providerName").value("ShieldCare"));

        String secondCardPayload = """
                {
                  "providerName": "NewCare",
                  "policyNumber": "POL-%s",
                  "validTill": "%s"
                }
                """.formatted(UUID.randomUUID(), LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/patients/{patientId}/insurance-card", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondCardPayload))
                .andExpect(status().isConflict());
    }

    private Long readId(MvcResult result, String field) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get(field).asLong();
    }
}
