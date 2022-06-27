package com.balashenka.comrade.service.impl;

import com.balashenka.comrade.entity.type.AttachmentType;
import com.balashenka.comrade.facade.WebexFacade;
import com.balashenka.comrade.handler.attachment.AttachmentHandler;
import com.balashenka.comrade.util.MessageUtil;
import com.balashenka.comrade.util.locale.AttachmentLocaleText;
import com.balashenka.comrade.service.AttachmentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class DefaultAttachmentService implements AttachmentService {
    private static final String ATTACHMENT_TYPE_INPUT = "ATTACHMENT_TYPE";

    private final WebexFacade webexFacade;
    private final Map<AttachmentType, AttachmentHandler> attachments = new HashMap<>();
    private final Map<String, AttachmentType> attachmentTypes = new HashMap<>();

    @Autowired
    public DefaultAttachmentService(@NonNull List<AttachmentHandler> attachments,
                                    WebexFacade webexFacade,
                                    @NonNull MessageUtil messageUtil) {
        this.webexFacade = webexFacade;

        attachmentTypes.put(messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_UPDATE), AttachmentType.UPDATE_WISHLIST);
        attachmentTypes.put(messageUtil.getText(AttachmentLocaleText.ATTACHMENT_WISHLIST_ADD), AttachmentType.ADD_WISHLIST);
        attachmentTypes.put(messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL_CREATE), AttachmentType.POLL_CREATE);
        attachmentTypes.put(messageUtil.getText(AttachmentLocaleText.ATTACHMENT_POLL), AttachmentType.POLL);

        attachments.forEach(attachment -> this.attachments.put(attachment.getAttachmentType(), attachment));
    }

    @Override
    public void handle(String roomId, String attachmentId) {
        var attachment = webexFacade.getAttachmentAction(attachmentId);
        var attachmentType = attachmentTypes.get(attachment.getInputs().get(ATTACHMENT_TYPE_INPUT));
        var attachmentHandler = attachments.get(attachmentType);

        if (attachmentHandler != null) {
            log.info("Handle attachment: {}", attachmentHandler.getAttachmentType());
            attachmentHandler.handle(attachment);
        }
    }
}
