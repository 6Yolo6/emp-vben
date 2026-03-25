package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.dto.message.MessageSendDTO;
import com.ldjt.emp.service.SysMessageService;
import com.ldjt.emp.vo.message.MessageVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息控制器
 */
@Tag(name = "站内消息管理")
@RestController
@RequestMapping("/system/message")
@RequiredArgsConstructor
public class SysMessageController {

    private final SysMessageService messageService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    @SaCheckPermission("system:message:send")
    public Result<Void> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        messageService.sendMessage(dto);
        return Result.success();
    }

    @Operation(summary = "查询我的消息列表")
    @GetMapping("/my/page")
    public Result<Page<MessageVO>> getMyMessagePage(
            @Parameter(description = "是否已读：0-未读 1-已读") @RequestParam(required = false) Integer isRead,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        return Result.success(messageService.getMyMessagePage(isRead, pageNum, pageSize));
    }

    @Operation(summary = "标记消息为已读")
    @PutMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success();
    }

    @Operation(summary = "批量标记为已读")
    @PutMapping("/read/batch")
    public Result<Void> batchMarkAsRead(@RequestBody List<Long> ids) {
        messageService.batchMarkAsRead(ids);
        return Result.success();
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return Result.success();
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount() {
        return Result.success(messageService.getUnreadCount());
    }

    @Operation(summary = "根据id查询消息详情")
    @GetMapping("/getMessage/{id}")
    public Result<MessageVO> getMessageById(@Parameter(description = "消息ID") @PathVariable Long id) {
        MessageVO message = messageService.getMessageById(id);
        return Result.success(message);
    }
}
