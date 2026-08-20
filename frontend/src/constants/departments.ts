export const DEPARTMENTS = [
  'ASE',
  'Business Development',
  'CyberSecurity',
  'Dev',
  'Devops',
  'Engineering',
  'UI',
] as const;

export type DepartmentName = (typeof DEPARTMENTS)[number];

export const DEFAULT_DEPARTMENT: DepartmentName = 'Engineering';
