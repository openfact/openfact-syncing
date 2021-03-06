package org.clarksnut.models.jpa;

import org.clarksnut.models.*;
import org.clarksnut.models.jpa.entity.RequestEntity;
import org.clarksnut.models.jpa.entity.SpaceEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class JpaRequestProvider implements RequestProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RequestModel addRequest(SpaceModel space, UserModel user, PermissionType permission, String message) {
        RequestEntity entity = new RequestEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setPermission(permission);
        entity.setMessage(message);
        entity.setStatus(RequestStatus.PENDING);
        entity.setSpace(SpaceAdapter.toEntity(space, em));
        entity.setUser(UserAdapter.toEntity(user, em));

        em.persist(entity);
        return new RequestAdapter(em, entity);
    }

    @Override
    public RequestModel getRequest(String id) {
        RequestEntity entity = em.find(RequestEntity.class, id);
        if (entity == null) return null;
        return new RequestAdapter(em, entity);
    }

    @Override
    public List<RequestModel> getRequests(RequestStatus status, SpaceModel... space) {
        if (space == null || space.length == 0) {
            return Collections.emptyList();
        }

        Set<SpaceEntity> spaceEntities = Arrays.stream(space).map(f -> SpaceAdapter.toEntity(f, em)).collect(Collectors.toSet());

        TypedQuery<RequestEntity> query = em.createNamedQuery("getRequestsByStatusAndSpaces", RequestEntity.class);
        query.setParameter("status", status);
        query.setParameter("spaces", spaceEntities);
        return query.getResultList().stream()
                .map(f -> new RequestAdapter(em, f))
                .collect(Collectors.toList());
    }
}
