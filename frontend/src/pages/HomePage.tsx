import React, { useState } from 'react';
import { Card, CardHeader, CardBody, Tabs, Tab } from '@nextui-org/react';
import PostList from '../components/PostList';
import { usePosts } from '../hooks/usePosts';
import { useCategories } from '../hooks/useCategories';
import { useTags } from '../hooks/useTags';

const HomePage: React.FC = () => {
  const [page, setPage] = useState(1);
  const [sortBy, setSortBy] = useState('createdAt,desc');
  const [selectedCategory, setSelectedCategory] = useState<string | undefined>(
    undefined,
  );
  const [selectedTag, setSelectedTag] = useState<string | undefined>(undefined);

  const {
    data: posts = null,
    isLoading: postsLoading,
    error: postsError,
  } = usePosts({
    categoryId: selectedCategory != undefined ? selectedCategory : undefined,
    tagId: selectedTag || undefined,
  });
  const { data: categories = [] } = useCategories();
  const { data: tags = [] } = useTags();

  const loading = postsLoading;
  const error = postsError
    ? 'Failed to load content. Please try again later.'
    : null;

  const handleCategoryChange = (categoryId: string | undefined) => {
    if ('all' === categoryId) {
      setSelectedCategory(undefined);
    } else {
      setSelectedCategory(categoryId);
    }
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
              selectedKey={selectedCategory}
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
                    onClick={() =>
                      setSelectedTag(selectedTag == tag.id ? undefined : tag.id)
                    }
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
        posts={posts}
        loading={loading}
        error={error}
        page={page}
        sortBy={sortBy}
        onPageChange={setPage}
        onSortChange={setSortBy}
      />
    </div>
  );
};

export default HomePage;
