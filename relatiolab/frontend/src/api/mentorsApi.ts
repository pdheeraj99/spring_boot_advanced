import { api } from "./client";
import type { Mentor, Skill } from "../types/models";

export const mentorApi = {
  list: async () => (await api.get<Mentor[]>("/mentors")).data,
  create: async (payload: { name: string; expertiseLevel: string }) => (await api.post<Mentor>("/mentors", payload)).data,
  linkSkill: async (mentorId: number, skillId: number) => (await api.post<Mentor>(`/mentors/${mentorId}/skills/${skillId}`)).data,
  unlinkSkill: async (mentorId: number, skillId: number) => (await api.delete<Mentor>(`/mentors/${mentorId}/skills/${skillId}`)).data,
  listSkills: async () => (await api.get<Skill[]>("/skills")).data,
  createSkill: async (payload: { code: string; displayName: string }) => (await api.post<Skill>("/skills", payload)).data
};