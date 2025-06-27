package com.plant_management.controller;

import com.plant_management.entity.Attendance;
import com.plant_management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://grainsync.up.railway.app")


public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Get attendance records for a specific date.
     */
    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }

    /**
     * Get attendance history for a specific employee.
     */
    @GetMapping("/{employee_id}/history")
    public ResponseEntity<List<Attendance>> getAttendanceHistory(@PathVariable Integer employee_id) {
        System.out.println("Fetching attendance history for employee ID: " + employee_id);
        List<Attendance> history = attendanceService.getAttendanceHistory(employee_id);
        System.out.println("Found " + history.size() + " attendance records");
        return ResponseEntity.ok(history);
    }

    /**
     * Initialize attendance records for all employees for a specific date.
     */
    @PostMapping("/initialize")
    public ResponseEntity<List<Attendance>> initializeAttendance(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("Received date: " + date);
        List<Attendance> attendanceList = attendanceService.initializeAttendanceForDate(date);
        return ResponseEntity.ok(attendanceList);
    }


    /**
     * Mark attendance for a specific employee on a given date.
     */
    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(
            @RequestParam("employee_id") Integer employeeId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "clock_in", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime clockIn,
            @RequestParam(value = "clock_out", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime clockOut) {
        return ResponseEntity.ok(attendanceService.markAttendance(employeeId, date, clockIn, clockOut));
    }

    @PutMapping("/{attendanceId}/clock-in")
    public ResponseEntity<Attendance> clockIn(@PathVariable Integer attendanceId) {
        Attendance attendance = attendanceService.getAttendanceById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + attendanceId));
        attendance.setClock_in(LocalTime.now());
        attendance.setStatus(Attendance.Status.present);
        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }

    @PutMapping("/{attendanceId}/clock-out")
    public ResponseEntity<Attendance> clockOut(@PathVariable Integer attendanceId) {
        Attendance attendance = attendanceService.getAttendanceById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + attendanceId));
        attendance.setClock_out(LocalTime.now());
        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }

    /**
     * Mark an employee as absent for a specific attendance record.
     */
    @PutMapping("/{attendanceId}/mark-absent")
    public ResponseEntity<Attendance> markAbsent(@PathVariable Integer attendanceId) {
        System.out.println("Marking attendance record " + attendanceId + " as absent");
        Attendance attendance = attendanceService.getAttendanceById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + attendanceId));
        return ResponseEntity.ok(attendanceService.markAbsent(attendance));
    }
}