import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  template: `
    <h1 class="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-2">Bem-vindo, {{ auth.usuarioLogado()?.nome }}</h2>
        <p class="text-gray-500">Sistema de ponto eletrônico da Triplex.</p>
      </div>

      <a routerLink="/admin/ponto"
         class="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow block">
        <h2 class="text-lg font-semibold text-gray-700 mb-2">Meu Ponto</h2>
        <p class="text-gray-500">Registrar entrada e saída.</p>
      </a>

      @if (auth.eAdmin()) {
        <a routerLink="/admin/pontos"
           class="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow block">
          <h2 class="text-lg font-semibold text-gray-700 mb-2">Registros de Ponto</h2>
          <p class="text-gray-500">Consultar registros de todos os usuários.</p>
        </a>
        <a routerLink="/admin/usuarios"
           class="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow block">
          <h2 class="text-lg font-semibold text-gray-700 mb-2">Gerenciar Usuários</h2>
          <p class="text-gray-500">Cadastrar e visualizar usuários do sistema.</p>
        </a>
      }
    </div>
  `,
})
export class DashboardComponent {
  constructor(public auth: AuthService) {}
}
