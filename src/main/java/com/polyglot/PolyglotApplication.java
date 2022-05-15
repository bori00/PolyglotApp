package com.polyglot;


import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PolyglotApplication {

    private static Translate translate;

    public static void main(String[] args) {
        SpringApplication.run(PolyglotApplication.class, args);

        RemoteTranslateHelper helper = RemoteTranslateHelper.create();
        translate = helper.getOptions().getService();

        Translation translation = translate.translate("¡Hola Mundo!");
        System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());
    }

}
