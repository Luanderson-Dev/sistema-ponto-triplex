import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, UsuarioLogado } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly accessToken = signal<string | null>(null);
  private readonly usuario = signal<UsuarioLogado | null>(null);

  readonly estaAutenticado = computed(() => !!this.accessToken());
  readonly usuarioLogado = computed(() => this.usuario());
  readonly eAdmin = computed(() => this.usuario()?.role === 'ADMIN');

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  getAccessToken(): string | null {
    return this.accessToken();
  }

  login(credenciais: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`/auth/login`, credenciais, { withCredentials: true })
      .pipe(tap((res) => this.definirSessao(res)));
  }

  refresh(): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`/auth/refresh`, null, { withCredentials: true })
      .pipe(tap((res) => this.definirSessao(res)));
  }

  logout(): void {
    this.http.post(`/auth/logout`, null, { withCredentials: true }).subscribe({
      complete: () => this.limparSessao(),
      error: () => this.limparSessao(),
    });
  }

  private definirSessao(res: LoginResponse): void {
    this.accessToken.set(res.accessToken);
    this.usuario.set({
      nome: res.nomeUsuario,
      email: res.email,
      role: res.role,
    });
  }

  private limparSessao(): void {
    this.accessToken.set(null);
    this.usuario.set(null);
    this.router.navigate(['/login']);
  }
}
