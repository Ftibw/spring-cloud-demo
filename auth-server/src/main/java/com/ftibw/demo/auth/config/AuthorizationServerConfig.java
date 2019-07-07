package com.ftibw.demo.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;


/**
 * @author ftibw
 * @date : 2019/7/6 1:47
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * RSA配置
     */
    @Value("${config.oauth2.privateKey}")
    private String privateKey;
    @Value("${config.oauth2.publicKey}")
    private String publicKey;

    private final UserDetailsService userDetailsService;
    private final ClientDetailsService clientDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

//    @Bean
//    public TokenStore jwtTokenStore() {
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }

    public AuthorizationServerConfig(
            @Qualifier("mongoClientDetailsService") ClientDetailsService clientDetailsService,
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager,
            RedisConnectionFactory redisConnectionFactory
    ) {
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //允许表单认证
                //.allowFormAuthenticationForClients()
                //isFullyAuthenticated():排除anonymous以及remember-me
                //isAuthenticated():排除anonymous

                //url:/oauth/token_key,exposes public key for token verification if using JWT tokens
                // 资源服务应用启动时拉取public-key,需要开放auth-server该端点权限
                // 公钥的权限默认开放,对外交给网关路由去控制最好
                .tokenKeyAccess("permitAll()")
                //url:/oauth/check_token allow check token
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);

//       PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("")

        // ==========================
//        clients.inMemory()
//                .withClient("q-media")
//                .secret(passwordEncoder.encode("q-media"))
//                .accessTokenValiditySeconds(3600)
//                .authorizedGrantTypes("authorization_code", "client_credentials", "password", "implicit", "refresh_token")
//                .redirectUris("http://localhost:8100/login/oauth2/code/q-media")
//                .scopes("all")
//                .autoApprove(true)
//                // ==========================
//                .and()
//                .withClient("feign")
//                .secret(passwordEncoder.encode("feign"))
//                .authorizedGrantTypes("client_credentials")
//                .scopes("all")
//                .accessTokenValiditySeconds(7200)
//                .autoApprove(true);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(redisTokenStore())
                .userDetailsService(userDetailsService)
//                .tokenServices(authorizationServerTokenServices())
                .accessTokenConverter(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(privateKey);
        jwtAccessTokenConverter.setVerifierKey(publicKey);
        return jwtAccessTokenConverter;
    }

//    @Bean
//    public AuthorizationServerTokenServices authorizationServerTokenServices() {
//        CustomAuthorizationTokenServices customTokenServices = new CustomAuthorizationTokenServices();
//        customTokenServices.setTokenStore(redisTokenStore());
//        customTokenServices.setSupportRefreshToken(true);
//        customTokenServices.setReuseRefreshToken(false);
//        customTokenServices.setClientDetailsService(clientDetailsService);
//        customTokenServices.setTokenEnhancer(jwtAccessTokenConverter());
//        return customTokenServices;
//    }

}
