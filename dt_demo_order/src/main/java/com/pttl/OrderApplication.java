package com.pttl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;//1.5x打开这个包
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.pttl.distributed.transaction.annotation.EnableDistributedTransaction;
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableHystrix
@ComponentScan(value = "com.pttl")
@MapperScan("com.pttl.mapper.order")
@EnableDistributedTransaction
public class OrderApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext application = SpringApplication.run(OrderApplication.class, args);
	
		
	}
}
