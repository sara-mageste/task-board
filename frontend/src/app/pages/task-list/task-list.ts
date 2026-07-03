import { Component, OnInit, OnDestroy, ViewChild, ElementRef, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-list.html',
  styleUrl: './task-list.css',
})
export class TaskList implements OnInit, OnDestroy {

  tasks = signal<Task[]>([]);

  backlogTasks = computed(() => this.tasks().filter(t => t.status === 'BACKLOG'));
  onHoldTasks = computed(() => this.tasks().filter(t => t.status === 'ON_HOLD'));
  inProgressTasks = computed(() => this.tasks().filter(t => t.status === 'IN_PROGRESS'));
  doneTasks = computed(() => this.tasks().filter(t => t.status === 'DONE'));

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
}