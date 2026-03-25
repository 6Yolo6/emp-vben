package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.message.MessageSendDTO;
import com.ldjt.emp.entity.SysMessage;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.entity.SysUserMessage;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysMessageMapper;
import com.ldjt.emp.mapper.SysUserMapper;
import com.ldjt.emp.mapper.SysUserMessageMapper;
import com.ldjt.emp.service.SysMessageService;
import com.ldjt.emp.vo.message.MessageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysMessageTableDef.SYS_MESSAGE;
import static com.ldjt.emp.entity.table.SysUserMessageTableDef.SYS_USER_MESSAGE;
import static com.ldjt.emp.entity.table.SysUserTableDef.SYS_USER;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.ldjt.emp.entity.table.SysUserTenantTableDef.SYS_USER_TENANT;

/**
 * 站内消息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl implements SysMessageService {

    private final SysMessageMapper messageMapper;
    private final SysUserMessageMapper userMessageMapper;
    private final SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        // 获取当前用户信息
        SysUser currentUser = userMapper.selectOneById(currentUserId);

        // 创建消息
        SysMessage message = new SysMessage();
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setMessageType(dto.getMessageType());
        message.setSenderId(currentUserId);
        message.setSenderName(currentUser != null ? currentUser.getNickname() : "系统");
        message.setReceiverType(dto.getReceiverType());
        message.setStatus(1); // 已发送
        message.setTenantId(tenantId);

        // 根据接收人类型处理
        List<Long> receiverUserIds = new ArrayList<>();

        if (dto.getReceiverType() == 1) {
            // 指定用户
            if (dto.getReceiverIds() == null || dto.getReceiverIds().isEmpty()) {
                throw new BusinessException("接收人不能为空");
            }
            receiverUserIds.addAll(dto.getReceiverIds());
            message.setReceiverIds(dto.getReceiverIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        } else if (dto.getReceiverType() == 2) {
            // 指定角色 - 查询角色下的所有用户
            if (dto.getReceiverRoleIds() == null || dto.getReceiverRoleIds().isEmpty()) {
                throw new BusinessException("接收角色不能为空");
            }
            message.setReceiverRoleIds(dto.getReceiverRoleIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));

            // 查询角色下的用户ID列表（通过用户角色关联表）
            List<Long> roleUserIds = userMapper.selectListByQuery(
                    QueryWrapper.create()
                            .select(SYS_USER.ID)
                            .from(SYS_USER)
                            .innerJoin(SYS_USER_ROLE).on(SYS_USER.ID.eq(SYS_USER_ROLE.USER_ID))
                            .where(SYS_USER_ROLE.ROLE_ID.in(dto.getReceiverRoleIds()))
                            .and(SYS_USER_ROLE.TENANT_ID.eq(tenantId))
                            .and(SYS_USER.DELETED.eq(0))
            ).stream()
                    .map(SysUser::getId)
                    .distinct()
                    .collect(Collectors.toList());

            receiverUserIds.addAll(roleUserIds);
        } else if (dto.getReceiverType() == 3) {
            // 全体用户 - 查询租户下所有用户（通过用户租户关联表）
            List<SysUser> allUsers = userMapper.selectListByQuery(
                    QueryWrapper.create()
                            .select(SYS_USER.ID)
                            .from(SYS_USER)
                            .innerJoin(SYS_USER_TENANT).on(SYS_USER.ID.eq(SYS_USER_TENANT.USER_ID))
                            .where(SYS_USER_TENANT.TENANT_ID.eq(tenantId))
                            .and(SYS_USER.DELETED.eq(0))
            );
            receiverUserIds = allUsers.stream()
                    .map(SysUser::getId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 保存消息
        messageMapper.insert(message);

        // 创建用户消息关联记录
        for (Long userId : receiverUserIds) {
            SysUserMessage userMessage = new SysUserMessage();
            userMessage.setMessageId(message.getId());
            userMessage.setUserId(userId);
            userMessage.setIsRead(0);
            userMessage.setTenantId(tenantId);
            userMessage.setDeleted(0);
            userMessageMapper.insert(userMessage);
        }

        log.info("消息发送成功，消息ID：{}，接收人数：{}", message.getId(), receiverUserIds.size());
    }

    @Override
    public Page<MessageVO> getMyMessagePage(Integer isRead, Integer pageNum, Integer pageSize) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        QueryWrapper query = QueryWrapper.create()
                .select(
                        SYS_MESSAGE.ID,
                        SYS_MESSAGE.TITLE,
                        SYS_MESSAGE.CONTENT,
                        SYS_MESSAGE.MESSAGE_TYPE,
                        SYS_MESSAGE.SENDER_ID,
                        SYS_MESSAGE.SENDER_NAME,
                        SYS_MESSAGE.CREATE_TIME,
                        SYS_USER_MESSAGE.IS_READ,
                        SYS_USER_MESSAGE.READ_TIME
                )
                .from(SYS_USER_MESSAGE)
                .leftJoin(SYS_MESSAGE).on(SYS_USER_MESSAGE.MESSAGE_ID.eq(SYS_MESSAGE.ID))
                .where(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                .and(SYS_USER_MESSAGE.DELETED.eq(0))
                .and(SYS_MESSAGE.DELETED.eq(0));

        if (isRead != null) {
            query.and(SYS_USER_MESSAGE.IS_READ.eq(isRead));
        }

        query.orderBy(SYS_MESSAGE.CREATE_TIME.desc());

        return userMessageMapper.paginateAs(
                Page.of(pageNum, pageSize),
                query,
                MessageVO.class
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        SysUserMessage userMessage = userMessageMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_USER_MESSAGE.MESSAGE_ID.eq(messageId))
                        .and(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                        .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                        .and(SYS_USER_MESSAGE.DELETED.eq(0))
        );

        if (userMessage == null) {
            throw new BusinessException("消息不存在");
        }

        if (userMessage.getIsRead() == 0) {
            userMessage.setIsRead(1);
            userMessage.setReadTime(LocalDateTime.now());
            userMessageMapper.update(userMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsRead(List<Long> messageIds) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        for (Long messageId : messageIds) {
            SysUserMessage userMessage = userMessageMapper.selectOneByQuery(
                    QueryWrapper.create()
                            .where(SYS_USER_MESSAGE.MESSAGE_ID.eq(messageId))
                            .and(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                            .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                            .and(SYS_USER_MESSAGE.DELETED.eq(0))
            );

            if (userMessage != null && userMessage.getIsRead() == 0) {
                userMessage.setIsRead(1);
                userMessage.setReadTime(LocalDateTime.now());
                userMessageMapper.update(userMessage);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        SysUserMessage userMessage = userMessageMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_USER_MESSAGE.MESSAGE_ID.eq(messageId))
                        .and(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                        .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                        .and(SYS_USER_MESSAGE.DELETED.eq(0))
        );

        if (userMessage == null) {
            throw new BusinessException("消息不存在");
        }

        userMessage.setDeleted(1);
        userMessageMapper.update(userMessage);
    }

    @Override
    public Long getUnreadCount() {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        return userMessageMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                        .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                        .and(SYS_USER_MESSAGE.IS_READ.eq(0))
                        .and(SYS_USER_MESSAGE.DELETED.eq(0))
        );
    }

    @Override
    public MessageVO getMessageById(Long messageId) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        return userMessageMapper.selectOneByQueryAs(
                QueryWrapper.create()
                        .select(
                                SYS_MESSAGE.ID,
                                SYS_MESSAGE.TITLE,
                                SYS_MESSAGE.CONTENT,
                                SYS_MESSAGE.MESSAGE_TYPE,
                                SYS_MESSAGE.SENDER_ID,
                                SYS_MESSAGE.SENDER_NAME,
                                SYS_MESSAGE.CREATE_TIME,
                                SYS_USER_MESSAGE.IS_READ,
                                SYS_USER_MESSAGE.READ_TIME
                        )
                        .from(SYS_USER_MESSAGE)
                        .leftJoin(SYS_MESSAGE).on(SYS_USER_MESSAGE.MESSAGE_ID.eq(SYS_MESSAGE.ID))
                        .where(SYS_USER_MESSAGE.USER_ID.eq(currentUserId))
                        .and(SYS_USER_MESSAGE.TENANT_ID.eq(tenantId))
                        .and(SYS_USER_MESSAGE.DELETED.eq(0))
                        .and(SYS_MESSAGE.DELETED.eq(0))
                        .and(SYS_MESSAGE.ID.eq(messageId)),
                MessageVO.class
        );
    }
}
