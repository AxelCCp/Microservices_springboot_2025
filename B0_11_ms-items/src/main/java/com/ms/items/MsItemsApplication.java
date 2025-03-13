package com.ms.items;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class}) //se pone esta pq en ms-items no se necesita conexion con la base de datos. se puso para que no de error con la clase product q se comparte con el microservicio commons "ms-mylibs"  
@EnableFeignClients
@SpringBootApplication
public class MsItemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsItemsApplication.class, args);
	}

}
