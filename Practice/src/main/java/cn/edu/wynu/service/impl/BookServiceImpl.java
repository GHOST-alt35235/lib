package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.common.HttpStatus;
import cn.edu.wynu.mapper.BookMapper;
import cn.edu.wynu.mapper.BookStockLogMapper;
import cn.edu.wynu.model.dto.BookDTO;
import cn.edu.wynu.model.dto.BookImportDTO;
import cn.edu.wynu.model.dto.BookStockDTO;
import cn.edu.wynu.model.entity.Book;
import cn.edu.wynu.model.entity.BookStockLog;
import cn.edu.wynu.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookStockLogMapper stockLogMapper;

    @Override
    public AjaxResult getBookList(Book book) {
        List<Book> books;
        if (book == null || (book.getBookName() == null && book.getAuthor() == null
                && book.getCategoryId() == null && book.getStatus() == null)) {
            books = bookMapper.selectAll();
        } else {
            books = bookMapper.selectByCondition(book);
        }
        return AjaxResult.success("查询成功", books);
    }

    @Override
    public AjaxResult getBookById(Integer id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return AjaxResult.error("图书不存在！");
        }
        return AjaxResult.success("查询成功", book);
    }

    @Override
    @Transactional
    public AjaxResult addBook(BookDTO bookDTO) {
        if (bookDTO.getBookName() == null) {
            return AjaxResult.error("书名不能为空！");
        }

        if (bookDTO.getIsbn() != null) {
            Book existBook = bookMapper.selectByIsbn(bookDTO.getIsbn());
            if (existBook != null) {
                return AjaxResult.error("ISBN已存在！");
            }
        }

        Book book = new Book();
        book.setIsbn(bookDTO.getIsbn());
        book.setBookName(bookDTO.getBookName());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishDate(bookDTO.getPublishDate());
        book.setCategoryId(bookDTO.getCategoryId());
        book.setCoverUrl(bookDTO.getCoverUrl());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCount(bookDTO.getTotalCount() != null ? bookDTO.getTotalCount() : 0);
        book.setAvailableCount(bookDTO.getAvailableCount() != null ? bookDTO.getAvailableCount() : bookDTO.getTotalCount());
        book.setPrice(bookDTO.getPrice());
        book.setLocation(bookDTO.getLocation());
        book.setStatus(bookDTO.getStatus() != null ? bookDTO.getStatus() : 1);

        int result = bookMapper.insert(book);
        if (result > 0) {
            return new AjaxResult(HttpStatus.CREATED, "添加成功", book.getId());
        }
        return AjaxResult.error("添加失败！");
    }

    @Override
    @Transactional
    public AjaxResult updateBook(BookDTO bookDTO) {
        if (bookDTO.getId() == null) {
            return AjaxResult.error("图书ID不能为空！");
        }

        Book existBook = bookMapper.selectById(bookDTO.getId());
        if (existBook == null) {
            return AjaxResult.error("图书不存在！");
        }

        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().equals(existBook.getIsbn())) {
            Book isbnBook = bookMapper.selectByIsbn(bookDTO.getIsbn());
            if (isbnBook != null) {
                return AjaxResult.error("ISBN已存在！");
            }
        }

        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setIsbn(bookDTO.getIsbn());
        book.setBookName(bookDTO.getBookName());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishDate(bookDTO.getPublishDate());
        book.setCategoryId(bookDTO.getCategoryId());
        book.setCoverUrl(bookDTO.getCoverUrl());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCount(bookDTO.getTotalCount());
        book.setPrice(bookDTO.getPrice());
        book.setLocation(bookDTO.getLocation());
        book.setStatus(bookDTO.getStatus() != null ? bookDTO.getStatus() : 1);

        if (bookDTO.getAvailableCount() == null) {
            int delta = book.getTotalCount() - existBook.getTotalCount();
            book.setAvailableCount(existBook.getAvailableCount() + delta);
        } else {
            book.setAvailableCount(bookDTO.getAvailableCount());
        }

        int result = bookMapper.update(book);
        if (result > 0) {
            return AjaxResult.success("更新成功");
        }
        return AjaxResult.error("更新失败！");
    }

    @Override
    @Transactional
    public AjaxResult deleteBook(Integer id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return AjaxResult.error("图书不存在！");
        }

        int result = bookMapper.deleteById(id);
        if (result > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败！");
    }

    @Override
    @Transactional
    public AjaxResult updateStatus(Integer id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return AjaxResult.error("图书不存在！");
        }

        book.setStatus(status);
        int result = bookMapper.update(book);
        if (result > 0) {
            return AjaxResult.success("状态更新成功");
        }
        return AjaxResult.error("状态更新失败！");
    }

    @Override
    public long getTotalBookCount() {
        return bookMapper.count();
    }

    @Override
    @Transactional
    public AjaxResult adjustStock(BookStockDTO stockDTO) {
        Book book = bookMapper.selectById(stockDTO.getBookId());
        if (book == null) {
            return AjaxResult.error("图书不存在！");
        }

        int beforeCount = book.getTotalCount();
        int afterCount = beforeCount + stockDTO.getAdjustCount();
        
        if (afterCount < 0) {
            return AjaxResult.error("调整后库存不能为负数！");
        }

        book.setTotalCount(afterCount);
        if (book.getAvailableCount() != null) {
            book.setAvailableCount(book.getAvailableCount() + stockDTO.getAdjustCount());
        }
        bookMapper.update(book);

        int changeType = stockDTO.getAdjustCount() > 0 ? 1 : 2;
        BookStockLog log = new BookStockLog();
        log.setBookId(book.getId());
        log.setOperatorId((Integer) StpUtil.getLoginId());
        log.setChangeType(changeType);
        log.setChangeCount(Math.abs(stockDTO.getAdjustCount()));
        log.setBeforeCount(beforeCount);
        log.setAfterCount(afterCount);
        log.setRemark(stockDTO.getRemark());
        stockLogMapper.insert(log);

        return AjaxResult.success("库存调整成功");
    }

    @Override
    public AjaxResult getStockLogs(Integer bookId) {
        List<BookStockLog> logs;
        if (bookId != null) {
            logs = stockLogMapper.selectByBookId(bookId);
        } else {
            BookStockLog query = new BookStockLog();
            logs = stockLogMapper.selectByCondition(query);
        }
        return AjaxResult.success("查询成功", logs);
    }

    @Override
    @Transactional
    public AjaxResult batchImportBooks(List<BookImportDTO> bookList) {
        int successCount = 0;
        int failCount = 0;
        List<Map<String, Object>> failList = new ArrayList<>();

        for (BookImportDTO importDTO : bookList) {
            try {
                if (importDTO.getIsbn() != null) {
                    Book existBook = bookMapper.selectByIsbn(importDTO.getIsbn());
                    if (existBook != null) {
                        failCount++;
                        Map<String, Object> failItem = new HashMap<>();
                        failItem.put("book", importDTO);
                        failItem.put("reason", "ISBN已存在");
                        failList.add(failItem);
                        continue;
                    }
                }

                Book book = new Book();
                BeanUtils.copyProperties(importDTO, book);
                
                if (importDTO.getPublishDate() != null) {
                    try {
                        book.setPublishDate(LocalDate.parse(importDTO.getPublishDate()));
                    } catch (Exception e) {
                        book.setPublishDate(null);
                    }
                }
                
                book.setTotalCount(importDTO.getTotalCount() != null ? importDTO.getTotalCount() : 1);
                book.setAvailableCount(book.getTotalCount());
                book.setStatus(1);

                bookMapper.insert(book);
                successCount++;
            } catch (Exception e) {
                failCount++;
                Map<String, Object> failItem = new HashMap<>();
                failItem.put("book", importDTO);
                failItem.put("reason", e.getMessage());
                failList.add(failItem);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failList", failList);

        return AjaxResult.success("批量导入完成", result);
    }
}
