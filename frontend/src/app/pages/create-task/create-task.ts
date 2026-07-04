import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { Task } from '../../models/task.model';

// Tipo do payload emitido pro pai - tarefa sem id (ainda não existe no banco)
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

  // Estado de abertura dos dropdowns customizados (substituem o <select> nativo)
  isPriorityOpen = signal(false);
  isStatusOpen = signal(false);

  form: FormGroup = this.fb.group({
    assignee: ['', [Validators.required, Validators.maxLength(25)]],
    title: ['', [Validators.required, Validators.maxLength(30)]],
    description: ['', [Validators.required, Validators.maxLength(100)]],
    priority: ['', [Validators.required]],
    status: ['', [Validators.required]],
  });

  // Facilita checar erro no template: só mostra se o campo foi tocado E é inválido
  hasError(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  // ----- Custom select: PRIORITY -----

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

  // ----- Custom select: STATUS -----

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

  // Fecha qualquer dropdown aberto ao clicar em outra parte do card
  closeDropdowns(): void {
    this.isPriorityOpen.set(false);
    this.isStatusOpen.set(false);
  }

  onSave(): void {
    if (this.form.invalid) {
      // marca todos os campos como touched pra mostrar os erros de uma vez
      // caso a pessoa clique em Salvar sem preencher nada
      this.form.markAllAsTouched();
      return;
    }

    this.save.emit(this.form.value as NewTaskPayload);
  }

  onCancel(): void {
    this.cancel.emit();
  }

  // clique no overlay (fora do card) também cancela, exceto se for dentro do card
  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onCancel();
    }
  }
}