package org.clarksnut.services.resources;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractResource {

    @Inject
    protected UserProvider userProvider;

    protected UserModel getUserSession(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new ForbiddenException();
        }
        return user;
    }

    protected boolean isUserAllowedToViewDocument(UserModel user, DocumentModel document) {
        Set<String> permittedSpaceAssignedIds = user.getAllPermittedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        return permittedSpaceAssignedIds.contains(document.getSupplierAssignedId()) ||
                permittedSpaceAssignedIds.contains(document.getCustomerAssignedId());
    }

    protected Set<SpaceModel> filterAllowedSpaces(UserModel user, Collection<String> spaceIds) {
        return user.getAllPermittedSpaces().stream()
                .filter(p -> spaceIds.contains(p.getAssignedId()))
                .collect(Collectors.toSet());
    }
}