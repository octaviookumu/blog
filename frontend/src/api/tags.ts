import { request } from './client';
import { Tag } from './types';

export const getTags = () => request<Tag[]>('/tags');

export const createTags = (names: string[]) =>
  request<Tag[]>('/tags', {
    method: 'POST',
    body: JSON.stringify({ names }),
  });

export const deleteTag = (id: string) =>
  request<void>(`/tags/${id}`, { method: 'DELETE' });
