package com.polyglot;


import com.google.cloud.translate.Translate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class PolyglotApplication {

    private static Translate translate;

    public static void main(String[] args) {
        SpringApplication.run(PolyglotApplication.class, args);

        /*RemoteTranslateHelper helper = RemoteTranslateHelper.create();
        translate = helper.getOptions().getService();

        Translation translation = translate.translate("¡Hola Mundo!");
        System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());*/
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
