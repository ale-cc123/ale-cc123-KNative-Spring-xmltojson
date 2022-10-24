package io.cloudevents.examples.spring;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

@SpringBootApplication
public class XmlToJsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlToJsonApplication.class, args);
    }

    @Bean
    public Function<CloudEvent, CloudEvent> events() {
        return event -> {
            System.out.println("Processing event in Spring Cloud Function '" + event.getId() + "'");
            String json = xmlToJson(new String(event.getData().toBytes()));
            return CloudEventBuilder.from(event)
                    .withId(UUID.randomUUID().toString())
                    .withType("cc.io.joboffer.json")
                    .withSource(URI.create("http://cc.io.xmltojson"))
                    .withData(json.getBytes())
                    .build();
        };
    }

    public String xmlToJson(String xml) {
        try {
            JSONObject jsonObj = XML.toJSONObject(xml);
            return jsonObj.toString(4);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Configure a MessageConverter for Spring Cloud Function to pick up and use to
     * convert to and from CloudEvent and Message.
     */
    @Configuration
    public static class CloudEventMessageConverterConfiguration {
        @Bean
        public CloudEventMessageConverter cloudEventMessageConverter() {
            return new CloudEventMessageConverter();
        }
    }

    /**
     * Configure an HTTP reader and writer so that we can process CloudEvents over
     * HTTP via Spring Webflux.
     */
    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

}