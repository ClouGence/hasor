package net.example.db.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration()
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)//
                .apiInfo(apiInfo())//
                .select()//
                .apis(RequestHandlerSelectors.basePackage("net.example.hasor"))//
                .paths(PathSelectors.any())//
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()//
                .title("Spring Boot中使用Swagger2构建RESTful APIs")//
                .description("参考手册：https://www.hasor.net/doc/display/dataql/")//
                .termsOfServiceUrl("https://www.hasor.net/doc/display/dataql/")//
                .contact("wxy").version("1.0")//
                .build();
    }
}