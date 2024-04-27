package api.repository.specification.manager;

import api.repository.specification.provider.BetweenSpecificationProvider;

public interface BetweenSpecificationProviderManager<T, P> {

    BetweenSpecificationProvider<T, P> getBetweenSpecificationProvider(String key);
}
