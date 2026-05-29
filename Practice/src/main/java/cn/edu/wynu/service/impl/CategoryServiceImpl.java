package cn.edu.wynu.service.impl;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.common.HttpStatus;
import cn.edu.wynu.mapper.CategoryMapper;
import cn.edu.wynu.model.dto.CategoryDTO;
import cn.edu.wynu.model.entity.Category;
import cn.edu.wynu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public AjaxResult getCategoryList() {
        List<Category> categories = categoryMapper.selectAll();
        return AjaxResult.success("查询成功", categories);
    }

    @Override
    public AjaxResult getCategoryTree() {
        List<Category> allCategories = categoryMapper.selectAll();
        List<Category> rootCategories = new ArrayList<>();

        for (Category category : allCategories) {
            if (category.getParentId() == 0) {
                category.setChildren(getChildren(category.getId(), allCategories));
                rootCategories.add(category);
            }
        }
        return AjaxResult.success("查询成功", rootCategories);
    }

    private List<Category> getChildren(Integer parentId, List<Category> allCategories) {
        List<Category> children = new ArrayList<>();
        for (Category category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                category.setChildren(getChildren(category.getId(), allCategories));
                children.add(category);
            }
        }
        return children;
    }

    @Override
    public AjaxResult getCategoryById(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return AjaxResult.error("分类不存在！");
        }
        return AjaxResult.success("查询成功", category);
    }

    @Override
    public AjaxResult addCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getCategoryName() == null) {
            return AjaxResult.error("分类名称不能为空！");
        }

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setParentId(categoryDTO.getParentId() != null ? categoryDTO.getParentId() : 0);
        category.setSortOrder(categoryDTO.getSortOrder() != null ? categoryDTO.getSortOrder() : 0);
        category.setDescription(categoryDTO.getDescription());
        category.setStatus(categoryDTO.getStatus() != null ? categoryDTO.getStatus() : 1);

        int result = categoryMapper.insert(category);
        if (result > 0) {
            return new AjaxResult(HttpStatus.CREATED, "添加成功", category.getId());
        }
        return AjaxResult.error("添加失败！");
    }

    @Override
    public AjaxResult updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            return AjaxResult.error("分类ID不能为空！");
        }

        Category existCategory = categoryMapper.selectById(categoryDTO.getId());
        if (existCategory == null) {
            return AjaxResult.error("分类不存在！");
        }

        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setParentId(categoryDTO.getParentId() != null ? categoryDTO.getParentId() : 0);
        category.setSortOrder(categoryDTO.getSortOrder());
        category.setDescription(categoryDTO.getDescription());
        category.setStatus(categoryDTO.getStatus() != null ? categoryDTO.getStatus() : 1);

        int result = categoryMapper.update(category);
        if (result > 0) {
            return AjaxResult.success("更新成功");
        }
        return AjaxResult.error("更新失败！");
    }

    @Override
    public AjaxResult deleteCategory(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return AjaxResult.error("分类不存在！");
        }

        List<Category> children = categoryMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            return AjaxResult.error("该分类下有子分类，不能删除！");
        }

        int result = categoryMapper.deleteById(id);
        if (result > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败！");
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getCategoryBookStats() {
        java.util.List<java.util.Map<String, Object>> stats = categoryMapper.getCategoryBookStats();
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> stat : stats) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("name", stat.get("category_name"));
            item.put("value", stat.get("book_count"));
            result.add(item);
        }
        return result;
    }
}
