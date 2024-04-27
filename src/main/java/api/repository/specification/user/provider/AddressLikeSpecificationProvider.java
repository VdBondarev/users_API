package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.ADDRESS_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.PERCENT;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AddressLikeSpecificationProvider
        implements LikeSpecificationProvider<User> {

    @Override
    public Specification<User> getSpecification(String address) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                        root.get(ADDRESS_COLUMN), PERCENT + address + PERCENT
        );
    }

    @Override
    public String getKey() {
        return ADDRESS_COLUMN;
    }
}
