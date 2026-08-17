import { request } from './client';
import { AuthResponse, LoginRequest } from './types';

export async function login(credentials: LoginRequest): Promise<AuthResponse> {
  const response = await request<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
  localStorage.setItem('token', response.token);
  return response;
}

export function logout(): void {
  localStorage.removeItem('token');
}
