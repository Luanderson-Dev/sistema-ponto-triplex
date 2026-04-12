import { Component, OnInit, signal } from '@angular/core';
import { PontoService } from '../../../core/services/ponto.service';
import { LeaderboardEntryResponse } from '../../../core/models/ponto.model';

@Component({
  selector: 'app-leaderboard',
  template: `
    <h1 class="text-2xl font-bold text-gray-800 mb-6">Leaderboard</h1>

    @if (carregando()) {
      <div class="text-center text-gray-500 py-8">Carregando...</div>
    } @else if (entradas().length === 0) {
      <div class="text-center text-gray-500 py-8">Nenhum registro encontrado.</div>
    } @else {
      <div class="space-y-3">
        @for (entry of entradas(); track entry.posicao) {
          <div class="bg-white rounded-lg shadow px-5 py-4 flex items-center gap-4"
               [class.ring-2]="entry.posicao <= 3"
               [class.ring-yellow-400]="entry.posicao === 1"
               [class.ring-gray-300]="entry.posicao === 2"
               [class.ring-amber-600]="entry.posicao === 3">

            <span class="text-2xl font-bold w-10 text-center"
                  [class.text-yellow-500]="entry.posicao === 1"
                  [class.text-gray-400]="entry.posicao === 2"
                  [class.text-amber-600]="entry.posicao === 3"
                  [class.text-gray-500]="entry.posicao > 3">
              {{ entry.posicao }}
            </span>

            @if (entry.avatarUrl) {
              <img [src]="entry.avatarUrl" [alt]="entry.nomeUsuario"
                   class="w-10 h-10 rounded-full object-cover" />
            } @else {
              <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-500 font-bold text-sm">
                {{ entry.nomeUsuario.charAt(0).toUpperCase() }}
              </div>
            }

            <div class="flex-1 min-w-0">
              <p class="font-semibold text-gray-800 truncate">{{ entry.nomeUsuario }}</p>
            </div>

            <div class="text-right">
              <p class="font-bold text-gray-700">{{ formatarHoras(entry.totalMinutos) }}</p>
            </div>
          </div>
        }
      </div>
    }
  `,
})
export class LeaderboardComponent implements OnInit {
  entradas = signal<LeaderboardEntryResponse[]>([]);
  carregando = signal(true);

  constructor(private pontoService: PontoService) {}

  ngOnInit(): void {
    this.pontoService.listarLeaderboard().subscribe({
      next: (data) => {
        this.entradas.set(data);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }

  formatarHoras(minutos: number): string {
    const h = Math.floor(minutos / 60);
    const m = Math.round(minutos % 60);
    return `${h}h ${m}min`;
  }
}
