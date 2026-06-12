package com.carbon.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("碳中和管理后端服务 API")
                        .description("面向企业内部系统提供碳预算与减排任务的统一能力平台，包括预算创建、指标分解、任务登记、进度上报、预警查询和报表读取等接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("碳中和管理平台")
                                .email("support@carbon.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
