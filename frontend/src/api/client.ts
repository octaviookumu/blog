import { ApiError } from './types';

// Same env-var convention as Vite: set VITE_API_BASE_URL to point at a
// different backend (e.g. in .env.local). Falls back to a same-origin
// /api/v1 path, which is what a dev-server proxy or prod reverse proxy
// would usually serve.
const BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/+$/, '') ?? '/api/v1';

/**
 * Turns a params object into a leading-`?` query string, dropping any
 * undefined/null values. `undefined` shows up a lot here because pages pass
 * things like `{ categoryId: selectedCategory || undefined }`.
 *
 *   buildQueryString({ categoryId: '1', tagId: undefined })  -> '?categoryId=1'
 *   buildQueryString({})                                      -> ''
 */
export function buildQueryString(
  params?: Record<string, string | number | undefined>,
): string {
  if (!params) return '';

  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      search.append(key, String(value));
    }
  });

  const qs = search.toString();
  return qs ? `?${qs}` : '';
}

async function toApiError(res: Response): Promise<ApiError> {
  // Try to use whatever error body the backend sent (message, field errors,
  // etc). Fall back to a generic message if the body isn't JSON or is empty.
  try {
    const data = await res.json();
    if (data && typeof data === 'object') {
      return {
        status: res.status,
        message: `HTTP error! status: ${res.status}`,
        ...data,
      };
    }
  } catch {
    // Response body wasn't JSON (or was empty) - fall through.
  }
  return { status: res.status, message: `HTTP error! status: ${res.status}` };
}

/**
 * Thin wrapper around fetch() shared by every api/*.ts file. Handles:
 * - prefixing BASE_URL
 * - attaching the Bearer token from localStorage, if there is one
 * - JSON content-type header
 * - redirecting to /login on a 401 (mirrors the old axios interceptor)
 * - turning non-2xx responses into a thrown ApiError
 * - parsing the JSON body (or returning undefined for empty responses, e.g.
 *   204 No Content from a DELETE)
 */
export async function request<T>(
  url: string,
  options?: RequestInit,
): Promise<T> {
  const token = localStorage.getItem('token');

  const res = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options?.headers || {}),
    },
  });

  if (res.status === 401) {
    localStorage.removeItem('token');
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
    }
  }

  if (!res.ok) {
    throw await toApiError(res);
  }

  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}
