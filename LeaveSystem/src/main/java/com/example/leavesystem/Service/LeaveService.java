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

            requestsDto.setLeaveId(requestsEntity.getLeaveId());
            requestsDto.setStartDate(requestsEntity.getStartDate().toString());
            requestsDto.setEndDate(requestsEntity.getEndDate().toString());
            requestsDto.setReason(requestsEntity.getReason());
            requestsDto.setStatus(requestsEntity.getStatus().name());
            requestsDto.setUserId(requestsEntity.getUserId());

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

    //post leave-requests
    public RequestsEntity createRequest(RequestsEntity request) {
        request.setUserId(1);
        request.setStatus(StatusEntity.PENDING);
        return requestRepo.save(request);
    }

    //get leave-requests
    public List<RequestsEntity> getRequest() {
        return requestRepo.findAll();
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    //put leave-requests/{id}
    public RequestsEntity updateStatus(int id, StatusEntity status) {
        RequestsEntity req = requestRepo.findById(id).orElse(null);
        if (req == null) {
            System.out.println("ไม่พบรายการลา");
            return null;
        }
        req.setStatus(status);
        RequestsEntity savedReq = requestRepo.save(req);
//        return requestRepo.save(req);

        if (status == StatusEntity.APPROVED) {
            handleBalanceIfApproved(savedReq);
        }

        return savedReq;
    }

    private void handleBalanceIfApproved(RequestsEntity req) {
        Date startDate = req.getStartDate();
        Date endDate = req.getEndDate();
        if (startDate == null || endDate == null) return;

        int days = (int) TimeUnit.MILLISECONDS.toDays(endDate.getTime() - startDate.getTime()) + 1;
        String year = String.valueOf(startDate.getYear() + 1900);

        TypeEntity type = typeRepo.findById(req.getLeaveId()).orElse(null)  ;

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

        return balances.stream().map(balance ->{
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



}
