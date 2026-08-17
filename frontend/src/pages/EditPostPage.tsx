import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardBody, CardHeader, Button } from '@nextui-org/react';
import { ArrowLeft } from 'lucide-react';
import { PostStatus } from '../api/types';
import PostForm from '../components/PostForm';
import { usePost, useCreatePost, useUpdatePost } from '../hooks/usePosts';
import { useCategories } from '../hooks/useCategories';
import { useTags } from '../hooks/useTags';

const EditPostPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const {
    data: post = null,
    isLoading: postLoading,
    error: postError,
  } = usePost(id);
  const {
    data: categories = [],
    isLoading: categoriesLoading,
    error: categoriesError,
  } = useCategories();
  const {
    data: tags = [],
    isLoading: tagsLoading,
    error: tagsError,
  } = useTags();

  const createPostMutation = useCreatePost();
  const updatePostMutation = useUpdatePost();

  const loading = (!!id && postLoading) || categoriesLoading || tagsLoading;
  const isSubmitting =
    createPostMutation.isPending || updatePostMutation.isPending;
  const error =
    postError || categoriesError || tagsError
      ? 'Failed to load necessary data. Please try again later.'
      : createPostMutation.isError || updatePostMutation.isError
        ? 'Failed to save the post. Please try again.'
        : null;

  const handleSubmit = async (postData: {
    title: string;
    content: string;
    categoryId: string;
    tagIds: string[];
    status: PostStatus;
  }) => {
    try {
      if (id) {
        await updatePostMutation.mutateAsync({
          id,
          post: { ...postData, id },
        });
      } else {
        await createPostMutation.mutateAsync(postData);
      }

      navigate('/');
    } catch (err) {
      // error is surfaced via the mutation's isError state above
    }
  };

  const handleCancel = () => {
    if (id) {
      navigate(`/posts/${id}`);
    } else {
      navigate('/');
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4">
        <Card className="w-full animate-pulse">
          <CardBody>
            <div className="h-8 bg-default-200 rounded w-3/4 mb-4"></div>
            <div className="space-y-3">
              <div className="h-4 bg-default-200 rounded w-full"></div>
              <div className="h-4 bg-default-200 rounded w-full"></div>
              <div className="h-4 bg-default-200 rounded w-2/3"></div>
            </div>
          </CardBody>
        </Card>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4">
      <Card className="w-full">
        <CardHeader className="flex justify-between items-center">
          <div className="flex items-center gap-4">
            <Button
              variant="flat"
              startContent={<ArrowLeft size={16} />}
              onClick={handleCancel}
            >
              Back
            </Button>
            <h1 className="text-2xl font-bold">
              {id ? 'Edit Post' : 'Create New Post'}
            </h1>
          </div>
        </CardHeader>

        <CardBody>
          {error && (
            <div className="mb-4 p-4 text-red-500 bg-red-50 rounded-lg">
              {error}
            </div>
          )}

          <PostForm
            initialPost={post}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            categories={categories}
            availableTags={tags}
            isSubmitting={isSubmitting}
          />
        </CardBody>
      </Card>
    </div>
  );
};

export default EditPostPage;
