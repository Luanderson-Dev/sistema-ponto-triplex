import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-perfil',
  imports: [FormsModule],
  template: `
    <h1 class="text-2xl font-bold text-gray-800 mb-6">Meu Perfil</h1>

    <div class="max-w-md">
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-4">Dados</h2>
        <div class="space-y-2 text-sm text-gray-600">
          <p><span class="font-medium text-gray-700">Nome:</span> {{ auth.usuarioLogado()?.nome }}</p>
          <p><span class="font-medium text-gray-700">E-mail:</span> {{ auth.usuarioLogado()?.email }}</p>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-4">Alterar Senha</h2>

        @if (erro()) {
          <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4 text-sm">
            {{ erro() }}
          </div>
        }

        @if (sucesso()) {
          <div class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4 text-sm">
            {{ sucesso() }}
          </div>
        }

        <form (ngSubmit)="alterarSenha()" class="space-y-4">
          <div>
            <label for="senhaAtual" class="block text-sm font-medium text-gray-700 mb-1">Senha atual</label>
            <input
              id="senhaAtual"
              type="password"
              [(ngModel)]="senhaAtual"
              name="senhaAtual"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div>
            <label for="novaSenha" class="block text-sm font-medium text-gray-700 mb-1">Nova senha</label>
            <input
              id="novaSenha"
              type="password"
              [(ngModel)]="novaSenha"
              name="novaSenha"
              required
              minlength="6"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div>
            <label for="confirmarSenha" class="block text-sm font-medium text-gray-700 mb-1">Confirmar nova senha</label>
            <input
              id="confirmarSenha"
              type="password"
              [(ngModel)]="confirmarSenha"
              name="confirmarSenha"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <button
            type="submit"
            [disabled]="processando()"
            class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            @if (processando()) { Alterando... } @else { Alterar Senha }
          </button>
        </form>
      </div>
    </div>
  `,
})
export class PerfilComponent {
  senhaAtual = '';
  novaSenha = '';
  confirmarSenha = '';
  processando = signal(false);
  erro = signal('');
  sucesso = signal('');

  constructor(
    public auth: AuthService,
    private http: HttpClient,
  ) {}

  alterarSenha(): void {
    this.erro.set('');
    this.sucesso.set('');

    if (this.novaSenha !== this.confirmarSenha) {
      this.erro.set('As senhas não coincidem.');
      return;
    }

    if (this.novaSenha.length < 6) {
      this.erro.set('A nova senha deve ter no mínimo 6 caracteres.');
      return;
    }

    this.processando.set(true);
    this.http
      .put('/api/perfil/senha', {
        senhaAtual: this.senhaAtual,
        novaSenha: this.novaSenha,
      })
      .subscribe({
        next: () => {
          this.sucesso.set('Senha alterada com sucesso!');
          this.senhaAtual = '';
          this.novaSenha = '';
          this.confirmarSenha = '';
          this.processando.set(false);
        },
        error: (err) => {
          this.erro.set(err.error?.detail || 'Erro ao alterar senha.');
          this.processando.set(false);
        },
      });
  }
}
