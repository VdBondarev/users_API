package api.repository.specification.user.manager;

import api.model.User;
import api.repository.specification.manager.BetweenSpecificationProviderManager;
import api.repository.specification.provider.BetweenSpecificationProvider;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBetweenSpecificationProviderManager
        implements BetweenSpecificationProviderManager<User, List<LocalDate>> {
    private final List<BetweenSpecificationProvider<User, List<LocalDate>>>
            specificationProviders;

    @Override
    public BetweenSpecificationProvider<User, List<LocalDate>> getBetweenSpecificationProvider(
            String key) {
        return specificationProviders
                .stream()
                .filter(provider -> provider.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find specification for key " + key));
    }
}
