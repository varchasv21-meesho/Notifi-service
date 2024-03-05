package com.notification.service.v2v.Notifiservice.services.imiconnect;

import com.notification.service.v2v.Notifiservice.data.entity.imiconnect.Channels;
import com.notification.service.v2v.Notifiservice.data.entity.imiconnect.Destination;
import com.notification.service.v2v.Notifiservice.data.entity.imiconnect.SmsModal;
import com.notification.service.v2v.Notifiservice.data.entity.imiconnect.Sms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmsBuilder {

    public SmsModal buildSmsModal(String correlationId, String phoneNumber, String message){

        Sms sms = new Sms(message);
        Channels channels = new Channels(sms);

        List<String> msisdn = new ArrayList<>();
        msisdn.add(phoneNumber);
        Destination destination = new Destination(msisdn,correlationId);
        List<Destination> destinationList = new ArrayList<>();
        destinationList.add(destination);

        return new SmsModal("sms", channels, destinationList);
    }
}
