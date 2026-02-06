package org.votingsystem.voting_system_for_award_nominations.service.em;

import org.springframework.stereotype.Service;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //  Create or update category
    public Category createCategory(Category category) {
        if (category.getEvent() == null) {
            throw new IllegalArgumentException("Category must be linked to an event");
        }
        return categoryRepository.save(category);
    }

    //  Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    //  Get categories for a specific event
    public List<Category> getCategoriesByEvent(Event event) {
        return categoryRepository.findByEvent(event);
    }

    //  Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    //  Delete category
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
