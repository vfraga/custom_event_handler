package org.sample.event.handler.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.sample.event.handler.CustomEventHandler;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticationService;
import org.wso2.carbon.identity.core.util.IdentityCoreInitializedEvent;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;

@Component(
        name = "custom.event.handler.bundle",
        immediate = true)
public class CustomEventHandlerBundleComponent {
    private static final Log log = LogFactory.getLog(CustomEventHandlerBundleComponent.class);

    @Activate
    protected void activate(final ComponentContext context) {
        final CustomEventHandler customAuditLogger = new CustomEventHandler();

        final ServiceRegistration<?> serviceRegistrationResult = context.getBundleContext()
                .registerService(AbstractEventHandler.class.getName(), customAuditLogger, null);

        if (serviceRegistrationResult == null) {
            log.error("Error registering CustomEventHandler as a AbstractEventHandler.");
        } else {
            log.info("CustomEventHandler successfully registered as a AbstractEventHandler.");
        }

        log.info("Custom bundle activated.");
    }

    @Deactivate
    protected void deactivate(final ComponentContext ignored) {
        log.info("Custom bundle deactivated.");
    }

    @Reference(
            name = "identity.application.authentication.framework",
            service = ApplicationAuthenticationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationAuthenticationService"
    )
    protected void setApplicationAuthenticationService(final ApplicationAuthenticationService ignored) {
        // do nothing: waiting for ApplicationAuthenticationService to initialise
    }

    protected void unsetApplicationAuthenticationService(final ApplicationAuthenticationService ignored) {
        // do nothing: method declaration for the unbind action for setApplicationAuthenticationService
    }

    @Reference(
            name = "identity.core.init.event.service",
            service = IdentityCoreInitializedEvent.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityCoreInitializedEventService"
    )
    protected void setIdentityCoreInitializedEventService(final IdentityCoreInitializedEvent ignored) {
        // do nothing: waiting for IdentityCoreInitializedEvent to initialise
    }

    protected void unsetIdentityCoreInitializedEventService(final IdentityCoreInitializedEvent ignored) {
        // do nothing: method declaration for the unbind action for setIdentityCoreInitializedEventService
    }
}
