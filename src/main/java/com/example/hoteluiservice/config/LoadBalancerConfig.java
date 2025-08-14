package com.example.hoteluiservice.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@LoadBalancerClient(name = "user-service", configuration = LoadBalancerConfig.UserServiceLoadBalancerConfiguration.class)
public class LoadBalancerConfig {

    @Configuration
    public static class UserServiceLoadBalancerConfiguration {

        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new ServiceInstanceListSupplier() {
                @Override
                public String getServiceId() {
                    return "user-service";
                }

                @Override
                public Flux<List<ServiceInstance>> get() {
                    List<ServiceInstance> instances = Arrays.asList(
                            new UserServiceInstance("user-service-1", "localhost", 1511),
                            new UserServiceInstance("user-service-2", "localhost", 1512)
                    );
                    System.out.println("LoadBalancer: Providing " + instances.size() + " instances");
                    return Flux.just(instances);
                }
            };
        }
    }

    private static class UserServiceInstance implements ServiceInstance {
        private final String instanceId;
        private final String host;
        private final int port;

        public UserServiceInstance(String instanceId, String host, int port) {
            this.instanceId = instanceId;
            this.host = host;
            this.port = port;
        }

        @Override
        public String getServiceId() {
            return "user-service";
        }

        @Override
        public String getInstanceId() {
            return instanceId;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public Map<String, String> getMetadata() {
            return Collections.emptyMap();
        }

        @Override
        public URI getUri() {
            return URI.create("http://" + host + ":" + port);
        }

        @Override
        public String toString() {
            return "UserServiceInstance{" + instanceId + " -> " + getUri() + "}";
        }
    }
}