package com.ma.message_apps.mapper;

import com.ma.message_apps.dto.MessageDto;
import com.ma.message_apps.entity.Message;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-17T19:13:39+0530",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class MessageConversionImpl implements MessageConversion {

    @Override
    public MessageDto toMessageDto(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setCreatedAt( message.getCreatedAt() );
        messageDto.setIsRead( message.getIsRead() );
        messageDto.setMessageId( message.getMessageId() );
        messageDto.setMessageText( message.getMessageText() );
        messageDto.setReceiver( message.getReceiver() );
        messageDto.setSender( message.getSender() );

        return messageDto;
    }

    @Override
    public Message toMessage(MessageDto messageDto) {
        if ( messageDto == null ) {
            return null;
        }

        Message message = new Message();

        message.setCreatedAt( messageDto.getCreatedAt() );
        message.setIsRead( messageDto.getIsRead() );
        message.setMessageId( messageDto.getMessageId() );
        message.setMessageText( messageDto.getMessageText() );
        message.setReceiver( messageDto.getReceiver() );
        message.setSender( messageDto.getSender() );

        return message;
    }
}
