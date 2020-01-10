package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.keycloak.models.UserModel;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;

public interface IRadiusConfigJPA {
    RadiusConfigModel getConfig();

    RadiusConfigModel saveConfig(RadiusConfigModel configModel, UserModel userModel);
}
