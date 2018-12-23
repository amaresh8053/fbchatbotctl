package com.ctl.hyperconverse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ctl.hyperconverse.util.Messenger;


@SpringBootApplication
public class HyperConverseSocialApplication {


	public static void main(String[] args) {
		SpringApplication.run(HyperConverseSocialApplication.class, args);
	}
}
