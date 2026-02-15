export interface StudentProfile {
  id: number;
  phone: string;
  address?: string;
  linkedinUrl?: string;
}

export interface Enrollment {
  id: number;
  studentId: number;
  studentName: string;
  courseId: number;
  courseTitle: string;
  progressPercent: number;
  status: string;
  enrolledAt: string;
}

export interface Student {
  id: number;
  name: string;
  email: string;
  createdAt: string;
  profile: StudentProfile | null;
  enrollments: Enrollment[];
}

export interface MentorSimple { id: number; name: string; }

export interface Course {
  id: number;
  title: string;
  fee: number;
  active: boolean;
  mentors: MentorSimple[];
}

export interface Skill { id: number; code: string; displayName: string; }

export interface Mentor {
  id: number;
  name: string;
  expertiseLevel: string;
  courses: { id: number; title: string }[];
  skills: Skill[];
}

export interface SqlTrace {
  timestamp: string;
  requestId: string;
  method: string;
  path: string;
  operation: string;
  sql: string;
}

export interface FetchComparison {
  scenario: string;
  mode: string;
  queryCount: number;
  selectCount: number;
  durationMs: number;
  data: unknown;
}