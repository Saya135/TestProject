package org.camunda.bpm.getstarted.loanapproval;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * JWTトークンのクレームに対象ユーザーが含まれていることを検証します。
 * このインターフェースの実装は、「検証」を担当します。OAuth 2.0 トークンに含まれる属性の有効性および / または制約。
 */
class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String audience;

    AudienceValidator(String audience) {
        this.audience = audience;
    }
    /**
     * Audience	JWTの利用者
     * Issuer	JWTの発行者
     */
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);
        
        if (jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}