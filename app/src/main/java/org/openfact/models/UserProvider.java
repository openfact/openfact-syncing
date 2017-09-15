package org.openfact.models;

import java.util.List;

public interface UserProvider {

    UserModel addUser(String identityID, String providerType, String username);

    UserModel getUser(String userId);

    UserModel getUserByUsername(String username);

    UserModel getUserByIdentityID(String identityID);

    List<UserModel> getUsers();

    List<UserModel> getUsers(QueryModel query);

    ScrollableResultsModel<UserModel> getScrollableUsers();

    /**
     * */
    void updateUser(String identityID, String offlineToken, boolean registrationComplete);
}
