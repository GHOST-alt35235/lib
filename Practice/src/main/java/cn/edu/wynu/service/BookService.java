package cn.edu.wynu.service;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.BookDTO;
import cn.edu.wynu.model.dto.BookImportDTO;
import cn.edu.wynu.model.dto.BookStockDTO;
import cn.edu.wynu.model.entity.Book;
import cn.edu.wynu.model.entity.BookStockLog;
import java.util.List;

public interface BookService {
    AjaxResult getBookList(Book book);
    AjaxResult getBookById(Integer id);
    AjaxResult addBook(BookDTO bookDTO);
    AjaxResult updateBook(BookDTO bookDTO);
    AjaxResult deleteBook(Integer id);
    AjaxResult updateStatus(Integer id, Integer status);
    long getTotalBookCount();
    
    AjaxResult adjustStock(BookStockDTO stockDTO);
    AjaxResult getStockLogs(Integer bookId);
    AjaxResult batchImportBooks(List<BookImportDTO> bookList);
}
