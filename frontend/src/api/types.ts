// Shared request/response shapes for the blog API.
// Kept separate from client.ts / the per-resource files so components can
// import types without pulling in fetch logic.

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  expiresIn: number;
}

export interface Category {
  id: string;
  name: string;
  postCount?: number;
}

export interface Tag {
  id: string;
  name: string;
  postCount?: number;
}

export enum PostStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
}

export interface Post {
  id: string;
  title: string;
  content: string;
  author?: {
    id: string;
    name: string;
  };
  category: Category;
  tags: Tag[];
  readingTime?: number;
  createdAt: string;
  updatedAt: string;
  status?: PostStatus;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  categoryId: string;
  tagIds: string[];
  status: PostStatus;
}

export interface UpdatePostRequest extends CreatePostRequest {
  id: string;
}

// Shape the backend is expected to return in the response body on error.
// `request()` in client.ts normalizes whatever comes back into this shape.
export interface ApiError {
  status: number;
  message: string;
  errors?: Array<{
    field: string;
    message: string;
  }>;
}

// Mirrors Spring Data's Page<T> JSON
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page, 0-based
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PostListParams {
  categoryId?: string;
  tagId?: string;
  page?: number; // 0-based, sent straight to Spring
  size?: number;
  sort?: string; // e.g. 'createdAt,desc'
}
