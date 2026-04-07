import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { PontoService } from '../../core/services/ponto.service';
import { RegistroPontoResponse } from '../../core/models/ponto.model';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, RouterLink],
  template: `
    <div class="flex flex-col lg:flex-row gap-6">
      <!-- Coluna esquerda: Registros -->
      <div class="flex-1 min-w-0">
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Meus Registros</h1>

        <div class="bg-white rounded-lg shadow overflow-hidden">
          @if (carregando()) {
            <div class="p-6 text-center text-gray-500">Carregando...</div>
          } @else if (registros().length === 0) {
            <div class="p-6 text-center text-gray-500">Nenhum registro encontrado.</div>
          } @else {
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Data</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Entrada</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Saída</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Horas</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                @for (r of registros(); track r.id) {
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4 text-sm text-gray-900">{{ r.horaEntrada | date:'dd/MM/yyyy' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-600">{{ r.horaEntrada | date:'HH:mm:ss' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-600">
                      @if (r.horaSaida) {
                        {{ r.horaSaida | date:'HH:mm:ss' }}
                      } @else {
                        <span class="text-yellow-600 font-medium">Em aberto</span>
                      }
                    </td>
                    <td class="px-6 py-4 text-sm text-gray-600">
                      @if (r.minutosTrabalhados > 0) {
                        {{ formatarHoras(r.minutosTrabalhados) }}
                      } @else {
                        —
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>

        @if (auth.eAdmin()) {
          <h2 class="text-lg font-semibold text-gray-700 mt-8 mb-4">Administração</h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <a routerLink="/pontos"
               class="bg-white rounded-lg shadow p-5 hover:shadow-md transition-shadow block">
              <h3 class="font-semibold text-gray-700 mb-1">Registros de Ponto</h3>
              <p class="text-sm text-gray-500">Consultar registros de todos os usuários.</p>
            </a>
            <a routerLink="/usuarios"
               class="bg-white rounded-lg shadow p-5 hover:shadow-md transition-shadow block">
              <h3 class="font-semibold text-gray-700 mb-1">Gerenciar Usuários</h3>
              <p class="text-sm text-gray-500">Cadastrar e visualizar usuários do sistema.</p>
            </a>
          </div>
        }
      </div>

      <!-- Coluna direita: Card flutuante de ponto -->
      <div class="lg:w-80 flex-shrink-0">
        <div class="bg-white rounded-lg shadow p-6 text-center lg:sticky lg:top-6">
          <p class="text-4xl font-mono font-bold text-gray-800 mb-1">{{ horaAtual() }}</p>
          <p class="text-sm text-gray-500 mb-5">{{ dataAtual() }}</p>

          @if (erro()) {
            <div class="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded mb-4 text-sm">
              {{ erro() }}
            </div>
          }

          @if (sucesso()) {
            <div class="bg-green-50 border border-green-200 text-green-700 px-3 py-2 rounded mb-4 text-sm">
              {{ sucesso() }}
            </div>
          }

          @if (pontoAberto()) {
            <p class="text-sm text-gray-500 mb-3">
              Entrada às {{ horaEntradaAberta() | date:'HH:mm:ss' }}
            </p>
            <button
              (click)="registrarSaida()"
              [disabled]="processando()"
              class="w-full bg-red-600 text-white px-6 py-3 rounded-lg text-lg font-semibold hover:bg-red-700 disabled:opacity-50 transition-colors"
            >
              @if (processando()) { Processando... } @else { Registrar Saída }
            </button>
          } @else {
            <button
              (click)="registrarEntrada()"
              [disabled]="processando()"
              class="w-full bg-green-600 text-white px-6 py-3 rounded-lg text-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition-colors"
            >
              @if (processando()) { Processando... } @else { Registrar Entrada }
            </button>
          }

          <p class="text-xs text-gray-400 mt-4">
            Olá, {{ auth.usuarioLogado()?.nome }}
          </p>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent implements OnInit, OnDestroy {
  pontoAberto = signal(false);
  horaEntradaAberta = signal<string | null>(null);
  registros = signal<RegistroPontoResponse[]>([]);
  carregando = signal(true);
  processando = signal(false);
  erro = signal('');
  sucesso = signal('');
  horaAtual = signal('');
  dataAtual = signal('');

  private intervalo: ReturnType<typeof setInterval> | null = null;

  constructor(
    public auth: AuthService,
    private pontoService: PontoService,
  ) {}

  ngOnInit(): void {
    this.atualizarRelogio();
    this.intervalo = setInterval(() => this.atualizarRelogio(), 1000);
    this.carregarEstado();
    this.carregarRegistros();
  }

  ngOnDestroy(): void {
    if (this.intervalo) clearInterval(this.intervalo);
  }

  registrarEntrada(): void {
    this.processando.set(true);
    this.limparMensagens();
    this.pontoService.registrarEntrada().subscribe({
      next: () => {
        this.sucesso.set('Entrada registrada!');
        this.processando.set(false);
        this.carregarEstado();
        this.carregarRegistros();
      },
      error: (err) => {
        this.erro.set(err.error?.detail || 'Erro ao registrar entrada.');
        this.processando.set(false);
      },
    });
  }

  registrarSaida(): void {
    this.processando.set(true);
    this.limparMensagens();
    this.pontoService.registrarSaida().subscribe({
      next: () => {
        this.sucesso.set('Saída registrada!');
        this.processando.set(false);
        this.carregarEstado();
        this.carregarRegistros();
      },
      error: (err) => {
        this.erro.set(err.error?.detail || 'Erro ao registrar saída.');
        this.processando.set(false);
      },
    });
  }

  formatarHoras(minutos: number): string {
    const h = Math.floor(minutos / 60);
    const m = minutos % 60;
    return `${h}h ${m}min`;
  }

  private carregarEstado(): void {
    this.pontoService.verificarAberto().subscribe({
      next: (res) => {
        this.pontoAberto.set(res.aberto);
        this.horaEntradaAberta.set(res.horaEntrada);
      },
    });
  }

  private carregarRegistros(): void {
    this.carregando.set(true);
    this.pontoService.listarMeus().subscribe({
      next: (registros) => {
        this.registros.set(registros);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }

  private atualizarRelogio(): void {
    const agora = new Date();
    this.horaAtual.set(agora.toLocaleTimeString('pt-BR'));
    this.dataAtual.set(
      agora.toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }),
    );
  }

  private limparMensagens(): void {
    this.erro.set('');
    this.sucesso.set('');
  }
}
