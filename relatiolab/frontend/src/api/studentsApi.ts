import { api } from "./client";
import type { Student } from "../types/models";

export const studentApi = {
  list: async () => (await api.get<Student[]>("/students")).data,
  create: async (payload: { name: string; email: string }) => (await api.post<Student>("/students", payload)).data,
  upsertProfile: async (studentId: number, payload: { phone: string; address?: string; linkedinUrl?: string }) =>
    (await api.post<Student>(`/students/${studentId}/profile`, payload)).data,
  deleteProfile: async (studentId: number) => api.delete(`/students/${studentId}/profile`)
};