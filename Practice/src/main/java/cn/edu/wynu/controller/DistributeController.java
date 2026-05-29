package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.DistributeDTO;
import cn.edu.wynu.model.dto.RenewDTO;
import cn.edu.wynu.model.entity.DistributeRecord;
import cn.edu.wynu.service.DistributeRecordService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/distribute")
@SaCheckLogin
public class DistributeController {

    @Autowired
    private DistributeRecordService recordService;

    @GetMapping("/list")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult getRecordList(DistributeRecord record) {
        return recordService.getRecordList(record);
    }

    @GetMapping("/{id}")
    public AjaxResult getRecordById(@PathVariable Integer id) {
        return recordService.getRecordById(id);
    }

    @PostMapping("/borrow")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult borrowBook(@Valid @RequestBody DistributeDTO distributeDTO) {
        return recordService.borrowBook(distributeDTO);
    }

    @PutMapping("/return/{id}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult returnBook(@PathVariable Integer id, @RequestParam(required = false) String remark) {
        return recordService.returnBook(id, remark);
    }

    @PutMapping("/renew")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult renewBook(@Valid @RequestBody RenewDTO renewDTO) {
        return recordService.renewBook(renewDTO);
    }

    @GetMapping("/overdue")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult getOverdueRecords() {
        return recordService.getOverdueRecords();
    }

    @GetMapping("/fine/{recordId}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult calculateFine(@PathVariable Integer recordId) {
        return recordService.calculateFine(recordId);
    }

    @PutMapping("/update")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult updateRecord(@Valid @RequestBody DistributeDTO distributeDTO) {
        return recordService.updateRecord(distributeDTO);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult deleteRecord(@PathVariable Integer id) {
        return recordService.deleteRecord(id);
    }

    @GetMapping("/my-borrow")
    public AjaxResult getMyBorrowList() {
        return recordService.getMyBorrowList();
    }
}
