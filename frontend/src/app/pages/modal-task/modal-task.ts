import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { Task } from '../../models/task.model';

export type TaskUpdatePayload = Pick<Task, 'assignee' | 'title' | 'description' | 'priority' | 'status'>;

@Component({
  selector: 'app-modal-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modal-task.html',
  styleUrl: './modal-task.css',
})
export class ModalTask implements OnChanges {

  @Input({ required: true }) task!: Task;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<TaskUpdatePayload>();
  @Output() delete = new EventEmitter<number>();

  private fb = inject(FormBuilder);

  priorities: Task['priority'][] = ['LOW', 'MEDIUM', 'HIGH'];
  statuses: Task['status'][] = ['BACKLOG', 'ON_HOLD', 'IN_PROGRESS', 'DONE'];

  isEditMode = signal(false);
  isPriorityOpen = signal(false);
  isStatusOpen = signal(false);
  isConfirmDeleteOpen = signal(false);

  form: FormGroup = this.fb.group({
    assignee: ['', [Validators.required, Validators.maxLength(25)]],
    title: ['', [Validators.required, Validators.maxLength(30)]],
    description: ['', [Validators.required, Validators.maxLength(100)]],
    priority: ['', [Validators.required]],
    status: ['', [Validators.required]],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['task'] && this.task) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.form.reset({
      assignee: this.task.assignee,
      title: this.task.title,
      description: this.task.description,
      priority: this.task.priority,
      status: this.task.status,
    });
  }

  hasError(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  // Edit Mode

  enableEdit(): void {
    this.isEditMode.set(true);
  }

  cancelEdit(): void {
    this.resetForm();
    this.isEditMode.set(false);
    this.closeDropdowns();
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.save.emit(this.form.value as TaskUpdatePayload);
    this.isEditMode.set(false);
  }

  // Custom select: PRIORITY AND STATUS

  togglePriorityDropdown(event: Event): void {
    if (!this.isEditMode()) return;
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
    if (!this.isEditMode()) return;
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

  // Delete

  requestDelete(): void {
    this.isConfirmDeleteOpen.set(true);
  }

  confirmDelete(): void {
    if (!this.task.id) return;
    this.delete.emit(this.task.id);
    this.isConfirmDeleteOpen.set(false);
  }

  cancelDelete(): void {
    this.isConfirmDeleteOpen.set(false);
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget && !this.isEditMode()) {
      this.close.emit();
    }
  }
}