package com.auth.AuthImpl.ctp.websocketconfig;

import com.auth.AuthImpl.utils.JWTService;
import com.auth.AuthImpl.utils.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Set up message broker and destination prefixes
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Registering the WebSocket endpoint and adding a custom handshake interceptor
//        registry.addEndpoint("/ws-game")
//                .setAllowedOrigins("http://localhost:8081")
////                .setAllowedOrigins("*")
////                .setAllowedOrigins("http://your-trusted-domain.com") // Replace with your trusted domains
//                .addInterceptors(new HandshakeInterceptor() {
//                    @Override
//                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//                        // Extract the "Authorization" header
//                        System.out.println("request :"+request.toString());
//                        System.out.println("response :"+response.toString());
//                        System.out.println("wsHandler :"+wsHandler.toString());
//                        System.out.println("attributes :"+attributes.toString());
//                        List<String> authHeaders = request.getHeaders().get("Authorization");
//                        if (authHeaders != null && !authHeaders.isEmpty()) {
//                            String token = authHeaders.get(0).substring(7); // Remove "Bearer "
//
//                            String username = jwtService.extractUserName(token);
//
//                            if (username != null) {
//                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                                if (jwtService.validateToken(token, userDetails)) {
//                                    attributes.put("username", username); // Store user info in WebSocket session attributes
//                                    return true; // Allow the WebSocket handshake
//                                } else {
//                                    logger.warn("Invalid token for user: {}", username);
//                                }
//                            } else {
//                                logger.warn("Username extraction failed.");
//                            }
//                        }
//
//                        // If token is invalid or missing, reject the handshake
//                        response.setStatusCode(HttpStatus.FORBIDDEN);
//                        logger.error("Handshake rejected: Invalid or missing token.");
//                        return false; // Reject handshake
//                    }
//
//                    @Override
//                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                               WebSocketHandler wsHandler, Exception exception) {
//                        if (exception != null) {
//                            logger.error("Handshake failed: {}", exception.getMessage());
//                        } else {
//                            logger.info("Handshake succeeded");
//                        }
//                    }
//                })
//                .withSockJS();
//    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/join-game")
                .setAllowedOrigins("/**") // Adjust if necessary
//                .addInterceptors(new HandshakeInterceptor() {
//                    @Override
//                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//                        List<String> authHeaders = request.getHeaders().get("Authorization");
//                        if (authHeaders != null && !authHeaders.isEmpty()) {
//                            String token = authHeaders.get(0).substring(7); // Remove "Bearer "
//                            logger.info("Received Token: {}", token); // Log the token
//
//                            String username = jwtService.extractUserName(token);
//                            if (username != null) {
//                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                                if (jwtService.validateToken(token, userDetails)) {
//                                    attributes.put("username", username);
//                                    return true; // Allow handshake
//                                } else {
//                                    logger.warn("Invalid token for user: {}", username);
//                                }
//                            } else {
//                                logger.warn("Username extraction failed.");
//                            }
//                        }
//
//                        response.setStatusCode(HttpStatus.FORBIDDEN);
//                        logger.error("Handshake rejected: Invalid or missing token.");
//                        return false; // Reject handshake
//                    }
//
//
//                    @Override
//                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                               WebSocketHandler wsHandler, Exception exception) {
//                        if (exception != null) {
//                            logger.error("Handshake failed: {}", exception.getMessage());
//                        } else {
//                            logger.info("Handshake succeeded");
//                        }
//                    }
//                })
                .withSockJS();
    }

}
