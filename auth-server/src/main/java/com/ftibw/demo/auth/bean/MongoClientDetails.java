package com.ftibw.demo.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author : Ftibw
 * @date : 2019/7/5 23:02
 * copy form {@link JdbcClientDetailsService}
 * <p>
 * 特别需要注意的是如果实体类没有为任何字段使用{@link Indexed}创建索引将不会自动创建集合。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "ClientDetails")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "oauth2_client_details")
public class MongoClientDetails implements ClientDetails, Serializable {

    private static final long serialVersionUID = -6998714682230293730L;

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";

    public static final String AUTHORIZED_GRANT_TYPES = "authorizedGrantTypes";
    public static final String REGISTERED_REDIRECT_URI = "registeredRedirectUri";
    public static final String AUTHORITIES = "authorities";

    public static final String SCOPE = "scope";
    public static final String AUTO_APPROVE_SCOPES = "autoApproveScopes";
    public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String RESOURCE_IDS = "resourceIds";

    public static final String ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String REFRESH_TOKEN_VALIDITY_SECONDS = "refreshTokenValiditySeconds";

    @Id
    @Field("client_id")
    private String clientId;
    @Field("client_secret")
    private String clientSecret;

    @Field("authorized_grant_types")
    private Set<String> authorizedGrantTypes;
    @Field("registered_redirect_uri")
    private Set<String> registeredRedirectUri;
    private Collection<GrantedAuthority> authorities;

    private Set<String> scope;
    @Field("auto_approve_scopes")
    private Set<String> autoApproveScopes;
    @Field("additional_information")
    private Map<String, Object> additionalInformation;
    @Field("resource_ids")
    private Set<String> resourceIds;

    @Field("access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;
    @Field("refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return this.additionalInformation;
    }

    /**
     * method below
     */
    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    /**
     * 如果 autoApproveScopes 集合中含值"true"则全局自动授权
     * 或者有scope参数值在集合中, 则该scope自动授权
     */
    @Override
    public boolean isAutoApprove(String scope) {
        if (this.autoApproveScopes == null) {
            return false;
        } else {
            Iterator<String> it = this.autoApproveScopes.iterator();
            String auto;
            do {
                if (!it.hasNext()) {
                    return false;
                }
                auto = it.next();
            } while (!"true".equals(auto) && !scope.matches(auto));
            return true;
        }
    }
}
