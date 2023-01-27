package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AttributeWalkerUtils {

    private AttributeWalkerUtils() {
    }

    public static void groupWalker(GroupModel groupModel, Map<String, Set<String>> attributes) {
        groupModel.getSubGroupsStream().forEach(subGroup -> groupWalker(subGroup, attributes));

        groupModel.getRoleMappingsStream().forEach(roleModel -> roleWalker(roleModel, attributes));

        Map<String, List<String>> modelAttributes = groupModel.getAttributes();
        mergeMaps(modelAttributes, attributes);
    }


    private static void mergeMaps(
            Map<String, List<String>> modelAttributes1,
            Map<String, Set<String>> modelAttributes2
    ) {
        modelAttributes1.forEach((s, strings) -> {
            Set<String> set = modelAttributes2.get(s);
            if (set == null) {
                set = new HashSet<>();
                modelAttributes2.put(s, set);
            }
            if (strings != null && !strings.isEmpty()) {
                set.addAll(strings);
            }
        });
    }

    public static void roleWalker(RoleModel roleModel, Map<String, Set<String>> attributes) {
        if (roleModel.isComposite()) {
            roleModel.getCompositesStream().forEach(compRole -> roleWalker(compRole, attributes));
        }
        mergeMaps(roleModel.getAttributes(), attributes);
    }
}
