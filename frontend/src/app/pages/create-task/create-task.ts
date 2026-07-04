import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { Task } from '../../models/task.model';

export type NewTaskPayload = Omit<Task, 'id' | 'createdAt' | 'updatedAt'>;

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-task.html',
  styleUrl: './create-task.css',
})
export class CreateTask {

  @Output() save = new EventEmitter<NewTaskPayload>();
  @Output() cancel = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  priorities: Task['priority'][] = ['LOW', 'MEDIUM', 'HIGH'];
  statuses: Task['status'][] = ['BACKLOG', 'ON_HOLD', 'IN_PROGRESS', 'DONE'];

  isPriorityOpen = signal(false);
  isStatusOpen = signal(false);

  form: FormGroup = this.fb.group({
    assignee: ['', [Validators.required, Validators.maxLength(25)]],
    title: ['', [Validators.required, Validators.maxLength(30)]],
    description: ['', [Validators.required, Validators.maxLength(100)]],
    priority: ['', [Validators.required]],
    status: ['', [Validators.required]],
  });

  hasError(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  // Custom select: PRIORITY AND STATUS

  togglePriorityDropdown(event: Event): void {
    event.stopPropagation();
    this.isStatusOpen.set(false);
    this.isPriorityOpen.update(open => !open);
  }

  selectPriority(value: Task['priority']): void {
    this.form.get('priority')?.setValue(value);
    this.form.get('priority')?.markAsTouched();
    this.isPriorityOpen.set(false);
  }

  toggleStatusDropdown(event: Event): void {
    event.stopPropagation();
    this.isPriorityOpen.set(false);
    this.isStatusOpen.update(open => !open);
  }

  selectStatus(value: Task['status']): void {
    this.form.get('status')?.setValue(value);
    this.form.get('status')?.markAsTouched();
    this.isStatusOpen.set(false);
  }

  closeDropdowns(): void {
    this.isPriorityOpen.set(false);
    this.isStatusOpen.set(false);
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.save.emit(this.form.value as NewTaskPayload);
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onCancel();
    }
  }
}