package org.camunda.bpm.getstarted.loanapproval;


import javax.servlet.Filter;

import org.camunda.bpm.engine.rest.security.auth.ProcessEngineAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * 
   APIエンドポイントへのアクセスを制限するようにSpringSecurityを使用してアプリケーションを構成します。
 */
@EnableWebSecurity
public class CamundaSecurityFilter extends WebSecurityConfigurerAdapter {

    @Value( "${auth0.audience}" )
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        /*
        	ここで、エンドポイントに必要なセキュリティを構成し、アプリを次のように設定します。
        	JWT検証を使用したOAuth2リソースサーバー。
        */
    	
        http.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .and().cors()
                .and().oauth2ResourceServer().jwt();//JWT形式のアクセストークンを使用することを明示的に指定する
        		/*
        		 * Spring Bootアプリケーションを、JWT認証を使用してすべての着信リクエスト（上記で除外されたものを除く）を認証するOAuth2リソースサーバーとして構成します。
        		 * つまり、すべてのリクエストには、有効なJWTを含む以下の認証ヘッダーが必要です。
        		 */
    }

    //JwtDecoder は、Jwt 構成でデコードし、公開キーに対して受信トークンを検証するために使用されます。
    /**
     * AudienceValidatorのカスタム検証構成をリソースサーバに追加
     * 
     */
    @Bean
    JwtDecoder jwtDecoder() {
        /*
			デフォルトでは、Spring Securityはトークンの「aud」クレームを検証せず、このトークンが
			        確かに私たちのアプリを対象としています。独自のバリデーターを追加するのは簡単です。
			JwtDecoderは着信JWTをデコードするために使用されます
        */

        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromOidcIssuerLocation(issuer);// JwtDecoder を初期化することにより、提供された発行者を使用して JwtDecoder を作成します。

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);//Jwt カスタムオーディエンスクレームのトークンバリデーター。
        
        //発行者がわかっている場合は、すべての標準バリデーターを含む Jwt バリデーターを作成します。
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);// 使用する Jwt Validator

        return jwtDecoder;
    }
	@Bean
	public FilterRegistrationBean processEngineAuthenticationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setName("camunda-auth");
		registration.setFilter(getProcessEngineAuthenticationFilter());
		registration.addInitParameter("authentication-provider",
				"org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider");
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public Filter getProcessEngineAuthenticationFilter() {
		return new ProcessEngineAuthenticationFilter();
	}	
}
