import { api } from "./client";
import type { Course } from "../types/models";

export const courseApi = {
  list: async () => (await api.get<Course[]>("/courses")).data,
  create: async (payload: { title: string; fee: number; active: boolean }) => (await api.post<Course>("/courses", payload)).data,
  linkMentor: async (courseId: number, mentorId: number) => (await api.post<Course>(`/courses/${courseId}/mentors/${mentorId}`)).data,
  unlinkMentor: async (courseId: number, mentorId: number) => (await api.delete<Course>(`/courses/${courseId}/mentors/${mentorId}`)).data
};