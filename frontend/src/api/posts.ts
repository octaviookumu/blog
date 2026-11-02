import { buildQueryString, request } from './client';
import {
  CreatePostRequest,
  Page,
  Post,
  PostListParams,
  UpdatePostRequest,
} from './types';

export const getPosts = (params: { categoryId?: string; tagId?: string }) =>
  request<Page<Post>>(`/posts${buildQueryString(params)}`);

export const getPost = (id: string) => request<Post>(`/posts/${id}`);

export const createPost = (post: CreatePostRequest) =>
  request<Post>('/posts', {
    method: 'POST',
    body: JSON.stringify(post),
  });

export const updatePost = (id: string, post: UpdatePostRequest) =>
  request<Post>(`/posts/${id}`, {
    method: 'PUT',
    body: JSON.stringify(post),
  });

export const deletePost = (id: string) =>
  request<void>(`/posts/${id}`, { method: 'DELETE' });

export const getDrafts = (
  params: Pick<PostListParams, 'page' | 'size' | 'sort'>,
) => request<Page<Post>>(`/posts/drafts${buildQueryString(params)}`);
