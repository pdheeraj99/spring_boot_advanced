import { api } from "./client";
import type { Enrollment } from "../types/models";

export const enrollmentApi = {
  list: async () => (await api.get<Enrollment[]>("/enrollments")).data,
  create: async (payload: { studentId: number; courseId: number; progressPercent: number }) =>
    (await api.post<Enrollment>("/enrollments", payload)).data,
  updateProgress: async (id: number, progressPercent: number) =>
    (await api.patch<Enrollment>(`/enrollments/${id}/progress`, { progressPercent })).data
};