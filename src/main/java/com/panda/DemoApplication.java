package com.panda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 丁许
 * @date 2019-01-23 13:35
 */
@SpringBootApplication
@Slf4j
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		log.info("             ヾ(◍°∇°◍)ﾉﾞ    DemoApplication       ヾ(◍°∇°◍)ﾉﾞ\n"
				+ "  ____          _              ______                    _   ______            \n"
				+ " / ___'_ __ _ _(_)_ __   __ _  |_   _ \\                  / |_|_   _ `.          \n"
				+ " \\___ | '_ | '_| | '_ \\/ _`  |  | |_) |   .--DemoApplication.    .--. `| |-' | | `. \\  .--.   \n"
				+ "  ___)| |_)| | | | | ||  (_| |  |  __'. / .'`\\ \\/ .'`\\ \\| |   | |  | |/ .'`\\ \\ \n"
				+ " |____| .__|_| |_|_| |_\\__,  |_ | |__) || \\__. || \\__. || |, _| |_.' /| \\__. | \n"
				+ "  ====|_|===============|___/  |_______/ '.__.'  '.__.' \\__/|______.'  '.__.'  ");
	}

}
