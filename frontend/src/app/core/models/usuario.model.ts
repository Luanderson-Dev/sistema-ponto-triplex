export interface CriarUsuarioRequest {
  nome: string;
  email: string;
  senha: string;
  role: 'RH' | 'USER';
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  role: string;
}
