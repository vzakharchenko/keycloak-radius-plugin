package com.github.vzakharchenko.radius.test;

import com.github.vzakharchenko.radius.RadiusHelper;
import com.github.vzakharchenko.radius.client.RadiusLoginProtocolFactory;
import com.github.vzakharchenko.radius.coa.IRadiusCoAClient;
import com.github.vzakharchenko.radius.coa.RadiusCoAClientHelper;
import com.github.vzakharchenko.radius.configuration.IRadiusConfiguration;
import com.github.vzakharchenko.radius.configuration.RadiusConfigHelper;
import com.github.vzakharchenko.radius.mappers.RadiusPasswordMapper;
import com.github.vzakharchenko.radius.password.RadiusCredentialModel;
import com.github.vzakharchenko.radius.providers.IRadiusServiceProvider;
import com.github.vzakharchenko.radius.radius.dictionary.DictionaryLoader;
import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocol;
import com.github.vzakharchenko.radius.radius.handlers.protocols.AuthProtocolFactory;
import com.github.vzakharchenko.radius.radius.handlers.protocols.RadiusAuthProtocolFactory;
import com.github.vzakharchenko.radius.radius.handlers.session.AccountingSessionManager;
import com.github.vzakharchenko.radius.radius.handlers.session.KeycloakSessionUtils;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfo;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoBuilder;
import com.github.vzakharchenko.radius.radius.holder.IRadiusUserInfoGetter;
import com.github.vzakharchenko.radius.radius.server.KeycloakRadiusServer;
import com.github.vzakharchenko.radius.transaction.KeycloakHelper;
import com.github.vzakharchenko.radius.transaction.KeycloakRadiusUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.ClientConnection;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.provider.Provider;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.tinyradius.attribute.AttributeType;
import org.tinyradius.dictionary.DictionaryParser;
import org.tinyradius.dictionary.WritableDictionary;
import org.tinyradius.packet.AccessRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;

public abstract class AbstractRadiusTest {

    public static final String RADIUS_SESSION_ID = "testSessionId";
    public static final String REALM_RADIUS = "realm-radius";

    {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String REALM_RADIUS_NAME = "RadiusName";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String USER = "USER";
    @Mock
    protected KeycloakSession session;

    @Mock
    protected KeycloakTransactionManager keycloakTransactionManager;

    @Mock
    protected KeycloakSessionFactory keycloakSessionFactory;

    @Mock
    protected RealmModel realmModel;

    @Mock
    protected ClientModel clientModel;

    @Mock
    protected UserModel userModel;

    @Mock
    protected UserProvider userProvider;

    @Mock
    protected IRadiusConfiguration configuration;
    @Mock
    protected RoleModel radiusRole;

    @Mock
    protected UserCredentialManager userCredentialManager;

    protected UserSessionProvider userSessionProvider;
    @Mock
    protected UserSessionModel userSessionModel;
    @Mock
    protected AuthenticatedClientSessionModel authenticatedClientSessionModel;

    @Mock
    protected ClientConnection clientConnection;

    @Mock
    protected AuthProtocolFactory radiusAuthProtocolFactory;
    @Mock
    protected AuthProtocol authProtocol;
    @Mock
    protected KeycloakContext keycloakContext;

    @Mock
    protected KeycloakUriInfo keycloakUriInfo;
    @Mock
    protected HttpHeaders httpHeaders;

    @Mock
    protected KeycloakHelper keycloakHelper;

    @Mock
    protected EntityManager entityManager;

    @Mock
    protected TypedQuery query;

    @Mock
    protected IRadiusServiceProvider radiusServiceProvider;

    protected WritableDictionary realDictionary;

    @Mock
    protected IRadiusUserInfoBuilder radiusUserInfoBuilder;
    @Mock
    protected IRadiusUserInfoGetter radiusUserInfoGetter;
    @Mock
    protected IRadiusUserInfo radiusUserInfo;

    @Mock
    protected IRadiusCoAClient radiusCoAClient;

    protected EventBuilder eventBuilder;
    protected AccessToken accessToken;


    protected Map<Class, Provider> providerByClass = new HashMap<>();

    protected List<? extends Object> resetMock() {
        return null;
    }


    void resetStatic() {
        try {
            RadiusConfigHelper.setConfiguration(configuration);
            RadiusAuthProtocolFactory.setInstance(radiusAuthProtocolFactory);
            KeycloakRadiusUtils.setKeycloakHelper(keycloakHelper);
            when(keycloakHelper.getAuthResult(session))
                    .thenReturn(new AuthenticationManager.AuthResult(userModel,
                            userSessionModel, accessToken));
            RadiusHelper.setRealmAttributes(Collections.singletonList(REALM_RADIUS));
            RadiusHelper.getServiceMap0().clear();
            RadiusHelper.getServiceMap0().put("sName", new ArrayList<>(Collections
                    .singletonList(radiusServiceProvider)));
            KeycloakSessionUtils.context(session, radiusUserInfoGetter);
            RadiusCoAClientHelper.setRadiusCoAClient(radiusCoAClient);
            DictionaryLoader.getInstance().setWritableDictionary(realDictionary);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @BeforeMethod
    public void beforeRadiusMethod() {
        reset(session);
        reset(keycloakTransactionManager);
        reset(keycloakSessionFactory);
        reset(realmModel);
        reset(clientModel);
        reset(userModel);
        reset(userProvider);
        reset(configuration);
        reset(radiusRole);
        reset(userCredentialManager);
        reset(userSessionModel);
        reset(clientConnection);
        reset(radiusAuthProtocolFactory);
        reset(authProtocol);
        reset(keycloakContext);
        reset(httpHeaders);
        reset(keycloakUriInfo);
        reset(entityManager);
        reset(radiusServiceProvider);
        reset(authenticatedClientSessionModel);
        reset(query);
        reset(radiusUserInfo);
        reset(radiusUserInfoBuilder);
        reset(radiusCoAClient);
        reset(radiusUserInfoGetter);
        providerByClass.clear();
        accessToken = new AccessToken();
        Answer<Object> providerAnswer = invocation -> {
            Object parameter = invocation.getArguments()[0];
            Class<? extends Provider> classToMock = (Class<? extends Provider>) parameter;
            Provider provider = providerByClass.get(classToMock);
            if (provider == null && classToMock != null) {
                provider = mock(classToMock);
                providerByClass.put(classToMock, provider);
            }
            return provider;
        };
        Answer<Object> providerAnswers = invocation -> {
            Object parameter = invocation.getArguments()[0];
            Class<? extends Provider> classToMock = (Class<? extends Provider>) parameter;
            Provider provider = providerByClass.get(classToMock);
            if (provider == null && classToMock != null) {
                provider = mock(classToMock);
                providerByClass.put(classToMock, provider);
            }
            HashSet<Object> set = new HashSet<>();
            set.add(provider);
            return set;
        };

        when(keycloakSessionFactory.create()).thenReturn(session);
        when(session.getProvider(any())).thenAnswer(providerAnswer);
        when(session.getProvider(any(), anyString())).thenAnswer(providerAnswer);
        when(session.getAllProviders(any())).thenAnswer(providerAnswers);
        when(session.getTransactionManager()).thenReturn(keycloakTransactionManager);
        when(session.getContext()).thenReturn(keycloakContext);
        when(keycloakContext.getRealm()).thenReturn(realmModel);
        when(keycloakContext.getConnection()).thenReturn(clientConnection);
        when(keycloakContext.getClient()).thenReturn(clientModel);
        when(keycloakContext.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakContext.getRequestHeaders()).thenReturn(httpHeaders);
        when(radiusUserInfo.getActivePassword()).thenReturn("secret");
        when(radiusUserInfo.getClientConnection()).thenReturn(clientConnection);
        when(radiusUserInfo.getUserModel()).thenReturn(userModel);
        when(radiusUserInfo.getRealmModel()).thenReturn(realmModel);
        when(radiusUserInfo.getRadiusSecret()).thenReturn("secret");
        when(radiusUserInfo.getClientModel()).thenReturn(clientModel);
        when(radiusUserInfo.getPasswords()).thenReturn(Collections.singletonList("secret"));
        when(radiusUserInfo.getAddress()).thenReturn(new InetSocketAddress(0));
        when(radiusUserInfoGetter.getBuilder()).thenReturn(radiusUserInfoBuilder);
        when(radiusUserInfoGetter.getRadiusUserInfo()).thenReturn(radiusUserInfo);
        when(radiusUserInfoBuilder.getRadiusUserInfoGetter()).thenReturn(radiusUserInfoGetter);
        when(session.getAttribute("RADIUS_INFO", IRadiusUserInfoGetter.class))
                .thenReturn(radiusUserInfoGetter);
        when(keycloakTransactionManager.isActive()).thenReturn(true);
        when(keycloakTransactionManager.getRollbackOnly()).thenReturn(false);
        when(session.userCredentialManager()).thenReturn(userCredentialManager);
        when(session.getKeycloakSessionFactory()).thenReturn(keycloakSessionFactory);
        userSessionProvider = session.getProvider(UserSessionProvider.class);
        assertNotNull(userSessionProvider);
        when(session.sessions()).thenReturn(userSessionProvider);
        RealmProvider realmProvider = getProvider(RealmProvider.class);
        when(session.realms()).thenReturn(realmProvider);
        when(clientModel.getRealm()).thenReturn(realmModel);
        when(clientModel.getClientId()).thenReturn(CLIENT_ID);
        when(clientModel.getProtocol()).thenReturn(RadiusLoginProtocolFactory.RADIUS_PROTOCOL);
        when(realmProvider.getRealm(REALM_RADIUS_NAME)).thenReturn(realmModel);
        when(realmProvider.getRealm(anyString())).thenReturn(realmModel);
        when(realmProvider.getRealms()).thenReturn(Arrays.asList(realmModel));
        when(realmModel.getClientByClientId(CLIENT_ID)).thenReturn(clientModel);
        when(realmModel.getName()).thenReturn(REALM_RADIUS_NAME);
        when(realmModel.getId()).thenReturn(REALM_RADIUS_NAME);
        when(realmModel.isEventsEnabled()).thenReturn(false);
        when(realmModel.getAttributes()).thenReturn(new HashMap<>());
        when(realmModel.getClients()).thenReturn(Arrays.asList(clientModel));
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserByUsername(USER, realmModel)).thenReturn(userModel);
        when(userProvider.getUserByEmail(USER, realmModel)).thenReturn(userModel);
        when(userModel.getUsername()).thenReturn(USER);
        when(userModel.getEmail()).thenReturn(USER);
        when(userModel.isEnabled()).thenReturn(true);
        when(userModel.hasRole(radiusRole)).thenReturn(true);
        when(userModel.getAttributes()).thenReturn(new HashMap<>());
        when(configuration.getRadiusSettings())
                .thenReturn(ModelBuilder.createRadiusServerSettings());
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(Collections
                        .singletonList(ModelBuilder.createCredentialModel()));
        when(userSessionProvider.getUserSessions(realmModel, userModel))
                .thenReturn(Arrays.asList(userSessionModel));
        when(userSessionProvider.getUserSession(eq(realmModel), anyString()))
                .thenReturn(userSessionModel);
        when(userSessionProvider
                .createUserSession(any(), any(), anyString(), anyString(), anyString(), eq(false), anyString(), anyString()))
                .thenReturn(userSessionModel);
        when(userSessionProvider
                .createUserSession(any()
                        , any(), any(),
                        any(),
                        eq("radius"), eq(false), isNull(), isNull()))
                .thenReturn(userSessionModel);
        when(userSessionProvider
                .createClientSession(realmModel, clientModel, userSessionModel))
                .thenReturn(authenticatedClientSessionModel);
        when(userSessionProvider.getUserSessions(realmModel, clientModel)).thenReturn(Collections.singletonList(userSessionModel));
        when(userSessionModel.getNote(RadiusPasswordMapper.RADIUS_SESSION_PASSWORD))
                .thenReturn("123");
        when(userSessionModel.getRealm()).thenReturn(realmModel);
        when(userSessionModel.getUser()).thenReturn(userModel);
        HashMap<String, AuthenticatedClientSessionModel> sessionModelHashMap = new HashMap<>();
        sessionModelHashMap.put("id", authenticatedClientSessionModel);
        when(authenticatedClientSessionModel.getNote(AccountingSessionManager.RADIUS_SESSION_ID)).thenReturn(RADIUS_SESSION_ID);
        when(authenticatedClientSessionModel.getUserSession()).thenReturn(userSessionModel);
        when(userSessionModel.getAuthenticatedClientSessions()).thenReturn(
                sessionModelHashMap);
        when(clientConnection.getRemoteAddr()).thenReturn("111.111.111.112");
        when(authProtocol.getRealm()).thenReturn(realmModel);
        when(authProtocol.verifyPassword(anyString())).thenReturn(true);
        when(authProtocol.isValid(any())).thenReturn(true);

        JpaConnectionProvider jpaConnectionProvider = session
                .getProvider(JpaConnectionProvider.class);
        when(jpaConnectionProvider.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createQuery(anyString(), any())).thenReturn(query);


        eventBuilder = new EventBuilder(realmModel, session, clientConnection);
        List<?> objects = resetMock();
        if (objects != null) {
            reset(objects.toArray(new Object[objects.size()]));
        }
        resetStatic();
        initDictionary();
        when(authProtocol.getAccessRequest()).thenReturn(
                new AccessRequest(realDictionary, 0, new byte[16]));
    }

    protected <T extends Provider> T getProvider(Class<T> providerClass) {
        return session.getProvider(providerClass);
    }

    @BeforeClass
    public void beforeRadiusTest() {
        MockitoAnnotations.initMocks(this);
    }

    void initDictionary() {
        try {
            DictionaryParser dictionaryParser = DictionaryParser.newClasspathParser();
            realDictionary = dictionaryParser
                    .parseDictionary("org/tinyradius/dictionary/default_dictionary");
            dictionaryParser
                    .parseDictionary(realDictionary,
                            KeycloakRadiusServer.MS);
            realDictionary.addAttributeType(new AttributeType(253, REALM_RADIUS, "string"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
