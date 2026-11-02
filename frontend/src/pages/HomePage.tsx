import React, { useState } from 'react';
import { Card, CardHeader, CardBody, Tabs, Tab } from '@nextui-org/react';
import PostList from '../components/PostList';
import { usePosts } from '../hooks/usePosts';
import { useCategories } from '../hooks/useCategories';
import { useTags } from '../hooks/useTags';

const PAGE_SIZE = 10;

const HomePage: React.FC = () => {
  const [page, setPage] = useState(1); // 1-based in the UI
  const [sortBy, setSortBy] = useState('createdAt,desc');
  const [selectedCategory, setSelectedCategory] = useState<string | undefined>(
    undefined,
  );
  const [selectedTag, setSelectedTag] = useState<string | undefined>(undefined);

  const {
    data: postsPage,
    isLoading: postsLoading,
    error: postsError,
  } = usePosts({
    categoryId: selectedCategory,
    tagId: selectedTag,
    page: page - 1, // Spring pages are 0-based
    size: PAGE_SIZE,
    sort: sortBy,
  });
  const { data: categories = [] } = useCategories();
  const { data: tags = [] } = useTags();

  const loading = postsLoading;
  const error = postsError
    ? 'Failed to load content. Please try again later.'
    : null;

  const handleCategoryChange = (categoryId: string | undefined) => {
    setSelectedCategory('all' === categoryId ? undefined : categoryId);
    setPage(1); // filter changed -> back to the first page
  };

  const handleTagClick = (tagId: string) => {
    setSelectedTag(selectedTag === tagId ? undefined : tagId);
    setPage(1);
  };

  const handleSortChange = (newSort: string) => {
    setSortBy(newSort);
    setPage(1);
  };

  return (
    <div className="max-w-6xl mx-auto px-4 space-y-6">
      <Card className="mb-6 px-2">
        <CardHeader>
          <h1 className="text-2xl font-bold">Blog Posts</h1>
        </CardHeader>
        <CardBody>
          <div className="flex flex-col gap-4">
            <Tabs
              selectedKey={selectedCategory ?? 'all'}
              onSelectionChange={(key) => {
                handleCategoryChange(key as string);
              }}
              variant="underlined"
              classNames={{
                tabList: 'gap-6',
                cursor: 'w-full bg-primary',
              }}
            >
              <Tab key="all" title="All Posts" />
              {categories.map((category) => (
                <Tab
                  key={category.id}
                  title={`${category.name} (${category.postCount})`}
                />
              ))}
            </Tabs>

            {tags.length > 0 && (
              <div className="flex gap-2 flex-wrap">
                {tags.map((tag) => (
                  <button
                    key={tag.id}
                    onClick={() => handleTagClick(tag.id)}
                    className={`px-3 py-1 rounded-full text-sm ${
                      selectedTag === tag.id
                        ? 'bg-primary text-white'
                        : 'bg-default-100 hover:bg-default-200'
                    }`}
                  >
                    {tag.name} ({tag.postCount})
                  </button>
                ))}
              </div>
            )}
          </div>
        </CardBody>
      </Card>

      <PostList
        posts={postsPage?.content ?? null}
        totalPages={postsPage?.totalPages ?? 0}
        loading={loading}
        error={error}
        page={page}
        sortBy={sortBy}
        onPageChange={setPage}
        onSortChange={handleSortChange}
      />
    </div>
  );
};

export default HomePage;
