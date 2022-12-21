package net.ict.bodymanager.service;

import net.ict.bodymanager.dto.MailDTO;

public interface EmailService {
  void sendSimpleMessage(MailDTO mailDto);

  String checkMessage(MailDTO mailDTO);
}
