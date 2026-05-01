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
      <div class="mb-8">
        <div class="podium-grid">
          @if (entryByPosicao(2); as segundo) {
            <div class="podium-card podium-second podium-box podium-box-second">
              <div class="podium-frame podium-frame-second">
                <img src="/SecondPlace.png" alt="Segundo lugar" class="podium-base" />
                @if (segundo.avatarUrl) {
                  <img [src]="segundo.avatarUrl" [alt]="segundo.nomeUsuario" class="podium-avatar podium-avatar-second" />
                } @else {
                  <div class="podium-avatar podium-avatar-second podium-avatar-fallback">
                    {{ segundo.nomeUsuario.charAt(0).toUpperCase() }}
                  </div>
                }
              </div>
              <p class="podium-name">{{ segundo.nomeUsuario }}</p>
              <p class="podium-time">{{ formatarHoras(segundo.totalMinutos) }}</p>
            </div>
          }

          @if (entryByPosicao(1); as primeiro) {
            <div class="podium-card podium-first podium-box podium-box-first">
              <div class="podium-frame podium-frame-first">
                <img src="/FirstPlace.png" alt="Primeiro lugar" class="podium-base" />
                @if (primeiro.avatarUrl) {
                  <img [src]="primeiro.avatarUrl" [alt]="primeiro.nomeUsuario" class="podium-avatar podium-avatar-first" />
                } @else {
                  <div class="podium-avatar podium-avatar-first podium-avatar-fallback">
                    {{ primeiro.nomeUsuario.charAt(0).toUpperCase() }}
                  </div>
                }
              </div>
              <p class="podium-name">{{ primeiro.nomeUsuario }}</p>
              <p class="podium-time">{{ formatarHoras(primeiro.totalMinutos) }}</p>
            </div>
          }

          @if (entryByPosicao(3); as terceiro) {
            <div class="podium-card podium-third podium-box podium-box-third">
              <div class="podium-frame podium-frame-third">
                <img src="/ThirdPlace.png" alt="Terceiro lugar" class="podium-base" />
                @if (terceiro.avatarUrl) {
                  <img [src]="terceiro.avatarUrl" [alt]="terceiro.nomeUsuario" class="podium-avatar podium-avatar-third" />
                } @else {
                  <div class="podium-avatar podium-avatar-third podium-avatar-fallback">
                    {{ terceiro.nomeUsuario.charAt(0).toUpperCase() }}
                  </div>
                }
              </div>
              <p class="podium-name">{{ terceiro.nomeUsuario }}</p>
              <p class="podium-time">{{ formatarHoras(terceiro.totalMinutos) }}</p>
            </div>
          }
        </div>
      </div>

      <div class="space-y-3">
        @for (entry of entradas(); track entry.posicao) {
          @if (entry.posicao > 3) {
          <div class="bg-white rounded-lg shadow px-5 py-4 flex items-center gap-4">

            <span class="text-2xl font-bold w-10 text-center text-gray-500">
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
        }
      </div>
    }
  `,
  styles: `
    .podium-grid {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      align-items: end;
      gap: 1rem;
      max-width: 56rem;
      margin: 0 auto;
    }

    .podium-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0.75rem 0.5rem 1rem;
      border-radius: 0.75rem;
      background: #ffffff;
    }

    .podium-box-first {
      border: 2px solid #facc15;
    }

    .podium-box-second {
      border: 2px solid #d1d5db;
    }

    .podium-box-third {
      border: 2px solid #d97706;
    }

    .podium-first {
      transform: scale(1);
    }

    .podium-second {
      transform: scale(0.88);
    }

    .podium-third {
      transform: scale(0.8);
    }

    .podium-frame {
      position: relative;
      width: 170px;
      height: 170px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .podium-base {
      width: 170px;
      height: 170px;
      object-fit: contain;
    }

    .podium-avatar {
      position: absolute;
      width: 74px;
      height: 74px;
      border-radius: 9999px;
      object-fit: cover;
      top: 37px;
      left: 48px;
      background: #22c55e;
    }

    .podium-avatar-first {
      top: 44px;
    }

    .podium-avatar-second {
      width: 80px;
      height: 80px;
      top: 34px;
      left: 45px;
    }

    .podium-avatar-third {
      top: 38px;
    }

    .podium-avatar-fallback {
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      color: #111827;
      font-size: 1.25rem;
    }

    .podium-name {
      margin-top: 0.25rem;
      font-size: 1.125rem;
      font-weight: 500;
      color: #111827;
      text-align: center;
    }

    .podium-time {
      margin-top: 0.125rem;
      font-size: 0.95rem;
      font-weight: 700;
      color: #374151;
      text-align: center;
    }

    @media (max-width: 900px) {
      .podium-grid {
        gap: 0.25rem;
      }

      .podium-frame {
        width: 130px;
        height: 130px;
      }

      .podium-base {
        width: 130px;
        height: 130px;
      }

      .podium-avatar {
        width: 56px;
        height: 56px;
        top: 30px;
        left: 37px;
      }

      .podium-avatar-first {
        top: 34px;
      }

      .podium-avatar-second {
        width: 62px;
        height: 62px;
        top: 28px;
        left: 34px;
      }

      .podium-avatar-third {
        top: 30px;
      }

      .podium-name {
        font-size: 1rem;
      }

      .podium-time {
        font-size: 0.875rem;
      }
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

  entryByPosicao(posicao: number): LeaderboardEntryResponse | undefined {
    return this.entradas().find((entry) => entry.posicao === posicao);
  }
}
