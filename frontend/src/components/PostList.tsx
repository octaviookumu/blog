import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Chip,
  Pagination,
} from '@nextui-org/react';
import { Post } from '../api/types';
import { Calendar, Clock, Tag } from 'lucide-react';
import DOMPurify from 'dompurify';

interface PostListProps {
  posts: Post[] | null;
  totalPages?: number;
  loading: boolean;
  error: string | null;
  page: number; // 1-based (UI); callers convert to 0-based for the API
  sortBy: string;
  onPageChange: (page: number) => void;
  onSortChange: (sortBy: string) => void;
}

const PostList: React.FC<PostListProps> = ({
  posts,
  totalPages = 0,
  loading,
  error,
  page,
  onPageChange,
}) => {
  const navigate = useNavigate();

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const createExcerpt = (content: string) => {
    // First sanitize the HTML
    const sanitizedContent = DOMPurify.sanitize(content, {
      ALLOWED_TAGS: ['p', 'strong', 'em', 'br'],
      ALLOWED_ATTR: [],
    });

    // Create a temporary div to parse the HTML
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = sanitizedContent;

    // Get the text content and limit it
    let textContent = tempDiv.textContent || tempDiv.innerText || '';
    textContent = textContent.trim();

    // Limit to roughly 200 characters, ending at the last complete word
    if (textContent.length > 200) {
      textContent =
        textContent.substring(0, 200).split(' ').slice(0, -1).join(' ') + '...';
    }

    return textContent;
  };

  if (error) {
    return <div className="p-4 text-red-500 bg-red-50 rounded-lg">{error}</div>;
  }

  const navToPostPage = (post: Post) => {
    navigate(`/posts/${post.id}`);
  };

  return (
    <div className="w-full space-y-6">
      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, index) => (
            <Card key={index} className="w-full animate-pulse">
              <CardBody>
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
              </CardBody>
            </Card>
          ))}
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {/*  if there are no posts OR the posts array is empty? */}
            {!posts || posts.length === 0 ? (
              <Card className="w-full">
                <CardBody className="text-center py-8">
                  <h3 className="mt-4 text-lg font-semibold">No posts</h3>
                  <p className="mt-2 text-sm text-default-500">
                    There are no posts to display yet.
                  </p>
                </CardBody>
              </Card>
            ) : (
              // else: Map through the posts and display them
              posts.map((post) => (
                <Card
                  key={post.id}
                  className="w-full p-2"
                  isPressable={true}
                  onPress={() => navToPostPage(post)}
                >
                  <CardHeader className="flex gap-3">
                    <div className="flex flex-col">
                      <h2 className="text-xl font-bold text-left">
                        {post.title}
                      </h2>
                      <p className="text-small text-default-500">
                        by {post.author?.name}
                      </p>
                    </div>
                  </CardHeader>
                  <CardBody>
                    <p className="line-clamp-3">
                      {createExcerpt(post.content)}
                    </p>
                  </CardBody>
                  <CardFooter className="flex flex-wrap gap-3">
                    <div className="flex items-center gap-1 text-small text-default-500">
                      <Calendar size={16} />
                      {formatDate(post.createdAt)}
                    </div>
                    <div className="flex items-center gap-1 text-small text-default-500">
                      <Clock size={16} />
                      {post.readingTime} min read
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Chip className="bg-primary-100 text-primary">
                        {post.category.name}
                      </Chip>
                      {post.tags.map((tag) => (
                        <Chip
                          key={tag.id}
                          className="bg-default-100"
                          startContent={<Tag size={14} />}
                        >
                          {tag.name}
                        </Chip>
                      ))}
                    </div>
                  </CardFooter>
                </Card>
              ))
            )}
          </div>

          {/* only show the pager when there's more than one page */}
          {totalPages > 1 && (
            <div className="flex justify-center pt-2">
              <Pagination
                total={totalPages}
                page={page}
                onChange={(p) => {
                  onPageChange(p);
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                showControls
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default PostList;
