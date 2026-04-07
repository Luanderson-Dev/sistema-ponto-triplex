import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CriarUsuarioRequest, UsuarioResponse } from '../../../core/models/usuario.model';
import { UsuarioService } from '../../../core/services/usuario.service';

@Component({
  selector: 'app-usuarios',
  imports: [FormsModule],
  template: `
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Usuários</h1>
      <button
        (click)="mostrarForm.set(!mostrarForm())"
        class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm"
      >
        @if (mostrarForm()) {
          Cancelar
        } @else {
          Novo Usuário
        }
      </button>
    </div>

    @if (mostrarForm()) {
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-4">Cadastrar Usuário</h2>

        @if (erro()) {
          <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {{ erro() }}
          </div>
        }

        @if (sucesso()) {
          <div class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4">
            {{ sucesso() }}
          </div>
        }

        <form (ngSubmit)="criar()" #form="ngForm">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nome</label>
              <input
                type="text"
                [(ngModel)]="novoUsuario.nome"
                name="nome"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
              <input
                type="email"
                [(ngModel)]="novoUsuario.email"
                name="email"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
              <input
                type="password"
                [(ngModel)]="novoUsuario.senha"
                name="senha"
                required
                minlength="6"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Perfil</label>
              <select
                [(ngModel)]="novoUsuario.role"
                name="role"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="USER">Usuário</option>
                <option value="RH">RH</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            [disabled]="salvando()"
            class="mt-4 bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 disabled:opacity-50 transition-colors text-sm"
          >
            @if (salvando()) {
              Salvando...
            } @else {
              Salvar
            }
          </button>
        </form>
      </div>
    }

    <div class="bg-white rounded-lg shadow overflow-hidden">
      @if (carregando()) {
        <div class="p-6 text-center text-gray-500">Carregando...</div>
      } @else if (usuarios().length === 0) {
        <div class="p-6 text-center text-gray-500">Nenhum usuário cadastrado.</div>
      } @else {
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nome</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">E-mail</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Perfil</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            @for (u of usuarios(); track u.id) {
              <tr class="hover:bg-gray-50">
                <td class="px-6 py-4 text-sm text-gray-500">{{ u.id }}</td>
                <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ u.nome }}</td>
                <td class="px-6 py-4 text-sm text-gray-500">{{ u.email }}</td>
                <td class="px-6 py-4">
                  <span
                    class="px-2 py-1 text-xs rounded-full"
                    [class]="u.role === 'RH' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'"
                  >
                    {{ u.role }}
                  </span>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </div>
  `,
})
export class UsuariosComponent implements OnInit {
  usuarios = signal<UsuarioResponse[]>([]);
  carregando = signal(true);
  mostrarForm = signal(false);
  salvando = signal(false);
  erro = signal('');
  sucesso = signal('');

  novoUsuario: CriarUsuarioRequest = {
    nome: '',
    email: '',
    senha: '',
    role: 'USER',
  };

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.usuarioService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
      },
    });
  }

  criar(): void {
    if (!this.novoUsuario.nome || !this.novoUsuario.email || !this.novoUsuario.senha) return;

    this.salvando.set(true);
    this.erro.set('');
    this.sucesso.set('');

    this.usuarioService.criar(this.novoUsuario).subscribe({
      next: () => {
        this.sucesso.set('Usuário criado com sucesso!');
        this.salvando.set(false);
        this.novoUsuario = { nome: '', email: '', senha: '', role: 'USER' };
        this.carregar();
      },
      error: (err) => {
        this.salvando.set(false);
        this.erro.set(err.error?.detail || 'Erro ao criar usuário.');
      },
    });
  }
}
