package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.BookDTO;
import cn.edu.wynu.model.dto.BookImportDTO;
import cn.edu.wynu.model.dto.BookStockDTO;
import cn.edu.wynu.model.entity.Book;
import cn.edu.wynu.service.BookService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@SaCheckLogin
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/list")
    public AjaxResult getBookList(Book book) {
        return bookService.getBookList(book);
    }

    @GetMapping("/{id}")
    public AjaxResult getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @PostMapping("/add")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN", "TEACHER"})
    public AjaxResult addBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    @PutMapping("/update")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN", "TEACHER"})
    public AjaxResult updateBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(bookDTO);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN", "TEACHER"})
    public AjaxResult deleteBook(@PathVariable Integer id) {
        return bookService.deleteBook(id);
    }

    @PutMapping("/status/{id}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN", "TEACHER"})
    public AjaxResult updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        return bookService.updateStatus(id, status);
    }

    @PutMapping("/stock/adjust")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult adjustStock(@Valid @RequestBody BookStockDTO stockDTO) {
        return bookService.adjustStock(stockDTO);
    }

    @GetMapping("/stock/logs")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult getStockLogs(@RequestParam(required = false) Integer bookId) {
        return bookService.getStockLogs(bookId);
    }

    @PostMapping("/import")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult batchImportBooks(@Valid @RequestBody List<BookImportDTO> bookList) {
        return bookService.batchImportBooks(bookList);
    }
}
