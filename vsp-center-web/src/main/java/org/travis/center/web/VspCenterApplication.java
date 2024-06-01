package org.travis.center.web;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.travis.shared.common.utils.NetworkUtil;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @ClassName VspCenterApplication
 * @Description VspCenterApplication
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@EnableDubbo
@EnableWebSocket
@EnableScheduling
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan(basePackages = {"org.travis.center", "org.travis.shared", "org.travis.api"})
@MapperScan("org.travis.center.**.mapper")
@SpringBootApplication
public class VspCenterApplication {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        // Create Spring Application Instance
        SpringApplication application = new SpringApplication(VspCenterApplication.class);

        // Run and get environment variables
        ConfigurableEnvironment environment = application.run(args).getEnvironment();

        // Judge protocol
        String protocol = environment.getProperty("server.ssl.key-store") != null ? "https" : "http";

        System.out.println("\n" +
                " _    ___      __              __   _____       __                      ____  __      __  ____                   \n" +
                "| |  / (_)____/ /___  ______ _/ /  / ___/____  / /_  ___  ________     / __ \\/ /___ _/ /_/ __/___  _________ ___ \n" +
                "| | / / / ___/ __/ / / / __ `/ /   \\__ \\/ __ \\/ __ \\/ _ \\/ ___/ _ \\   / /_/ / / __ `/ __/ /_/ __ \\/ ___/ __ `__ \\\n" +
                "| |/ / / /  / /_/ /_/ / /_/ / /   ___/ / /_/ / / / /  __/ /  /  __/  / ____/ / /_/ / /_/ __/ /_/ / /  / / / / / /\n" +
                "|___/_/_/   \\__/\\__,_/\\__,_/_/   /____/ .___/_/ /_/\\___/_/   \\___/  /_/   /_/\\__,_/\\__/_/  \\____/_/  /_/ /_/ /_/ \n" +
                "                                     /_/                                                                         \n"
        );
        System.out.println("(♥◠‿◠)ﾉﾞ  VSP-Center Run Successfully!   ლ(´ڡ`ლ)ﾞ  ");
        System.out.printf(
                "\n---------------------------------------------------------------------------------------\n\t" +
                        "Application '%s' is running! Access URLs:\n\t" +
                        "Local: \t\t%s://localhost:%s\n\t" +
                        "External: \t%s://%s:%s\n\t" +
                        "Profile(s): \t%s" +
                        "\n---------------------------------------------------------------------------------------\n",
                environment.getProperty("spring.application.name"),
                protocol,
                environment.getProperty("server.port"),
                protocol,
                NetworkUtil.getLocalHostAddress(),
                environment.getProperty("server.port"),
                Arrays.toString(environment.getActiveProfiles())
        );
    }
}
