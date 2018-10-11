package com.kaka.elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @author fuwei
 * @version V1.0
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2018/6/21 10:48
 */
@SpringBootApplication
public class PlatformApplication extends SpringBootServletInitializer {

  public static void main(String[] args){
    SpringApplication.run(PlatformApplication.class, args);
  }
}
