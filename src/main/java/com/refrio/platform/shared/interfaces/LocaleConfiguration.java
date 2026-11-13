package com.refrio.platform.shared.interfaces;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.Locale;
@Configuration
public class LocaleConfiguration {
    @Bean LocaleResolver localeResolver(){
        AcceptHeaderLocaleResolver resolver=new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
}
