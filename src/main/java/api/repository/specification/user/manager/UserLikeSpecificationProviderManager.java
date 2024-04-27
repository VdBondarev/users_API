package api.repository.specification.user.manager;

import api.model.User;
import api.repository.specification.manager.LikeSpecificationProviderManager;
import api.repository.specification.provider.LikeSpecificationProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLikeSpecificationProviderManager
        implements LikeSpecificationProviderManager<User> {
    private final List<LikeSpecificationProvider<User>> specificationProviders;

    @Override
    public LikeSpecificationProvider<User> getLikeSpecificationProvider(String key) {
        return specificationProviders
                .stream()
                .filter(provider -> provider.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find specification for key " + key));
    }
}
