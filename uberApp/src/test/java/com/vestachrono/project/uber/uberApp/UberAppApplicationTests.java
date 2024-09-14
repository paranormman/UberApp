package com.vestachrono.project.uber.uberApp;

import com.vestachrono.project.uber.uberApp.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UberAppApplicationTests {

	@Autowired
	private EmailSenderService emailSenderService;

	@Test
	void contextLoads() {

		emailSenderService.sendEmail(
				"lareki8980@asaud.com",
				"This is a test email Subject",
				"This is a test email body");

	}

	@Test
	void sendEMailToMultipleMailId() {

		String emails[] = {
				"lareki8980@asaud.com",
				"manojshivaprakash10@gmail.com",
				"shivaprakashmanoj@gmail.com"
		};

		emailSenderService.sendEmail(emails,
				"Hello from Uber application demo",
				"This is a test email body");
	}

}
