package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.service.BookService;
import cn.edu.wynu.service.DistributeRecordService;
import cn.edu.wynu.service.CategoryService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@SaCheckLogin
public class StatisticsController {

    @Autowired
    private BookService bookService;

    @Autowired
    private DistributeRecordService recordService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/borrow-rate")
    public AjaxResult getBorrowRate() {
        Map<String, Object> result = new HashMap<>();
        
        long totalBooks = bookService.getTotalBookCount();
        long borrowedBooks = recordService.getBorrowedBookCount();
        
        double borrowRate = totalBooks > 0 ? (double) borrowedBooks / totalBooks * 100 : 0;
        
        result.put("totalBooks", totalBooks);
        result.put("borrowedBooks", borrowedBooks);
        result.put("borrowRate", Math.round(borrowRate * 100.0) / 100.0);
        
        return AjaxResult.success("查询成功", result);
    }

    @GetMapping("/category-stats")
    public AjaxResult getCategoryStats() {
        return AjaxResult.success("查询成功", categoryService.getCategoryBookStats());
    }

    @GetMapping("/monthly-borrow")
    public AjaxResult getMonthlyBorrowStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        int[] borrowCounts = recordService.getMonthlyBorrowCounts();
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", months[i]);
            item.put("count", borrowCounts[i]);
            result.add(item);
        }
        
        return AjaxResult.success("查询成功", result);
    }

    @GetMapping("/status-distribution")
    public AjaxResult getStatusDistribution() {
        return AjaxResult.success("查询成功", recordService.getStatusDistribution());
    }

    @GetMapping("/top-borrowed-books")
    public AjaxResult getTopBorrowedBooks() {
        return AjaxResult.success("查询成功", recordService.getTopBorrowedBooks(10));
    }
}
