package org.openfact.models.utils;

import org.openfact.models.*;
import org.openfact.representation.idm.*;
import org.openfact.services.resources.SpacesService;
import org.openfact.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation.Data toRepresentation(UserModel model, UriInfo uriInfo) {
        UserRepresentation.Data rep = new UserRepresentation.Data();

        rep.setId(model.getIdentityID());
        rep.setType(ModelType.IDENTITIES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getIdentityID());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        UserAttributesRepresentation attributes = new UserAttributesRepresentation();
        attributes.setUserID(model.getId());
        attributes.setIdentityID(model.getIdentityID());
        attributes.setProviderType(model.getProviderType());
        attributes.setUsername(model.getUsername());
        attributes.setFullName(model.getFullName());
        attributes.setRegistrationCompleted(model.isRegistrationCompleted());
        attributes.setBio(model.getBio());
        attributes.setEmail(model.getEmail());
        attributes.setCompany(model.getCompany());
        attributes.setImageURL(model.getImageURL());
        attributes.setUrl(model.getUrl());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        rep.setAttributes(attributes);
        return rep;
    }

    public SpaceRepresentation.Data toRepresentation(SpaceModel model, UriInfo uriInfo) {
        SpaceRepresentation.Data rep = new SpaceRepresentation.Data();

        rep.setId(model.getId());
        rep.setType(ModelType.SPACES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpace")
                .build(model.getId());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Relationships
        SpaceRepresentation.Relationships relationships = new SpaceRepresentation.Relationships();
        SpaceRepresentation.OwnedBy ownedBy = new SpaceRepresentation.OwnedBy();
        relationships.setOwnedBy(ownedBy);
        rep.setRelationships(relationships);

        UserRepresentation.Data userData = new UserRepresentation.Data();
        userData.setId(model.getOwner().getIdentityID());
        userData.setType(ModelType.IDENTITIES.getAlias());
        ownedBy.setData(userData); // save

        GenericLinksRepresentation ownedLinks = new GenericLinksRepresentation();
        ownedLinks.setSelf(uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getOwner().getIdentityID()).toString());
        ownedBy.setLinks(ownedLinks); // save

        // Attributes
        SpaceRepresentation.Attributes attributes = new SpaceRepresentation.Attributes();
        rep.setAttributes(attributes);

        attributes.setName(model.getName());
        attributes.setAssignedId(model.getAssignedId());
        attributes.setDescription(model.getDescription());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        return rep;
    }

//    public SpaceRepresentation toRepresentation(SharedSpaceModel model) {
//        SpaceRepresentation rep = toRepresentation(model.getSpace(), false);
//        rep.setPermissions(model.getPermissions().stream().map(PermissionType::getName).collect(Collectors.toList()));
//        return rep;
//    }
//
//    public RequestAccessToSpaceRepresentation toRepresentation(RequestAccessToSpaceModel model) {
//        RequestAccessToSpaceRepresentation rep = new RequestAccessToSpaceRepresentation();
//
//        rep.setMessage(model.getMessage());
//        rep.setPermissions(model.getPermissions().stream().map(PermissionType::getName).collect(Collectors.toList()));
//        rep.setStatus(model.getStatus().getName());
//
//        return rep;
//    }
//
//    public RepositoryRepresentation toRepresentation(UserRepositoryModel model) {
//        RepositoryRepresentation rep = new RepositoryRepresentation();
//
//        rep.setId(model.getId());
//        rep.setType(model.getType().getName());
//        rep.setEmail(model.getEmail());
//        rep.setLasTimeSynchronized(model.getLastTimeSynchronized());
//
//        return rep;
//    }

}