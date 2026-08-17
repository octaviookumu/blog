import React, { useState } from 'react';
import {
  Card,
  CardHeader,
  CardBody,
  Button,
  Input,
  Table,
  TableHeader,
  TableBody,
  TableColumn,
  TableRow,
  TableCell,
  useDisclosure,
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Tooltip,
} from '@nextui-org/react';
import { Plus, Edit2, Trash2 } from 'lucide-react';
import { Category } from '../api/types';
import {
  useCategories,
  useCreateCategory,
  useUpdateCategory,
  useDeleteCategory,
} from '../hooks/useCategories';

interface CategoriesPageProps {
  isAuthenticated: boolean;
}

const CategoriesPage: React.FC<CategoriesPageProps> = ({ isAuthenticated }) => {
  const {
    data: categories = [],
    isLoading: loading,
    error: fetchError,
  } = useCategories();
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [newCategoryName, setNewCategoryName] = useState('');
  const { isOpen, onOpen, onClose } = useDisclosure();

  const createCategoryMutation = useCreateCategory();
  const updateCategoryMutation = useUpdateCategory();
  const deleteCategoryMutation = useDeleteCategory();

  const isSubmitting =
    createCategoryMutation.isPending || updateCategoryMutation.isPending;

  const error = fetchError
    ? 'Failed to load categories. Please try again later.'
    : createCategoryMutation.isError || updateCategoryMutation.isError
      ? `Failed to ${editingCategory ? 'update' : 'create'} category. Please try again.`
      : deleteCategoryMutation.isError
        ? 'Failed to delete category. Please try again.'
        : null;

  const handleAddEdit = async () => {
    if (!newCategoryName.trim()) {
      return;
    }

    try {
      if (editingCategory) {
        await updateCategoryMutation.mutateAsync({
          id: editingCategory.id,
          name: newCategoryName.trim(),
        });
      } else {
        await createCategoryMutation.mutateAsync(newCategoryName.trim());
      }
      handleModalClose();
    } catch (err) {
      // error is surfaced via the mutation's isError state above
    }
  };

  const handleDelete = async (category: Category) => {
    if (
      !window.confirm(
        `Are you sure you want to delete the category "${category.name}"?`,
      )
    ) {
      return;
    }

    try {
      await deleteCategoryMutation.mutateAsync(category.id);
    } catch (err) {
      // error is surfaced via deleteCategoryMutation.isError above
    }
  };

  const handleModalClose = () => {
    setEditingCategory(null);
    setNewCategoryName('');
    onClose();
  };

  const openEditModal = (category: Category) => {
    setEditingCategory(category);
    setNewCategoryName(category.name);
    onOpen();
  };

  const openAddModal = () => {
    setEditingCategory(null);
    setNewCategoryName('');
    onOpen();
  };

  return (
    <div className="max-w-4xl mx-auto px-4">
      <Card>
        <CardHeader className="flex justify-between items-center">
          <h1 className="text-2xl font-bold">Categories</h1>
          {isAuthenticated && (
            <Button
              color="primary"
              startContent={<Plus size={16} />}
              onClick={openAddModal}
            >
              Add Category
            </Button>
          )}
        </CardHeader>

        <CardBody>
          {error && (
            <div className="mb-4 p-4 text-red-500 bg-red-50 rounded-lg">
              {error}
            </div>
          )}

          <Table
            aria-label="Categories table"
            isHeaderSticky
            classNames={{
              wrapper: 'max-h-[600px]',
            }}
          >
            <TableHeader>
              <TableColumn>NAME</TableColumn>
              <TableColumn>POST COUNT</TableColumn>
              <TableColumn>ACTIONS</TableColumn>
            </TableHeader>
            <TableBody
              isLoading={loading}
              loadingContent={<div>Loading categories...</div>}
            >
              {categories.map((category) => (
                <TableRow key={category.id}>
                  <TableCell>{category.name}</TableCell>
                  <TableCell>{category.postCount || 0}</TableCell>
                  <TableCell>
                    {isAuthenticated ? (
                      <div className="flex gap-2">
                        <Button
                          isIconOnly
                          variant="flat"
                          size="sm"
                          onClick={() => openEditModal(category)}
                        >
                          <Edit2 size={16} />
                        </Button>
                        <Tooltip
                          content={
                            category.postCount
                              ? 'Cannot delete category with existing posts'
                              : 'Delete category'
                          }
                        >
                          <Button
                            isIconOnly
                            variant="flat"
                            color="danger"
                            size="sm"
                            onClick={() => handleDelete(category)}
                            isDisabled={
                              category?.postCount
                                ? category.postCount > 0
                                : false
                            }
                          >
                            <Trash2 size={16} />
                          </Button>
                        </Tooltip>
                      </div>
                    ) : (
                      <span>-</span>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardBody>
      </Card>

      <Modal isOpen={isOpen} onClose={handleModalClose}>
        <ModalContent>
          <ModalHeader>
            {editingCategory ? 'Edit Category' : 'Add Category'}
          </ModalHeader>
          <ModalBody>
            <Input
              label="Category Name"
              value={newCategoryName}
              onChange={(e) => setNewCategoryName(e.target.value)}
              isRequired
            />
          </ModalBody>
          <ModalFooter>
            <Button variant="flat" onClick={handleModalClose}>
              Cancel
            </Button>
            <Button
              color="primary"
              onClick={handleAddEdit}
              isLoading={isSubmitting}
            >
              {editingCategory ? 'Update' : 'Add'}
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </div>
  );
};

export default CategoriesPage;
