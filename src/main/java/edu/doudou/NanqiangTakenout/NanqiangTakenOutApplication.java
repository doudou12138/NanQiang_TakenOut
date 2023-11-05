package edu.doudou.NanqiangTakenout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
public class NanqiangTakenOutApplication {

	public static void main(String[] args) {

		SpringApplication.run(NanqiangTakenOutApplication.class, args);
		log.info("项目正在启动....");

	}

}
