// Centralized query key factory.
// Keeping all keys in one place avoids typos / mismatches between the
// hook that fetches data and the hook that invalidates it after a mutation.

export const queryKeys = {
  posts: {
    all: ['posts'] as const,
    list: (params: { categoryId?: string; tagId?: string }) =>
      ['posts', 'list', params] as const,
    detail: (id: string) => ['posts', 'detail', id] as const,
    drafts: (params: { page?: number; size?: number; sort?: string }) =>
      ['posts', 'drafts', params] as const,
  },
  categories: {
    all: ['categories'] as const,
  },
  tags: {
    all: ['tags'] as const,
  },
};
