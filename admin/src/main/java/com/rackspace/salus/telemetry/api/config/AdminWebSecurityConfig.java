/*
 * Copyright 2019 Rackspace US, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rackspace.salus.telemetry.api.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The majority of the Spring Security is derived from
 * <a href="https://github.com/vdenotaris/spring-boot-security-saml-sample">the sample project</a>
 * referenced from the
 * <a href="https://docs.spring.io/spring-security-saml/docs/1.0.x/reference/html/configuration-sso.html">Spring SAML documentation</a>.
 * There is a lot of generic parts in this config since there is not a Spring Boot starter or
 * auto-configuration, so it is worth pointing out that only piece that is specific to our
 * application's authorization is in {@link #configure(HttpSecurity)}.
 */
@Configuration
@Profile("secured")
@EnableConfigurationProperties(SamlProperties.class)
public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final SamlProperties samlProperties;
  private final ApiAdminProperties apiAdminProperties;
  private final ResourceLoader resourceLoader;
  private final SAMLUserDetailsService samlUserDetailsService;

  @Autowired
  public AdminWebSecurityConfig(SamlProperties samlProperties, ApiAdminProperties apiAdminProperties,
      ResourceLoader resourceLoader,
      SAMLUserDetailsService samlUserDetailsService) {
    this.samlProperties = samlProperties;
    this.apiAdminProperties = apiAdminProperties;
    this.resourceLoader = resourceLoader;
    this.samlUserDetailsService = samlUserDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable() // CSRF doesn't help since this is an API-only service
        .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
        .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(samlEntryPoint())
        .and()
        .authorizeRequests()
        .antMatchers("/saml/**").permitAll()
        // this path and role constraint is really the only thing specific to our application
        .antMatchers("/gui", "/graphql").hasAnyRole(apiAdminProperties.getRoles())
    ;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers("/favicon.ico");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(samlAuthenticationProvider());
  }

  private FilterChainProxy samlFilter() throws Exception {
    List<SecurityFilterChain> chains = new ArrayList<>();
    chains.add(new DefaultSecurityFilterChain(
        new AntPathRequestMatcher("/saml/login/**"),
        samlEntryPoint()
    ));
    chains.add(new DefaultSecurityFilterChain(
        new AntPathRequestMatcher("/saml/logout/**"),
        samlLogoutFilter()
    ));
    chains.add(new DefaultSecurityFilterChain(
        new AntPathRequestMatcher("/saml/SSO/**"),
        samlWebSSOProcessingFilter()
    ));
    return new FilterChainProxy(chains);
  }

  @Bean
  public WebSSOProfile webSSOprofile() {
    return new WebSSOProfileImpl();
  }

  // SAML 2.0 WebSSO Assertion Consumer
  @Bean
  public WebSSOProfileConsumer webSSOprofileConsumer() {
    final WebSSOProfileConsumerImpl webSSOProfileConsumer = new WebSSOProfileConsumerImpl();
    webSSOProfileConsumer.setResponseSkew(samlProperties.getResponseSkew());
    return webSSOProfileConsumer;
  }

  // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
  @Bean
  public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
    return new WebSSOProfileConsumerHoKImpl();
  }

  @Bean
  public SingleLogoutProfile logoutprofile() {
    return new SingleLogoutProfileImpl();
  }

  @Bean
  public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
    SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
    samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
    samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
    samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    return samlWebSSOProcessingFilter;
  }

  @Bean
  public FilterRegistrationBean disablesamlWebSSOProcessingFilter() throws Exception {
    final FilterRegistrationBean<SAMLProcessingFilter> registration = new FilterRegistrationBean<>(
        samlWebSSOProcessingFilter());
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
    SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
    successLogoutHandler.setDefaultTargetUrl("/admin");
    return successLogoutHandler;
  }

  @Bean
  public SecurityContextLogoutHandler logoutHandler() {
    SecurityContextLogoutHandler logoutHandler =
        new SecurityContextLogoutHandler();
    logoutHandler.setInvalidateHttpSession(true);
    logoutHandler.setClearAuthentication(true);
    return logoutHandler;
  }

  @Bean
  public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
    SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler =
        new SavedRequestAwareAuthenticationSuccessHandler();
    successRedirectHandler.setDefaultTargetUrl("/admin/profile");
    return successRedirectHandler;
  }

  @Bean
  public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    SimpleUrlAuthenticationFailureHandler failureHandler =
        new SimpleUrlAuthenticationFailureHandler();
    failureHandler.setUseForward(true);
    failureHandler.setDefaultFailureUrl("/error");
    return failureHandler;
  }

  @Bean
  public SAMLProcessorImpl processor() {
    Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>();
    bindings.add(httpPostBinding());
    bindings.add(httpRedirectDeflateBinding());
    return new SAMLProcessorImpl(bindings);
  }

  @Bean
  public HTTPPostBinding httpPostBinding() {
    return new HTTPPostBinding(parserPool(), velocityEngine());
  }

  @Bean
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean
  public VelocityEngine velocityEngine() {
    return VelocityFactory.getEngine();
  }

  @Bean(initMethod = "initialize")
  public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
  }

  @Bean
  public SAMLContextProvider contextProvider() throws MalformedURLException {
    if (samlProperties.isUsingLbContextProvider()) {
      final SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();

      final URL baseUrl = new URL(samlProperties.getEntityBaseUrl());
      contextProvider.setScheme(baseUrl.getProtocol());
      contextProvider.setServerName(baseUrl.getHost());
      contextProvider.setServerPort(baseUrl.getPort());
      contextProvider.setContextPath(baseUrl.getPath());

      return contextProvider;
    }
    return new SAMLContextProviderImpl();
  }

  @Bean
  public static SAMLBootstrap sAMLBootstrap() {
    return new SAMLBootstrap();
  }

  @Bean
  @Qualifier("metadata")
  public CachingMetadataManager metadata() throws MetadataProviderException, IOException {
    List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
    providers.add(idpMetadataProvider());
    return new CachingMetadataManager(providers);
  }

  @Bean
  public MetadataProvider idpMetadataProvider() throws IOException, MetadataProviderException {

    final Resource metadataResource = resourceLoader
        .getResource(samlProperties.getMetadataLocation());

    final FilesystemMetadataProvider metadataProvider = new FilesystemMetadataProvider(
        metadataResource.getFile());
    metadataProvider.setParserPool(parserPool());

    return metadataProvider;
  }

  @Bean
  public KeyManager keyManager() {
    Resource storeFile = resourceLoader
        .getResource(samlProperties.getKeystoreLocation());
    Map<String, String> passwords = new HashMap<>();
    final String password = samlProperties.getKeystorePassword();
    final String alias = samlProperties.getKeyAlias();
    passwords.put(alias, samlProperties.getKeyPassword());
    return new JKSKeyManager(storeFile, password, passwords, alias);
  }

  @Bean
  public SAMLEntryPoint samlEntryPoint() {
    SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
    return samlEntryPoint;
  }

  @Bean
  public FilterRegistrationBean disableSAMLEntryPoint() {
    final FilterRegistrationBean registration =
        new FilterRegistrationBean<>(samlEntryPoint());
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    return webSSOProfileOptions;
  }

  @Bean
  public SAMLDefaultLogger samlLogger() {
    return new SAMLDefaultLogger();
  }

  @Bean
  public SAMLLogoutFilter samlLogoutFilter() {
    return new SAMLLogoutFilter(
        successLogoutHandler(),
        new LogoutHandler[]{logoutHandler()},
        new LogoutHandler[]{logoutHandler()}
    );
  }

  @Bean
  public FilterRegistrationBean disableSamlLogoutFilter() {
    final FilterRegistrationBean<SAMLLogoutFilter> registration = new FilterRegistrationBean<>(
        samlLogoutFilter());
    registration.setEnabled(false);
    return registration;
  }

  // Required for SP context injection
  @Bean
  public MetadataGeneratorFilter metadataGeneratorFilter() {
    return new MetadataGeneratorFilter(metadataGenerator());
  }

  @Bean
  public FilterRegistrationBean disablemetadataGeneratorFilter() {
    final FilterRegistrationBean<MetadataGeneratorFilter> registration = new FilterRegistrationBean<>(
        metadataGeneratorFilter());
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  public MetadataGenerator metadataGenerator() {
    MetadataGenerator metadataGenerator = new MetadataGenerator();
    metadataGenerator.setEntityId(samlProperties.getEntityId());
    metadataGenerator.setEntityBaseURL(samlProperties.getEntityBaseUrl());
    metadataGenerator.setExtendedMetadata(extendedMetadata());
    metadataGenerator.setIncludeDiscoveryExtension(false);
    metadataGenerator.setKeyManager(keyManager());
    return metadataGenerator;
  }

  @Bean
  public ExtendedMetadata extendedMetadata() {
    ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    extendedMetadata.setIdpDiscoveryEnabled(false);
    extendedMetadata.setSignMetadata(false);
    extendedMetadata.setEcpEnabled(true);
    return extendedMetadata;
  }

  @Bean
  public SAMLAuthenticationProvider samlAuthenticationProvider() {
    SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
    samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
    samlAuthenticationProvider.setForcePrincipalAsString(false);
    return samlAuthenticationProvider;
  }
}
