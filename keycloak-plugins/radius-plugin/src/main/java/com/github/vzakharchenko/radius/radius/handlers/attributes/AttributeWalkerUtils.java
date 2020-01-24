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
        Set<GroupModel> subGroups = groupModel.getSubGroups();
        if (subGroups != null) {
            for (GroupModel subGroup : subGroups) {
                groupWalker(subGroup, attributes);
            }
        }
        Set<RoleModel> roleMappings = groupModel.getRoleMappings();
        if (roleMappings != null) {
            for (RoleModel roleModel : roleMappings) {
                roleWalker(roleModel, attributes);
            }
        }
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
            Set<RoleModel> composites = roleModel.getComposites();
            for (RoleModel r : composites) {
                roleWalker(r, attributes);
            }
        }
        mergeMaps(roleModel.getAttributes(), attributes);
    }
}
