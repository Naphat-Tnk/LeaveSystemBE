package com.example.leavesystem.Controller;

import com.example.leavesystem.DTO.BalancesDto;
import com.example.leavesystem.DTO.RequestsDto;
import com.example.leavesystem.Entity.BalancesEntity;
import com.example.leavesystem.Entity.RequestsEntity;
import com.example.leavesystem.Service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

//    - POST /api/leave-requests (สร้างคำขอลา)
//    - GET /api/leave-requests (ดูประวัติการลา)
//    - PUT /api/leave-requests/{id} (อัพเดทสถานะการลา)
//    - GET /api/leave-balances (ดูจำนวนวันลาคงเหลือ)


    @PostMapping("/leave-requests")
    public RequestsEntity createRequests(@RequestBody RequestsEntity req) {
        return leaveService.createRequest(req);
    }

    @GetMapping("/leave-requests")
    public ResponseEntity<List<RequestsDto>> getRequests() {
        return ResponseEntity.ok(leaveService.getAllRequestsDto());
    }


    @PutMapping("/leave-requests/{id}")
    public RequestsEntity updateRequests(@PathVariable int id, @RequestBody RequestsEntity req) {
        return leaveService.updateStatus(id, req.getStatus());
    }

    @GetMapping("/leave-balances")
    public ResponseEntity<List<BalancesDto>> getBalances() {
        return ResponseEntity.ok(leaveService.getAllBalance());
    }
}
