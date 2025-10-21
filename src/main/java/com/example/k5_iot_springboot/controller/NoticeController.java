package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.entity.Notice;
import com.example.k5_iot_springboot.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
    public Notice create(@RequestBody Notice req) {
        Notice result = noticeService.create(req.getTitle(), req.getContent(), req.getAuthor());
        return result;
    }

    @GetMapping
    public List<Notice> getAll() {
        List<Notice> result = noticeService.getAll();
        return result;
    }

    @GetMapping
    public Notice getById(@PathVariable Long id) {
        Notice result = noticeService.getById(id);
        return result;
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }
}
