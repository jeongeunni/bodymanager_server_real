package net.ict.bodymanager.service;

import net.ict.bodymanager.controller.dto.MailDTO;

public interface EmailService {
  void sendSimpleMessage(MailDTO mailDto);

  String checkMessage(MailDTO mailDTO);
}
