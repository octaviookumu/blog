import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as tagsApi from '../api/tags';
import { queryKeys } from './queryKeys';

export function useTags() {
  return useQuery({
    queryKey: queryKeys.tags.all,
    queryFn: tagsApi.getTags,
  });
}

export function useCreateTags() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: tagsApi.createTags,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.tags.all });
    },
  });
}

export function useDeleteTag() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: tagsApi.deleteTag,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.tags.all });
    },
  });
}
