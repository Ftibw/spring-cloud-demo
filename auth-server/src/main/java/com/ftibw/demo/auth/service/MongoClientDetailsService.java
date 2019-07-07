package com.ftibw.demo.auth.service;

import com.ftibw.demo.auth.bean.MongoClientDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ftibw.demo.auth.bean.MongoClientDetails.*;


/**
 * @author : Ftibw
 * @date : 2019/7/5 22:55
 * copy from {@link JdbcClientDetailsService}
 */
@Slf4j
@Service
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final Class<MongoClientDetails> TYPE = MongoClientDetails.class;

    private final MongoTemplate template;

    /**
     * 非常重要,不同版本的decoder校验方式不一样,
     * 一定要保证项目使用的decoder版本与数据库存储的值匹配
     */
    private PasswordEncoder passwordEncoder;


    public MongoClientDetailsService(MongoTemplate template) {
        this.template = template;
        // Spring Security团队推荐
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    private static MongoClientDetails toMongoClientDetails(ClientDetails clientDetails) {

        if (clientDetails instanceof MongoClientDetails) {
            return (MongoClientDetails) clientDetails;
        }

        return MongoClientDetails.builder()
                .clientId(clientDetails.getClientId())
                .resourceIds(clientDetails.getResourceIds())
                .clientSecret(clientDetails.getClientSecret())
                .scope(clientDetails.getScope())
                .authorizedGrantTypes(clientDetails.getAuthorizedGrantTypes())
                .registeredRedirectUri(clientDetails.getRegisteredRedirectUri())
                .authorities(clientDetails.getAuthorities())
                .accessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds())
                .additionalInformation(clientDetails.getAdditionalInformation()).build();
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        try {
            return template.findOne(new Query(Criteria.where(CLIENT_ID).is(clientId)), TYPE);
        } catch (EmptyResultDataAccessException var4) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        try {
            template.insert(toMongoClientDetails(clientDetails));
        } catch (DuplicateKeyException var3) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId(), var3);
        }
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        String clientId = clientDetails.getClientId();
        assert null != clientId;

        Update update = Update.update(RESOURCE_IDS, clientDetails.getResourceIds());

        update.set(SCOPE, clientDetails.getScope());
        update.set(AUTHORIZED_GRANT_TYPES, clientDetails.getAuthorizedGrantTypes());
        update.set(REGISTERED_REDIRECT_URI, clientDetails.getRegisteredRedirectUri());
        update.set(AUTHORITIES, clientDetails.getAuthorities());
        update.set(ACCESS_TOKEN_VALIDITY_SECONDS, clientDetails.getAccessTokenValiditySeconds());
        update.set(REFRESH_TOKEN_VALIDITY_SECONDS, clientDetails.getRefreshTokenValiditySeconds());
        update.set(ADDITIONAL_INFORMATION, clientDetails.getAdditionalInformation());

        long count = template.updateFirst(new Query(Criteria.where(CLIENT_ID).is(clientId)),
                update, TYPE).getModifiedCount();
        if (count != 1) {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        long count = template.updateFirst(
                new Query(Criteria.where(CLIENT_ID).is(clientId)),
                Update.update(CLIENT_SECRET, this.passwordEncoder.encode(secret)),
                TYPE
        ).getModifiedCount();
        if (count != 1) {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        long count = template.remove(new Query(Criteria.where(CLIENT_ID).is(clientId)), TYPE).getDeletedCount();
        if (count != 1) {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return template.findAll(TYPE).stream().map(d -> (ClientDetails) d).collect(Collectors.toList());
    }

    /**
     * 更新并返回修改后的对象
     */
    public ClientDetails updateAutoApproveScopesByClientId(String clientId, List<String> autoScopes) throws NoSuchClientException {
        return template.findAndModify(
                new Query(Criteria.where(CLIENT_ID).is(clientId)),
                Update.update(AUTO_APPROVE_SCOPES, new HashSet<>(autoScopes)),
                FindAndModifyOptions.options().returnNew(true),
                TYPE
        );
    }

}
