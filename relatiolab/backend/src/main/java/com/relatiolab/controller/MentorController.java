package com.relatiolab.controller;

import com.relatiolab.dto.request.CreateMentorRequest;
import com.relatiolab.dto.request.CreateSkillRequest;
import com.relatiolab.dto.response.MentorResponse;
import com.relatiolab.dto.response.SkillResponse;
import com.relatiolab.service.MentorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/mentors")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorResponse createMentor(@Valid @RequestBody CreateMentorRequest request) {
        return mentorService.create(request);
    }

    @GetMapping("/mentors")
    public List<MentorResponse> listMentors() {
        return mentorService.list();
    }

    @GetMapping("/mentors/{id}")
    public MentorResponse getMentor(@PathVariable Long id) {
        return mentorService.get(id);
    }

    @DeleteMapping("/mentors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@PathVariable Long id) {
        mentorService.delete(id);
    }

    @PostMapping("/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse createSkill(@Valid @RequestBody CreateSkillRequest request) {
        return mentorService.createSkill(request);
    }

    @GetMapping("/skills")
    public List<SkillResponse> listSkills() {
        return mentorService.listSkills();
    }

    @DeleteMapping("/skills/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable Long id) {
        mentorService.deleteSkill(id);
    }

    @PostMapping("/mentors/{mentorId}/skills/{skillId}")
    public MentorResponse linkSkill(@PathVariable Long mentorId, @PathVariable Long skillId) {
        return mentorService.linkSkill(mentorId, skillId);
    }

    @DeleteMapping("/mentors/{mentorId}/skills/{skillId}")
    public MentorResponse unlinkSkill(@PathVariable Long mentorId, @PathVariable Long skillId) {
        return mentorService.unlinkSkill(mentorId, skillId);
    }
}