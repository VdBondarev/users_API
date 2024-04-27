package api.repository.specification.provider;

import org.springframework.data.jpa.domain.Specification;

public interface BetweenSpecificationProvider<T, P> {

    Specification<T> getSpecification(P params);

    String getKey();
}
