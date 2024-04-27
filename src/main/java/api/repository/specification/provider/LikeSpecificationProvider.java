package api.repository.specification.provider;

import org.springframework.data.jpa.domain.Specification;

public interface LikeSpecificationProvider<T> {

    Specification<T> getSpecification(String param);

    String getKey();
}
