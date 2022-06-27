package com.balashenka.comrade.factory;

import com.balashenka.comrade.entity.type.AttachmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AttachmentFactoryProvider {
    private final Map<AttachmentType, AttachmentFactory> factories = new HashMap<>();

    @Autowired
    public AttachmentFactoryProvider(@NonNull List<AttachmentFactory> factories) {
        factories.forEach(factory -> this.factories.put(factory.getAttachmentType(), factory));
    }

    public AttachmentFactory getFactory(AttachmentType attachmentType) {
        return factories.get(attachmentType);
    }
}
