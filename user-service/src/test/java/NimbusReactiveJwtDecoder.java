////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package org.springframework.security.oauth2.jwt;
//
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jose.JWSAlgorithm;
//import com.nimbusds.jose.jwk.JWK;
//import com.nimbusds.jose.jwk.JWKSelector;
//import com.nimbusds.jose.jwk.JWKSet;
//import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.RSAKey.Builder;
//import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.BadJOSEException;
//import com.nimbusds.jose.proc.JWSKeySelector;
//import com.nimbusds.jose.proc.JWSVerificationKeySelector;
//import com.nimbusds.jwt.JWT;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.JWTParser;
//import com.nimbusds.jwt.SignedJWT;
//import com.nimbusds.jwt.proc.DefaultJWTProcessor;
//import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
//import com.nimbusds.jwt.proc.JWTProcessor;
//import java.security.interfaces.RSAPublicKey;
//import java.time.Instant;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import org.springframework.security.oauth2.core.OAuth2Error;
//import org.springframework.security.oauth2.core.OAuth2TokenValidator;
//import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
//import org.springframework.util.Assert;
//import reactor.core.publisher.Mono;
//
//public final class NimbusReactiveJwtDecoder implements ReactiveJwtDecoder {
//    private final JWTProcessor<JWKContext> jwtProcessor;
//    private final ReactiveJWKSource reactiveJwkSource;
//    private final JWKSelectorFactory jwkSelectorFactory;
//    private OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefault();
//
//    public NimbusReactiveJwtDecoder(RSAPublicKey publicKey) {
//        JWSAlgorithm algorithm = JWSAlgorithm.parse("RS256");
//        RSAKey rsaKey = rsaKey(publicKey);
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        JWKSource jwkSource = new ImmutableJWKSet(jwkSet);
//        JWSKeySelector<JWKContext> jwsKeySelector = new JWSVerificationKeySelector(algorithm, jwkSource);
//        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
//        jwtProcessor.setJWSKeySelector(jwsKeySelector);
//        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
//        });
//        this.jwtProcessor = jwtProcessor;
//        this.reactiveJwkSource = new ReactiveJWKSourceAdapter(jwkSource);
//        this.jwkSelectorFactory = new JWKSelectorFactory(algorithm);
//    }
//
//    public NimbusReactiveJwtDecoder(String jwkSetUrl) {
//        Assert.hasText(jwkSetUrl, "jwkSetUrl cannot be empty");
//        String jwsAlgorithm = "RS256";
//        JWSAlgorithm algorithm = JWSAlgorithm.parse(jwsAlgorithm);
//        JWKSource jwkSource = new JWKContextJWKSource();
//        JWSKeySelector<JWKContext> jwsKeySelector = new JWSVerificationKeySelector(algorithm, jwkSource);
//        DefaultJWTProcessor<JWKContext> jwtProcessor = new DefaultJWTProcessor();
//        jwtProcessor.setJWSKeySelector(jwsKeySelector);
//        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
//        });
//        this.jwtProcessor = jwtProcessor;
//        this.reactiveJwkSource = new ReactiveRemoteJWKSource(jwkSetUrl);
//        this.jwkSelectorFactory = new JWKSelectorFactory(algorithm);
//    }
//
//    public void setJwtValidator(OAuth2TokenValidator<Jwt> jwtValidator) {
//        Assert.notNull(jwtValidator, "jwtValidator cannot be null");
//        this.jwtValidator = jwtValidator;
//    }
//
//    public Mono<Jwt> decode(String token) throws JwtException {
//        JWT jwt = this.parse(token);
//        if (jwt instanceof SignedJWT) {
//            return this.decode((SignedJWT)jwt);
//        } else {
//            throw new JwtException("Unsupported algorithm of " + jwt.getHeader().getAlgorithm());
//        }
//    }
//
//    private JWT parse(String token) {
//        try {
//            return JWTParser.parse(token);
//        } catch (Exception var3) {
//            throw new JwtException("An error occurred while attempting to decode the Jwt: " + var3.getMessage(), var3);
//        }
//    }
//
//    private Mono<Jwt> decode(SignedJWT parsedToken) {
//        try {
//            JWKSelector selector = this.jwkSelectorFactory.createSelector(parsedToken.getHeader());
//            return this.reactiveJwkSource.get(selector).onErrorMap((e) -> {
//                return new IllegalStateException("Could not obtain the keys", e);
//            }).map((jwkList) -> {
//                return this.createClaimsSet(parsedToken, jwkList);
//            }).map((set) -> {
//                return this.createJwt(parsedToken, set);
//            }).map(this::validateJwt).onErrorMap((e) -> {
//                return !(e instanceof IllegalStateException) && !(e instanceof JwtException);
//            }, (e) -> {
//                return new JwtException("An error occurred while attempting to decode the Jwt: ", e);
//            });
//        } catch (RuntimeException var3) {
//            throw new JwtException("An error occurred while attempting to decode the Jwt: " + var3.getMessage(), var3);
//        }
//    }
//
//    private JWTClaimsSet createClaimsSet(JWT parsedToken, List<JWK> jwkList) {
//        try {
//            return this.jwtProcessor.process(parsedToken, new JWKContext(jwkList));
//        } catch (JOSEException | BadJOSEException var4) {
//            throw new JwtException("Failed to validate the token", var4);
//        }
//    }
//
//    private Jwt createJwt(JWT parsedJwt, JWTClaimsSet jwtClaimsSet) {
//        Instant expiresAt = null;
//        if (jwtClaimsSet.getExpirationTime() != null) {
//            expiresAt = jwtClaimsSet.getExpirationTime().toInstant();
//        }
//
//        Instant issuedAt = null;
//        if (jwtClaimsSet.getIssueTime() != null) {
//            issuedAt = jwtClaimsSet.getIssueTime().toInstant();
//        } else if (expiresAt != null) {
//            issuedAt = Instant.from(expiresAt).minusSeconds(1L);
//        }
//
//        Map<String, Object> headers = new LinkedHashMap(parsedJwt.getHeader().toJSONObject());
//        return new Jwt(parsedJwt.getParsedString(), issuedAt, expiresAt, headers, jwtClaimsSet.getClaims());
//    }
//
//    private Jwt validateJwt(Jwt jwt) {
//        OAuth2TokenValidatorResult result = this.jwtValidator.validate(jwt);
//        if (result.hasErrors()) {
//            String message = ((OAuth2Error)result.getErrors().iterator().next()).getDescription();
//            throw new JwtValidationException(message, result.getErrors());
//        } else {
//            return jwt;
//        }
//    }
//
//    private static RSAKey rsaKey(RSAPublicKey publicKey) {
//        return (new Builder(publicKey)).build();
//    }
//}
