package com.relatiolab.service;

import com.relatiolab.dto.request.CreateMentorRequest;
import com.relatiolab.dto.request.CreateSkillRequest;
import com.relatiolab.dto.response.MentorResponse;
import com.relatiolab.dto.response.SkillResponse;
import com.relatiolab.entity.Mentor;
import com.relatiolab.entity.Skill;
import com.relatiolab.exception.ResourceNotFoundException;
import com.relatiolab.repository.MentorRepository;
import com.relatiolab.repository.SkillRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;
    private final SkillRepository skillRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public MentorResponse create(CreateMentorRequest request) {
        Mentor mentor = new Mentor();
        mentor.setName(request.name());
        mentor.setExpertiseLevel(request.expertiseLevel());
        return dtoMapper.toMentor(mentorRepository.save(mentor));
    }

    @Transactional
    public List<MentorResponse> list() {
        return mentorRepository.findAll().stream().map(dtoMapper::toMentor).toList();
    }

    @Transactional
    public MentorResponse get(Long id) {
        Mentor mentor = mentorRepository.findGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + id));
        return dtoMapper.toMentor(mentor);
    }

    @Transactional
    public SkillResponse createSkill(CreateSkillRequest request) {
        Skill skill = new Skill();
        skill.setCode(request.code());
        skill.setDisplayName(request.displayName());
        return dtoMapper.toSkill(skillRepository.save(skill));
    }

    @Transactional
    public List<SkillResponse> listSkills() {
        return skillRepository.findAll().stream().map(dtoMapper::toSkill).toList();
    }

    @Transactional
    public MentorResponse linkSkill(Long mentorId, Long skillId) {
        Mentor mentor = mentorRepository.findGraphById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + mentorId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        mentor.addSkill(skill);
        return dtoMapper.toMentor(mentorRepository.save(mentor));
    }

    @Transactional
    public MentorResponse unlinkSkill(Long mentorId, Long skillId) {
        Mentor mentor = mentorRepository.findGraphById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + mentorId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        mentor.removeSkill(skill);
        return dtoMapper.toMentor(mentorRepository.save(mentor));
    }

    public void delete(Long id) {
        if (!mentorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mentor not found: " + id);
        }
        mentorRepository.deleteById(id);
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found: " + id);
        }
        skillRepository.deleteById(id);
    }
}