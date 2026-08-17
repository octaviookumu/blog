import { request } from './client';
import { Category } from './types';

export const getCategories = () => request<Category[]>('/categories');

export const createCategory = (name: string) =>
  request<Category>('/categories', {
    method: 'POST',
    body: JSON.stringify({ name }),
  });

export const updateCategory = (id: string, name: string) =>
  request<Category>(`/categories/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ id, name }),
  });

export const deleteCategory = (id: string) =>
  request<void>(`/categories/${id}`, { method: 'DELETE' });
