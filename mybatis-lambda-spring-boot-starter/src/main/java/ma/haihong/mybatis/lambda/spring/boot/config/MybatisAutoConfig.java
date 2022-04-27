package ma.haihong.mybatis.lambda.spring.boot.config;

import ma.haihong.mybatis.lambda.core.MybatisMapperRegistry;
import ma.haihong.mybatis.lambda.util.MybatisConfigUtils;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haihong.ma
 */
@Configuration
@ConditionalOnClass(MybatisAutoConfiguration.class)
public class MybatisAutoConfig {

    @Bean
    public ConfigurationCustomizer mybatisConfigCustomizer() {
        return config -> {
            config.setMapUnderscoreToCamelCase(true);
            MybatisConfigUtils.registerMapperRegistry(config, new MybatisMapperRegistry(config));
        };
    }
}
