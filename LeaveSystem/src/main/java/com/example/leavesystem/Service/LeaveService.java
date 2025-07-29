package com.example.leavesystem.Service;

import com.example.leavesystem.DTO.BalancesDto;
import com.example.leavesystem.DTO.RequestsDto;
import com.example.leavesystem.Entity.*;
import com.example.leavesystem.Repository.BalancesRepo;
import com.example.leavesystem.Repository.RequestsRepo;
import com.example.leavesystem.Repository.TypeRepo;
import com.example.leavesystem.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private RequestsRepo requestRepo;
    @Autowired
    private BalancesRepo balancesRepo;
    @Autowired
    private TypeRepo typeRepo;
    @Autowired
    private UsersRepo usersRepo;

    public List<RequestsDto> getAllRequestsDto() {
        return requestRepo.findAll().stream().map(requestsEntity -> {
            RequestsDto requestsDto = new RequestsDto();

            requestsDto.setId(requestsEntity.getId());
            requestsDto.setLeaveId(requestsEntity.getLeaveId());
            requestsDto.setStartDate(requestsEntity.getStartDate().toString());
            requestsDto.setEndDate(requestsEntity.getEndDate().toString());
            requestsDto.setReason(requestsEntity.getReason());
            requestsDto.setStatus(requestsEntity.getStatus().name());
            requestsDto.setUserId(requestsEntity.getUserId());
            requestsDto.setComment(requestsEntity.getComment());

            //คำนวณวันลา
            if (requestsEntity.getStartDate() != null && requestsEntity.getEndDate() != null) {
                long diff =  requestsEntity.getEndDate().getTime() - requestsEntity.getStartDate().getTime();
                int days = (int) TimeUnit.MILLISECONDS.toDays(diff);
                requestsDto.setDays(days);
            }

            //หาชื่อ type ที่เลือกกับข้อมูลคนกรอก
            TypeEntity type = typeRepo.findById(requestsEntity.getLeaveId()).orElse(null);
            requestsDto.setLeaveTypeName(type.getName());

            UsersEntity user = usersRepo.findById(requestsEntity.getUserId()).orElse(null);
            if (user != null) {
                requestsDto.setUsername(user.getUsername());
                requestsDto.setEmail(user.getEmail());
                requestsDto.setDepartment(user.getDepartment());
            }
            return requestsDto;
        }).collect(Collectors.toList());
    }

    //post leave-requests ขอลา
    public RequestsEntity createRequest(RequestsEntity request) {
        request.setUserId(2);
        request.setStatus(StatusEntity.PENDING);
        return requestRepo.save(request);
    }

    //get leave-requests ดูรายการขอลาทั้งหมด
    public List<RequestsEntity> getRequest() {
        return requestRepo.findAll();
    }

    //เอาไว้แปลง date -> localDate
    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    //put leave-requests/{id} อัปเดทสถานะ
    public RequestsEntity updateStatus(int id, StatusEntity status, String comment) {
        RequestsEntity req = requestRepo.findById(id).orElse(null);
        if (req == null) {
            System.out.println("ไม่พบรายการลา");
            return null;
        }
        req.setStatus(status);
        req.setComment(comment);
        RequestsEntity savedReq = requestRepo.save(req);
//        return requestRepo.save(req);

        //ตั้งเงื่อนไขว่าถ้าอนุมัติถึงจะหักวันลา
        if (status == StatusEntity.APPROVED) {
            handleBalanceIfApproved(savedReq);
        }

        return savedReq;
    }

    //methon ที่เอาไว้หักวันลาถ้าอนุมัติ
    private void handleBalanceIfApproved(RequestsEntity req) {
        Date startDate = req.getStartDate();
        Date endDate = req.getEndDate();
        if (startDate == null || endDate == null) return;

        int days = (int) TimeUnit.MILLISECONDS.toDays(endDate.getTime() - startDate.getTime()) + 1;
        String year = String.valueOf(startDate.getYear() + 1900);

        TypeEntity type = typeRepo.findById(req.getLeaveId()).orElse(null);

        if (type == null) {
            System.out.println("ไม่พบ balances");
            return;
        }
        int maxday = type.getMaxDay();

        BalancesEntity balances = balancesRepo.findByUserIdAndLeaveIdAndYear(req.getUserId(), req.getLeaveId(), year);
        if (balances == null) {
            balances = new BalancesEntity();
            balances.setUserId(req.getUserId());
            balances.setLeaveId(req.getLeaveId());
            balances.setYear(year);
            balances.setRemainDay(maxday);
        }
        balances.setRemainDay(balances.getRemainDay() - days);
        balancesRepo.save(balances);
    }

    //get balances
    public List<BalancesDto> getAllBalance() {
        List<BalancesEntity> balances = balancesRepo.findAll();

        return balances.stream().map(balance -> {
            BalancesDto balancesDto = new BalancesDto();

            balancesDto.setId(balance.getId());
            balancesDto.setUserId(balance.getUserId());
            balancesDto.setLeaveId(balance.getLeaveId());
            balancesDto.setYear(balance.getYear());
            balancesDto.setRemainDay(balance.getRemainDay());

            TypeEntity type = typeRepo.findById(balance.getLeaveId()).orElse(null);
            balancesDto.setTypename(type.getName());

            return balancesDto;
        }).collect(Collectors.toList());

    }

    //Excel
    private int getUsedDay(int userId, int leaveId, String year) {
        TypeEntity type = typeRepo.findById(leaveId).orElse(null);
        if (type == null) {
            return 0;
        }

        BalancesEntity balances = balancesRepo.findByUserIdAndLeaveIdAndYear(userId, leaveId, year);
        if (balances == null) {
            return 0;
        }

        int usedDay = type.getMaxDay() - balances.getRemainDay();
        return Math.max(usedDay, 0);
    }

    private int RemainDay(int userId, int leaveId, String year) {
        TypeEntity type = typeRepo.findById(leaveId).orElse(null);
        if (type == null) {
            return 0;
        }

        BalancesEntity balances = balancesRepo.findByUserIdAndLeaveIdAndYear(userId, leaveId, year);
        if (balances == null) {
            return type.getMaxDay();
        }
        return balances.getRemainDay();
    }

    public ByteArrayOutputStream exportExcel() throws IOException {
        StringBuilder csv = new StringBuilder();
        final String UTF8 = "\uFEFF";
        csv.append(UTF8);
        csv.append("ชื่อ-นามสกุล,แผนก,ลาป่วย(ใช้),ลาป่วย(เหลือ),ลากิจ(ใช้),ลากิจ(เหลือ),ลาพักร้อน(ใช้),ลาพักร้อน(เหลือ),รวม(ใช้),รวม(เหลือ)\n");

        List<UsersEntity> users = usersRepo.findAll();
        String year = String.valueOf(LocalDate.now().getYear());

        for (UsersEntity user : users) {
            if("admin".equals(user.getUsername())) continue;
            int sick = getUsedDay(user.getId(), 2, year);
            int sickRemain = RemainDay(user.getId(), 2, year);

            int busy = getUsedDay(user.getId(), 3, year);
            int busyRemain = RemainDay(user.getId(), 3, year);

            int holiday = getUsedDay(user.getId(), 1, year);
            int holidayRemain = RemainDay(user.getId(), 1, year);

            int totalused = sick + busy + holiday;
            int totalRemain = sickRemain + busyRemain + holidayRemain;

            csv.append(String.format("%s,%s,%d,%d,%d,%d,%d,%d,%d,%d\n",
                    user.getUsername(), user.getDepartment(), sick, sickRemain, busy, busyRemain, holiday, holidayRemain, totalused, totalRemain));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(csv.toString().getBytes(StandardCharsets.UTF_8));
        return outputStream;
    }
}
