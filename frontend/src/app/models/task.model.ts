export interface Task {
  id?: number;
  title: string;
  description: string;
  assignee: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'BACKLOG' | 'ON_HOLD' | 'IN_PROGRESS' | 'DONE';
  createdAt?: string;
  updatedAt?: string;
}