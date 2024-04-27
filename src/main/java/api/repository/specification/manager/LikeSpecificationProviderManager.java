package api.repository.specification.manager;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;

public interface LikeSpecificationProviderManager<T> {

    LikeSpecificationProvider<User> getLikeSpecificationProvider(String key);
}
