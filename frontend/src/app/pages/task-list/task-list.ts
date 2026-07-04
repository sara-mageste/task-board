import { Component, OnInit, OnDestroy, ViewChild, ElementRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

type Status = Task['status'];

const STATUS_ORDER: Status[] = ['BACKLOG', 'ON_HOLD', 'IN_PROGRESS', 'DONE'];

interface ColumnConfig {
  status: Status;
  title: string;
  colorClass: string;
}

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-list.html',
  styleUrl: './task-list.css',
})
export class TaskList implements OnInit, OnDestroy {

  tasks = signal<Task[]>([]);

  columns: ColumnConfig[] = [
    { status: 'BACKLOG',     title: 'Backlog',     colorClass: 'col-backlog' },
    { status: 'ON_HOLD',     title: 'On Hold',     colorClass: 'col-onhold' },
    { status: 'IN_PROGRESS', title: 'In Progress', colorClass: 'col-progress' },
    { status: 'DONE',        title: 'Done',        colorClass: 'col-done' },
  ];

  searchTerm = '';
  isSearchOpen = false;

  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;

  private searchTerms = new Subject<string>();

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();

    this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        const trimmed = value.trim();

        if (!trimmed) {
          return this.taskService.getAll().pipe(catchError(() => of([] as Task[])));
        }
        if (/^\d+$/.test(trimmed)) {
          return this.taskService.getById(Number(trimmed)).pipe(catchError(() => of(null)));
        }
        return this.taskService.searchByTitle(trimmed).pipe(catchError(() => of([] as Task[])));
      })
    ).subscribe(result => {
      this.tasks.set(Array.isArray(result) ? result : (result ? [result] : []));
    });
  }

  ngOnDestroy(): void {
    this.searchTerms.complete();
  }

  loadTasks(): void {
    this.taskService.getAll().subscribe({
      next: (tasks) => this.tasks.set(tasks),
      error: (err) => console.error(err)
    });
  }

  search(value: string): void {
    this.searchTerm = value;
    this.searchTerms.next(value);
  }

  toggleSearch(): void {
    this.isSearchOpen = !this.isSearchOpen;
    if (this.isSearchOpen) {
      setTimeout(() => this.searchInput?.nativeElement.focus());
    }
  }

  tasksByStatus(status: Status): Task[] {
    return this.tasks().filter(t => t.status === status);
  }

  canGoBack(status: Status): boolean {
    return STATUS_ORDER.indexOf(status) > 0;
  }

  canGoForward(status: Status): boolean {
    return STATUS_ORDER.indexOf(status) < STATUS_ORDER.length - 1;
  }

  moveTask(task: Task, direction: -1 | 1): void {
    if (!task.id) return;

    const currentIndex = STATUS_ORDER.indexOf(task.status);
    const newIndex = currentIndex + direction;

    if (newIndex < 0 || newIndex >= STATUS_ORDER.length) return;

    const newStatus = STATUS_ORDER[newIndex];

    this.taskService.updateStatus(task.id, newStatus).subscribe({
      next: (updated) => {
        this.tasks.update(list => list.map(t => t.id === updated.id ? updated : t));
      },
      error: (err) => console.error('Erro ao mover tarefa:', err)
    });
  }

  openDetails(task: Task): void {
    console.log('abrir detalhes', task.id);
  }

  openCreateModal(): void {
    console.log('abrir modal de criação');
  }
}