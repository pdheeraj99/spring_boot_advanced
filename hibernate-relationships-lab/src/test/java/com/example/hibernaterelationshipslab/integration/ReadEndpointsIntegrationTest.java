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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReadEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReadDoctorAppointmentsAndPatientDetailsWithoutLazySerializationErrors() throws Exception {
        Long doctorId = createDoctor();
        Long patientId = createPatient();

        String appointmentPayload = """
                {
                  "doctorId": %d,
                  "patientId": %d,
                  "appointmentAt": "%s",
                  "reason": "Follow-up"
                }
                """.formatted(doctorId, patientId, LocalDateTime.now().plusDays(3).withNano(0));
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/doctors/{doctorId}/appointments", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(doctorId))
                .andExpect(jsonPath("$[0].patientId").value(patientId));

        mockMvc.perform(get("/api/patients/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.appointments.length()").value(1));
    }

    private Long createDoctor() throws Exception {
        String payload = """
                {
                  "fullName": "Doctor %s",
                  "licenseNumber": "LIC-%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());
        MvcResult result = mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();
        return readId(result);
    }

    private Long createPatient() throws Exception {
        String payload = """
                {
                  "fullName": "Patient %s",
                  "email": "patient-%s@example.com"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());
        MvcResult result = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();
        return readId(result);
    }

    private Long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
