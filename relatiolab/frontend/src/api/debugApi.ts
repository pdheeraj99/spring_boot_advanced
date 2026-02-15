import { api } from "./client";
import type { FetchComparison, SqlTrace } from "../types/models";

export const debugApi = {
  recentSql: async (limit = 60) => (await api.get<SqlTrace[]>(`/debug/sql/recent?limit=${limit}`)).data,
  clearSql: async () => api.delete("/debug/sql/clear"),
  nplus1Students: async (mode: string) => (await api.get<FetchComparison>(`/debug/nplus1/students-with-enrollments?mode=${mode}`)).data,
  nplus1Courses: async (mode: string) => (await api.get<FetchComparison>(`/debug/nplus1/courses-with-mentors?mode=${mode}`)).data,
  enrollmentReport: async (mode: string) => (await api.get<FetchComparison>(`/debug/nplus1/enrollment-report?mode=${mode}`)).data,
  fetchComparison: async (mode: string) => (await api.get<FetchComparison>(`/debug/fetch-comparison?mode=${mode}`)).data
};