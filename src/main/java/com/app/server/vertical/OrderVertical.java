/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.vertical;

import com.app.server.handler.ChangePasswordHandler;
import com.app.server.handler.CreateShipmentsHandler;
import com.app.server.handler.DashboardHandler;
import com.app.server.handler.LoginHandler;
import com.app.server.handler.OptionHandler;
import com.app.server.handler.OrderNotifyHandler;
import com.app.server.handler.RegisterHandler;
import com.app.server.handler.ShowShipmentsHandler;
import com.app.server.handler.WalletInfoHandler;
import com.app.server.handler.common.ExceptionHandler;
import com.app.server.handler.common.RequestLoggingHandler;
import com.app.server.handler.common.ResponseHandler;
import com.app.util.LoggerInterface;
import com.app.util.StringPool;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.auth.AuthProvider;
import io.vertx.rxjava.ext.auth.shiro.ShiroAuth;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CookieHandler;
import io.vertx.rxjava.ext.web.handler.RedirectAuthHandler;
import io.vertx.rxjava.ext.web.handler.ResponseTimeHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.handler.TimeoutHandler;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;

/**
 *
 * @author hungdt
 */
public class OrderVertical extends AbstractVerticle implements LoggerInterface {

    private String serverHost;
    private int serverPort;
    private boolean connectionKeepAlive;
    private long connectionTimeOut;
    private int connectionIdleTimeOut;
    private String apiPrefix;

    public static HttpClient httpClient;
    public static HttpClient httpsClient;

    //AuthProvider authProvider = ShiroAuth.create(vertx, new ShiroAuthOptions());
    
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setConnectionKeepAlive(boolean connectionKeepAlive) {
        this.connectionKeepAlive = connectionKeepAlive;
    }

    public void setConnectionTimeOut(long connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public void setConnectionIdleTimeOut(int connectionIdleTimeOut) {
        this.connectionIdleTimeOut = connectionIdleTimeOut;
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    @Override
    public void start() throws Exception {

        logger.info("[INIT] STARTING UP ORDER API SERVER...");

        httpClient = vertx.createHttpClient();
        httpsClient = vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true));

        super.start();

        Router router = Router.router(vertx);
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        // router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider));

        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(TimeoutHandler.create(connectionTimeOut));
        router.route().handler(new RequestLoggingHandler());

        router.route().handler(SessionHandler
                .create(LocalSessionStore.create(vertx, "test tts", 30000))
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(true)
        );
        
        
        //AuthHandler redirectAuthHandler = RedirectAuthHandler.create(authProvider);

        router.mountSubRouter(apiPrefix, initAPI());

        router.route().failureHandler(new ExceptionHandler());
        router.route().last().handler(new ResponseHandler());

        HttpServerOptions httpServerOptions = new HttpServerOptions();

        httpServerOptions.setHost(serverHost);
        httpServerOptions.setPort(serverPort);
        httpServerOptions.setTcpKeepAlive(connectionKeepAlive);
        httpServerOptions.setIdleTimeout(connectionIdleTimeOut);

        HttpServer httpServer = vertx.createHttpServer(httpServerOptions);

        httpServer.requestHandler(router);

        httpServer.listen(result -> {
            if (result.failed()) {
                logger.error("[INIT] START ORDER API ERROR " + result.cause());
            } else {
                logger.info("[INIT] ORDER STARTED AT " + StringPool.SPACE + serverHost + StringPool.COLON + serverPort);
            }
        });
    }

    private Router initAPI() {

        Router router = Router.router(vertx);
        //xet uri de xem handler nao se bat login, handler nao khong bat login
       // router.route("/private/*").handler(RedirectAuthHandler.create(authProvider));
        
        router.route(HttpMethod.POST, "/notifyOrder/:source").handler(new OrderNotifyHandler());
        router.route(HttpMethod.OPTIONS, "/login").handler(new OptionHandler());
        router.route(HttpMethod.POST, "/login").handler(new LoginHandler());
        router.route(HttpMethod.POST, "/register").handler(new RegisterHandler());
        router.route(HttpMethod.POST, "/changePassword").handler(new ChangePasswordHandler());
        router.route(HttpMethod.GET, "/wallet/:sessionId").handler(new WalletInfoHandler());
        router.route(HttpMethod.GET, "/dashboard/:sessionId").handler(new DashboardHandler());
        router.route(HttpMethod.POST, "/showShipments").handler(new ShowShipmentsHandler());
        router.route(HttpMethod.POST, "/createShipments").handler(new CreateShipmentsHandler());
        return router;
    }
}
