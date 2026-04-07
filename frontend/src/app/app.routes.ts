import { Routes } from '@angular/router';
import { adminGuard, authGuard, guestGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'auth/discord/callback',
    loadComponent: () =>
      import('./features/login/discord-callback.component').then(
        (m) => m.DiscordCallbackComponent,
      ),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/admin/admin-layout.component').then((m) => m.AdminLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/admin/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'usuarios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/usuarios/usuarios.component').then((m) => m.UsuariosComponent),
      },
      {
        path: 'pontos',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/ponto/admin-pontos.component').then((m) => m.AdminPontosComponent),
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/admin/perfil/perfil.component').then((m) => m.PerfilComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
