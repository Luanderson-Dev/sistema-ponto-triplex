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
