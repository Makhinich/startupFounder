package com.goit.startup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Spring MVC configuration class
 *
 * @author Vadym Pylypchenko
 * @version 1.0
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.goit.startup.controller", "com.goit.startup.validator", "com.goit.startup.config" })
@PropertySource("classpath:content.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * a parameter of content type
     */
    @Value("${view.type}")
    private String contentType;

    /**
     * a parameter of URL path to locate the view
     */
    @Value("${view.name-prefix}")
    private String viewPrefix;

    /**
     * a parameter type file of view
     */
    @Value("${view.name-suffix}")
    private String viewSuffix;

    /**
     * a parameter, that allows to use bean as a attribute
     */
    @Value("${view.expose_beans_as_attributes}")
    private boolean exposeContextBeansAsAttributes;

    /**
     * a location of resource handler
     */
    @Value("${resources.handler}")
    private String resourcesHandler;

    /**
     * a location of configuration file
     */
    @Value("${resources.location}")
    private String resourcesLocation;

    /**
     * The method configures a views
     *
     * @return an instance of {@link ViewResolver}
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setContentType(contentType);
        viewResolver.setPrefix(viewPrefix);
        viewResolver.setSuffix(viewSuffix);
        viewResolver.setExposeContextBeansAsAttributes(exposeContextBeansAsAttributes);
        return viewResolver;
    }

    /**
     * The method configures an instance of {@link ResourceHandlerRegistry}
     *
     * @param resourceHandlerRegistry an instance of {@link ResourceHandlerRegistry}
     */
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler(resourcesHandler)
                .addResourceLocations(resourcesLocation);
    }

    /**
     * Method configures an instance of {@link MessageSource}
     *
     * @return  an instance of {@link MessageSource}
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Method configures an instance of {@link CommonsMultipartResolver}
     *
     * @return an instance of {@link CommonsMultipartResolver}
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }

}
