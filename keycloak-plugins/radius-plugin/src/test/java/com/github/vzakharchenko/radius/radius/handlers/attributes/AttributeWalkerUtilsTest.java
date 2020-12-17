package com.github.vzakharchenko.radius.radius.handlers.attributes;

import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class AttributeWalkerUtilsTest {
    @Mock
    private GroupModel groupModel;

    @Mock
    private RoleModel role;

    @Mock
    private RoleModel role1;
    @Mock
    private RoleModel role2;
    @Mock
    private RoleModel role3;

    @Mock
    private GroupModel subGroupModel1;
    @Mock
    private GroupModel subGroupModel2;
    @Mock
    private GroupModel subGroupModel3;


    @BeforeMethod
    public void testBeforeMethods() {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("attribute", Arrays.asList("a1", "a2"));
        map.put("attributeEmpty", Collections.emptyList());
        map.put("attributeNull", null);

        HashMap<String, List<String>> mapRole = new HashMap<>();
        mapRole.put("attributeRole", Collections.singletonList("a1"));
        mapRole.put("attributeRoleEmpty", Collections.emptyList());
        mapRole.put("attributeRoleNull", null);

        reset(groupModel);
        reset(subGroupModel1);
        reset(subGroupModel2);
        reset(subGroupModel3);
        reset(role);
        reset(role1);
        reset(role2);
        reset(role3);

        when(groupModel.getAttributes()).thenReturn(map);
        when(groupModel.getSubGroups())
                .thenReturn(new HashSet<>(Arrays
                        .asList(subGroupModel1, subGroupModel2)));
        when(subGroupModel1.getAttributes()).thenReturn(map);
        when(subGroupModel2.getAttributes()).thenReturn(map);
        when(subGroupModel2.getSubGroups())
                .thenReturn(new HashSet<>(Arrays.asList(subGroupModel3)));
        when(subGroupModel2.getAttributes()).thenReturn(map);
        when(subGroupModel2.getSubGroups()).thenReturn(null);
        when(subGroupModel2.getRoleMappings())
                .thenReturn(new HashSet<>(Collections.singletonList(role2)));

        when(role.getAttributes()).thenReturn(mapRole);
        when(role.isComposite()).thenReturn(true);
        when(role.getComposites())
                .thenReturn(new HashSet<>(Arrays
                        .asList(role1, role2)));
        when(role1.getAttributes()).thenReturn(mapRole);
        when(role2.getAttributes()).thenReturn(mapRole);
        when(role2.getComposites())
                .thenReturn(new HashSet<>(Arrays.asList(role3)));
        when(role3.getAttributes()).thenReturn(map);


    }

    @Test
    public void testGroupWalker() {
        HashMap<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.groupWalker(groupModel, attributes);
        assertNotNull(attributes);
        assertEquals(attributes.size(), 6);
        assertTrue(attributes.containsKey("attribute"));
        assertTrue(attributes.containsKey("attributeRole"));
        assertEquals(attributes.get("attribute").size(), 2);
        assertEquals(attributes.get("attributeRole").size(), 1);
    }

    @Test
    public void testRoleWalker() {
        HashMap<String, Set<String>> attributes = new HashMap<>();
        AttributeWalkerUtils.roleWalker(role, attributes);
        assertNotNull(attributes);
        assertEquals(attributes.size(), 3);
        assertTrue(attributes.containsKey("attributeRole"));
        assertEquals(attributes.get("attributeRole").size(), 1);
    }

    @BeforeClass
    public void beforeRadiusTest() {
        MockitoAnnotations.initMocks(this);
    }
}
