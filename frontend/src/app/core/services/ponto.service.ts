import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PontoAbertoResponse, RegistroPontoResponse } from '../models/ponto.model';

@Injectable({ providedIn: 'root' })
export class PontoService {
  private readonly apiUrl = '/api/pontos';

  constructor(private http: HttpClient) {}

  registrarEntrada(): Observable<RegistroPontoResponse> {
    return this.http.post<RegistroPontoResponse>(`${this.apiUrl}/entrada`, null);
  }

  registrarSaida(): Observable<RegistroPontoResponse> {
    return this.http.post<RegistroPontoResponse>(`${this.apiUrl}/saida`, null);
  }

  verificarAberto(): Observable<PontoAbertoResponse> {
    return this.http.get<PontoAbertoResponse>(`${this.apiUrl}/aberto`);
  }

  listarMeus(): Observable<RegistroPontoResponse[]> {
    return this.http.get<RegistroPontoResponse[]>(`${this.apiUrl}/meus`);
  }

  listarAdmin(usuarioId: number, dataInicio: string, dataFim: string): Observable<RegistroPontoResponse[]> {
    const params = new HttpParams()
      .set('usuarioId', usuarioId)
      .set('dataInicio', dataInicio)
      .set('dataFim', dataFim);
    return this.http.get<RegistroPontoResponse[]>(`${this.apiUrl}/admin`, { params });
  }
}
