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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorSpecialtyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLinkAndUnlinkDoctorSpecialtyAndRejectDuplicateLink() throws Exception {
        Long doctorId = createDoctor();
        Long specialtyId = createSpecialty();

        mockMvc.perform(post("/api/doctors/{doctorId}/specialties/{specialtyId}", doctorId, specialtyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialties.length()").value(1));

        mockMvc.perform(post("/api/doctors/{doctorId}/specialties/{specialtyId}", doctorId, specialtyId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/specialties/{specialtyId}/doctors", specialtyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors.length()").value(1));

        mockMvc.perform(delete("/api/doctors/{doctorId}/specialties/{specialtyId}", doctorId, specialtyId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/specialties/{specialtyId}/doctors", specialtyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors.length()").value(0));
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

    private Long createSpecialty() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        String payload = """
                {
                  "code": "SP-%s",
                  "name": "Specialty %s"
                }
                """.formatted(suffix, suffix);
        MvcResult result = mockMvc.perform(post("/api/specialties")
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
