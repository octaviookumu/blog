# Blog Frontend

A React + TypeScript + Vite app for a blog: browsing/creating posts, managing categories and tags, and a simple login flow. Data fetching goes through [TanStack Query](https://tanstack.com/query/latest) on top of a small `fetch`-based API layer.

## Folder layout

```
src/
├── api/          # fetch calls to the backend - no React here
│   ├── client.ts       # shared request() helper + query-string builder
│   ├── types.ts        # shared request/response types (Post, Category, Tag, ...)
│   ├── auth.ts          } one file per resource, each exporting
│   ├── posts.ts         } plain async functions (getPosts, createPost,
│   ├── categories.ts    } updatePost, ...). No React, no query/mutation
│   └── tags.ts           } logic - just "call the backend, return data".
│
├── hooks/        # TanStack Query wrappers around api/*.ts
│   ├── queryKeys.ts     # single source of truth for query keys
│   ├── usePosts.ts      # usePosts, usePost, useDrafts, useCreatePost, ...
│   ├── useCategories.ts
│   ├── useTags.ts
│   └── useLogin.ts
│
├── components/   # reusable UI (NavBar, PostList, PostForm, AuthContext)
├── pages/        # one component per route (HomePage, PostPage, ...)
├── App.tsx       # routes
└── main.tsx      # app entry point - sets up QueryClientProvider etc.
```

The rule of thumb: **`api/` talks to the backend, `hooks/` wires that into React Query, `pages/`/`components/` just call the hooks.** A page component never calls `fetch` or imports from `api/` directly - it calls `useCategories()` or `useCreatePost()` and gets back the familiar `{ data, isLoading, error }` / `{ mutate, isPending }` shape.

## The API layer (`src/api/`)

Every resource file (`posts.ts`, `categories.ts`, `tags.ts`, `auth.ts`) exports plain functions - no classes, no singletons:

```ts
// src/api/posts.ts
export const getPosts = (params: { categoryId?: string; tagId?: string }) =>
  request<Post[]>(`/posts${buildQueryString(params)}`);

export const createPost = (post: CreatePostRequest) =>
  request<Post>('/posts', {
    method: 'POST',
    body: JSON.stringify(post),
  });
```

`request<T>()` in `client.ts` is the one place that actually calls `fetch`. It:

- prefixes every URL with `VITE_API_BASE_URL` (defaults to `/api/v1`, matching the old backend proxy setup)
- attaches `Authorization: Bearer <token>` from `localStorage` automatically, so individual functions never touch auth headers
- redirects to `/login` and clears the stored token on a `401`
- throws a typed `ApiError` for any non-2xx response
- parses the JSON body for you, returning `undefined` for empty responses (e.g. a `DELETE` that returns `204 No Content`)

```ts
// src/api/client.ts (shape, trimmed)
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
    window.location.href = '/login';
  }

  if (!res.ok) throw await toApiError(res);

  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}
```

Because every `api/*.ts` function goes through `request()`, none of them need to worry about headers, auth, or error handling individually - they just describe the endpoint.

`buildQueryString()` handles the `?categoryId=...&tagId=...` style query params that `getPosts`/`getDrafts` use, skipping any `undefined` values (which shows up a lot since pages often pass things like `{ categoryId: selectedCategory || undefined }`).

### Backend URL

By default requests go to `/api/v1` on the same origin (e.g. behind a dev-server proxy or a prod reverse proxy). To point at a different backend, set `VITE_API_BASE_URL` in a `.env.local` file:

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## The hooks layer (`src/hooks/`)

Each hook file pairs one `api/*.ts` module with TanStack Query. Queries (`useQuery`) are for reading data; mutations (`useMutation`) are for creating/updating/deleting, and invalidate the relevant queries on success so the UI refetches automatically:

```ts
// src/hooks/usePosts.ts
export function usePosts(params: { categoryId?: string; tagId?: string }) {
  return useQuery({
    queryKey: queryKeys.posts.list(params),
    queryFn: () => postsApi.getPosts(params),
  });
}

export function useCreatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postsApi.createPost,
    onSuccess: () => {
      // refetches the post list(s) so the new post shows up without a manual reload
      queryClient.invalidateQueries({ queryKey: queryKeys.posts.all });
    },
  });
}
```

`queryKeys.ts` centralizes every query key (e.g. `['posts', 'list', { categoryId, tagId }]`) so the hook that _fetches_ data and the mutation that _invalidates_ it always agree on the key shape - a common source of "I updated the data but the UI didn't refresh" bugs.

### Using a hook in a page

```tsx
// inside a page component
const { data: posts = [], isLoading, error } = usePosts({ categoryId });
const createPostMutation = useCreatePost();

await createPostMutation.mutateAsync(newPost); // auto-refetches the post list
```

No `useEffect`, no manual `loading`/`error` state, no manual refetch after a mutation - React Query owns the request lifecycle and caching.

## Adding a new endpoint

1. Add the function to the relevant `api/*.ts` file (or create a new one for a new resource), calling `request<T>()`.
2. Add a `useQuery`/`useMutation` wrapper in the matching `hooks/*.ts` file. Add a key to `queryKeys.ts` if it's a query.
3. Call the hook from a page/component.

## Auth

`AuthContext` (`src/components/AuthContext.tsx`) holds the logged-in state (`isAuthenticated`, `token`, `user`) and exposes `login`/`logout`. The actual network call lives in `useLogin()` (a mutation wrapping `api/auth.ts#login`); `LoginPage` calls the mutation and, on success, hands the result to `AuthContext.login()` to update shared state. `api/auth.ts#login` persists the token to `localStorage`, and `client.ts` picks it up on every subsequent request - there's no separate step to "sync" the token onto an HTTP client instance.

---

# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react/README.md) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type aware lint rules:

- Configure the top-level `parserOptions` property like this:

```js
export default tseslint.config({
  languageOptions: {
    // other options...
    parserOptions: {
      project: ['./tsconfig.node.json', './tsconfig.app.json'],
      tsconfigRootDir: import.meta.dirname,
    },
  },
});
```

- Replace `tseslint.configs.recommended` to `tseslint.configs.recommendedTypeChecked` or `tseslint.configs.strictTypeChecked`
- Optionally add `...tseslint.configs.stylisticTypeChecked`
- Install [eslint-plugin-react](https://github.com/jsx-eslint/eslint-plugin-react) and update the config:

```js
// eslint.config.js
import react from 'eslint-plugin-react';

export default tseslint.config({
  // Set the react version
  settings: { react: { version: '18.3' } },
  plugins: {
    // Add the react plugin
    react,
  },
  rules: {
    // other rules...
    // Enable its recommended rules
    ...react.configs.recommended.rules,
    ...react.configs['jsx-runtime'].rules,
  },
});
```
