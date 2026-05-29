package cn.edu.wynu.service;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.DistributeDTO;
import cn.edu.wynu.model.dto.RenewDTO;
import cn.edu.wynu.model.entity.DistributeRecord;
import java.util.List;

public interface DistributeRecordService {
    AjaxResult getRecordList(DistributeRecord record);
    AjaxResult getRecordById(Integer id);
    AjaxResult borrowBook(DistributeDTO distributeDTO);
    AjaxResult returnBook(Integer id, String remark);
    AjaxResult updateRecord(DistributeDTO distributeDTO);
    AjaxResult deleteRecord(Integer id);
    AjaxResult getMyBorrowList();
    long getBorrowedBookCount();
    int[] getMonthlyBorrowCounts();
    java.util.Map<String, Integer> getStatusDistribution();
    java.util.List<java.util.Map<String, Object>> getTopBorrowedBooks(int limit);
    
    AjaxResult renewBook(RenewDTO renewDTO);
    AjaxResult getOverdueRecords();
    AjaxResult calculateFine(Integer recordId);
}
