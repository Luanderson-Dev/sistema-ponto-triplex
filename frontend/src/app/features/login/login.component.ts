import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
      <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h1 class="text-2xl font-bold text-center text-gray-800 mb-6">Ponto da Triplex</h1>

        @if (erro()) {
          <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {{ erro() }}
          </div>
        }

        <form (ngSubmit)="entrar()" #loginForm="ngForm">
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
            <input
              id="email"
              type="email"
              [(ngModel)]="email"
              name="email"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="seu@email.com"
            />
          </div>

          <div class="mb-6">
            <label for="senha" class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
            <input
              id="senha"
              type="password"
              [(ngModel)]="senha"
              name="senha"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="••••••"
            />
          </div>

          <button
            type="submit"
            [disabled]="carregando()"
            class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            @if (carregando()) {
              Entrando...
            } @else {
              Entrar
            }
          </button>
        </form>
      </div>
    </div>
  `,
})
export class LoginComponent {
  email = '';
  senha = '';
  erro = signal('');
  carregando = signal(false);

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  entrar(): void {
    if (!this.email || !this.senha) return;

    this.carregando.set(true);
    this.erro.set('');

    this.auth.login({ email: this.email, senha: this.senha }).subscribe({
      next: () => {
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        this.carregando.set(false);
        this.erro.set(err.status === 401 ? 'E-mail ou senha incorretos.' : 'Erro ao conectar com o servidor.');
      },
    });
  }
}
