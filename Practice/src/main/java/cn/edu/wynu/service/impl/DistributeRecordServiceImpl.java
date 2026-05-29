package cn.edu.wynu.service.impl;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.common.HttpStatus;
import cn.edu.wynu.mapper.BookMapper;
import cn.edu.wynu.mapper.DistributeRecordMapper;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.dto.DistributeDTO;
import cn.edu.wynu.model.dto.RenewDTO;
import cn.edu.wynu.model.entity.Book;
import cn.edu.wynu.model.entity.DistributeRecord;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.DistributeRecordService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class DistributeRecordServiceImpl implements DistributeRecordService {

    @Autowired
    private DistributeRecordMapper recordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    private static final int DEFAULT_RENEW_DAYS = 30;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.5");

    @Override
    public AjaxResult getRecordList(DistributeRecord record) {
        java.util.List<DistributeRecord> records = recordMapper.selectByCondition(record);
        return AjaxResult.success("查询成功", records);
    }

    @Override
    public AjaxResult getRecordById(Integer id) {
        DistributeRecord record = recordMapper.selectById(id);
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }
        return AjaxResult.success("查询成功", record);
    }

    @Override
    @Transactional
    public AjaxResult borrowBook(DistributeDTO distributeDTO) {
        if (!StpUtil.isLogin()) {
            return AjaxResult.error("用户未登录！");
        }

        Book book = bookMapper.selectById(distributeDTO.getBookId());
        if (book == null) {
            return AjaxResult.error("图书不存在！");
        }

        if (book.getStatus() == 0) {
            return AjaxResult.error("图书已下架！");
        }

        if (book.getAvailableCount() <= 0) {
            return AjaxResult.error("图书库存不足！");
        }

        User user = userMapper.selectById(distributeDTO.getUserId());
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        if (user.getStatus() == 0) {
            return AjaxResult.error("用户账号已被禁用！");
        }

        DistributeRecord record = new DistributeRecord();
        record.setBookId(distributeDTO.getBookId());
        record.setUserId(distributeDTO.getUserId());
        record.setOperatorId((Integer) StpUtil.getLoginId());
        record.setDueDate(distributeDTO.getDueDate());
        record.setStatus("BORROWING");
        record.setRemark(distributeDTO.getRemark());

        int result = recordMapper.insert(record);
        if (result > 0) {
            bookMapper.updateAvailableCount(distributeDTO.getBookId(), -1);
            return new AjaxResult(HttpStatus.CREATED, "借阅成功", record.getId());
        }
        return AjaxResult.error("借阅失败！");
    }

    @Override
    @Transactional
    public AjaxResult returnBook(Integer id, String remark) {
        DistributeRecord record = recordMapper.selectById(id);
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }

        if (!"BORROWING".equals(record.getStatus())) {
            return AjaxResult.error("图书已归还！");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus("RETURNED");
        if (remark != null) {
            record.setRemark(remark);
        }

        int result = recordMapper.update(record);
        if (result > 0) {
            bookMapper.updateAvailableCount(record.getBookId(), 1);
            return AjaxResult.success("归还成功");
        }
        return AjaxResult.error("归还失败！");
    }

    @Override
    @Transactional
    public AjaxResult updateRecord(DistributeDTO distributeDTO) {
        if (distributeDTO.getId() == null) {
            return AjaxResult.error("记录ID不能为空！");
        }

        DistributeRecord record = recordMapper.selectById(distributeDTO.getId());
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }

        DistributeRecord updateRecord = new DistributeRecord();
        updateRecord.setId(distributeDTO.getId());
        
        if (distributeDTO.getBookId() != null) {
            updateRecord.setBookId(distributeDTO.getBookId());
        }
        if (distributeDTO.getUserId() != null) {
            updateRecord.setUserId(distributeDTO.getUserId());
        }
        if (distributeDTO.getDueDate() != null) {
            updateRecord.setDueDate(distributeDTO.getDueDate());
        }
        if (distributeDTO.getRemark() != null) {
            updateRecord.setRemark(distributeDTO.getRemark());
        }

        int result = recordMapper.updateRecord(updateRecord);
        if (result > 0) {
            return AjaxResult.success("更新成功");
        }
        return AjaxResult.error("更新失败！");
    }

    @Override
    public AjaxResult deleteRecord(Integer id) {
        DistributeRecord record = recordMapper.selectById(id);
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }

        int result = recordMapper.deleteById(id);
        if (result > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败！");
    }

    @Override
    public AjaxResult getMyBorrowList() {
        if (!StpUtil.isLogin()) {
            return AjaxResult.error("用户未登录！");
        }

        String username = StpUtil.getLoginIdAsString();
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        DistributeRecord record = new DistributeRecord();
        record.setUserId(user.getId());
        java.util.List<DistributeRecord> records = recordMapper.selectByCondition(record);
        return AjaxResult.success("查询成功", records);
    }

    @Override
    public long getBorrowedBookCount() {
        return recordMapper.countBorrowing();
    }

    @Override
    public int[] getMonthlyBorrowCounts() {
        return recordMapper.getMonthlyBorrowCounts();
    }

    @Override
    public java.util.Map<String, Integer> getStatusDistribution() {
        java.util.Map<String, Integer> distribution = new java.util.HashMap<>();
        distribution.put("borrowing", recordMapper.countByStatus("BORROWING"));
        distribution.put("returned", recordMapper.countByStatus("RETURNED"));
        distribution.put("overdue", recordMapper.countByStatus("OVERDUE"));
        return distribution;
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getTopBorrowedBooks(int limit) {
        java.util.List<java.util.Map<String, Object>> books = recordMapper.getTopBorrowedBooks(limit);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> book : books) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("bookName", book.get("book_name"));
            item.put("author", book.get("author"));
            item.put("borrowCount", book.get("borrow_count"));
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional
    public AjaxResult renewBook(RenewDTO renewDTO) {
        DistributeRecord record = recordMapper.selectById(renewDTO.getRecordId());
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }

        if (!"BORROWING".equals(record.getStatus())) {
            return AjaxResult.error("图书已归还，无法续借！");
        }

        if (record.getRenewCount() != null && record.getRenewCount() >= 2) {
            return AjaxResult.error("续借次数已达上限！");
        }

        int extendDays = renewDTO.getExtendDays() != null ? renewDTO.getExtendDays() : DEFAULT_RENEW_DAYS;
        if (extendDays <= 0) {
            return AjaxResult.error("续借天数必须大于0！");
        }

        record.setDueDate(record.getDueDate().plusDays(extendDays));
        record.setRenewCount(record.getRenewCount() == null ? 1 : record.getRenewCount() + 1);
        
        int result = recordMapper.updateRecord(record);
        if (result > 0) {
            return AjaxResult.success("续借成功");
        }
        return AjaxResult.error("续借失败！");
    }

    @Override
    public AjaxResult getOverdueRecords() {
        DistributeRecord query = new DistributeRecord();
        query.setStatus("BORROWING");
        java.util.List<DistributeRecord> allRecords = recordMapper.selectByCondition(query);
        java.util.List<DistributeRecord> overdueRecords = new java.util.ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (DistributeRecord record : allRecords) {
            if (record.getDueDate() != null && record.getDueDate().isBefore(now)) {
                long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), now);
                record.setOverdueDays((int) overdueDays);
                BigDecimal fine = FINE_PER_DAY.multiply(new BigDecimal(overdueDays));
                record.setFine(fine);
                overdueRecords.add(record);
            }
        }
        return AjaxResult.success("查询成功", overdueRecords);
    }

    @Override
    public AjaxResult calculateFine(Integer recordId) {
        DistributeRecord record = recordMapper.selectById(recordId);
        if (record == null) {
            return AjaxResult.error("记录不存在！");
        }

        LocalDateTime now = LocalDateTime.now();
        if (record.getDueDate() == null || record.getDueDate().isAfter(now)) {
            return AjaxResult.success("查询成功", BigDecimal.ZERO);
        }

        long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), now);
        BigDecimal fine = FINE_PER_DAY.multiply(new BigDecimal(overdueDays));

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("overdueDays", overdueDays);
        result.put("fine", fine);
        return AjaxResult.success("查询成功", result);
    }
}
