import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CriarUsuarioRequest, UsuarioResponse } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly apiUrl = '/api/usuarios';

  constructor(private http: HttpClient) {}

  criar(usuario: CriarUsuarioRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(this.apiUrl, usuario);
  }

  listar(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(this.apiUrl);
  }
}
