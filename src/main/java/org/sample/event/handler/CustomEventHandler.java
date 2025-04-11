package org.sample.event.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.event.IdentityEventConstants.EventName;
import org.wso2.carbon.identity.event.IdentityEventConstants.EventProperty;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.openidconnect.OIDCConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CustomEventHandler extends AbstractEventHandler {
    private static final Log log = LogFactory.getLog(CustomEventHandler.class);
    private static final String HANDLER_NAME = "CustomEventHandler";
    private static final int PRIORITY = 50;

    private static final Set<String> SUBSCRIPTIONS = new HashSet<>(Arrays.asList(
            EventName.SESSION_TERMINATE.name(),
            EventName.SESSION_EXPIRE.name(),
            OIDCConstants.Event.POST_REVOKE_ACESS_TOKEN_BY_ID,
            OIDCConstants.Event.POST_REVOKE_ACESS_TOKEN
    ));

    private static Map<String, Object> getSecureObjectCastToMap(final Object in) {
        final Map<String, Object> outMap = new HashMap<>();
        if (in instanceof Map) {
            // validate the key and value type to prevent UncheckedCast and CastException
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) in).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() != null) {
                    outMap.put((String) entry.getKey(), entry.getValue());
                }
            }
        }
        return outMap;
    }

    @Override
    public void handleEvent(final Event event) throws IdentityEventException {
        log.debug(event.getEventName() + " event received to CustomEventHandler.");

        // Double check if the event is what we are interested in, though `canHandle` already verifies this
        if (!SUBSCRIPTIONS.contains(event.getEventName())) return;

        final Map<String, Object> eventProperties = event.getEventProperties();

        try {
            final HttpServletRequest request = (HttpServletRequest) eventProperties.get(EventProperty.REQUEST);
            final AuthenticationContext context = (AuthenticationContext) eventProperties.get(EventProperty.CONTEXT);

            // Perform your custom logic here
            // For example, try logging the session ID and authenticated user if there's any data in the properties
            final Map<String, Object> params = getSecureObjectCastToMap(eventProperties.get(EventProperty.PARAMS));

            final Object sessionId = params.get(FrameworkConstants.AnalyticsAttributes.SESSION_ID);
            final Object authenticatedUser = params.get(FrameworkConstants.AnalyticsAttributes.USER);

            if (sessionId instanceof String && authenticatedUser instanceof AuthenticatedUser) {
                log.info(String.format("Session ID: %s, Authenticated User: %s", sessionId, authenticatedUser));
            } else {
                log.info("Session ID or Authenticated User is not available in the event properties.");
            }
        } catch (final Exception e) {
            log.error("Error while handling event: " + event.getEventName(), e);
        }
    }

    @Override
    public String getName() {
        return HANDLER_NAME;
    }

    @Override
    public int getPriority(MessageContext messageContext) {
        return PRIORITY;
    }

    private String getProperty(final String key) {
        return configs.getModuleProperties().getProperty(String.format("%s.%s", getName(), key));
    }
}