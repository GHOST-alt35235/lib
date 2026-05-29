package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.CategoryDTO;
import cn.edu.wynu.service.CategoryService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@SaCheckLogin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public AjaxResult getCategoryList() {
        return categoryService.getCategoryList();
    }

    @GetMapping("/tree")
    public AjaxResult getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/{id}")
    public AjaxResult getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/add")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.addCategory(categoryDTO);
    }

    @PutMapping("/update")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryDTO);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id);
    }
}
