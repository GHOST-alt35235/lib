package cn.edu.wynu.service;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.CategoryDTO;
import cn.edu.wynu.model.entity.Category;
import java.util.List;

public interface CategoryService {
    AjaxResult getCategoryList();
    AjaxResult getCategoryTree();
    AjaxResult getCategoryById(Integer id);
    AjaxResult addCategory(CategoryDTO categoryDTO);
    AjaxResult updateCategory(CategoryDTO categoryDTO);
    AjaxResult deleteCategory(Integer id);
    java.util.List<java.util.Map<String, Object>> getCategoryBookStats();
}
