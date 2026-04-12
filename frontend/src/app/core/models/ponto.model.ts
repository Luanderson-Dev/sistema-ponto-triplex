export interface RegistroPontoResponse {
  id: number;
  usuarioId: number;
  nomeUsuario: string;
  email: string;
  horaEntrada: string;
  horaSaida: string | null;
  minutosTrabalhados: number;
}

export interface PontoAbertoResponse {
  aberto: boolean;
  horaEntrada: string | null;
}

export interface LeaderboardEntryResponse {
  posicao: number;
  nomeUsuario: string;
  avatarUrl: string | null;
  totalMinutos: number;
}
