import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as postsApi from '../api/posts';
import { Post, UpdatePostRequest } from '../api/types';
import { queryKeys } from './queryKeys';

export function usePosts(params: { categoryId?: string; tagId?: string }) {
  return useQuery({
    queryKey: queryKeys.posts.list(params),
    queryFn: () => postsApi.getPosts(params),
  });
}

export function usePost(id: string | undefined) {
  return useQuery({
    queryKey: queryKeys.posts.detail(id ?? ''),
    queryFn: () => postsApi.getPost(id as string),
    enabled: !!id,
  });
}

export function useDrafts(params: {
  page?: number;
  size?: number;
  sort?: string;
}) {
  return useQuery({
    queryKey: queryKeys.posts.drafts(params),
    queryFn: () => postsApi.getDrafts(params),
  });
}

export function useCreatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postsApi.createPost,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.posts.all });
    },
  });
}

export function useUpdatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, post }: { id: string; post: UpdatePostRequest }) =>
      postsApi.updatePost(id, post),
    onSuccess: (updatedPost: Post) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.posts.all });
      queryClient.setQueryData(
        queryKeys.posts.detail(updatedPost.id),
        updatedPost,
      );
    },
  });
}

export function useDeletePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postsApi.deletePost,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.posts.all });
    },
  });
}
