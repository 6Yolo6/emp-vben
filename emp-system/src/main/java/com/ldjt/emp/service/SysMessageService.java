package com.ldjt.emp.service;

import com.ldjt.emp.dto.message.MessageSendDTO;
import com.ldjt.emp.vo.message.MessageVO;
import com.mybatisflex.core.paginate.Page;

/**
 * 站内消息服务接口
 */
public interface SysMessageService {

    /**
     * 发送消息
     */
    void sendMessage(MessageSendDTO dto);

    /**
     * 查询我的消息列表
     */
    Page<MessageVO> getMyMessagePage(Integer isRead, Integer pageNum, Integer pageSize);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId);

    /**
     * 批量标记为已读
     */
    void batchMarkAsRead(java.util.List<Long> messageIds);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId);

    /**
     * 获取未读消息数量
     */
    Long getUnreadCount();

    MessageVO getMessageById(Long messageId);
}
