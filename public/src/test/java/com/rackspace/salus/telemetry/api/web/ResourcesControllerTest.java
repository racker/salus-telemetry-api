/*
 * Copyright 2019 Rackspace US, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rackspace.salus.telemetry.api.web;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@WebMvcTest(ResourcesController.class)
@AutoConfigureWebClient
@AutoConfigureMockRestServiceServer
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ResourcesControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServicesProperties servicesProperties() {
            final ServicesProperties servicesProperties = new ServicesProperties();
            servicesProperties.setResourceManagementUrl("");
            return servicesProperties;
        }
    }

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;


    String resourceIdDescription = "Machine readable identifier for this Resource";
    //eventually the following FieldDescriptor code should

    final FieldDescriptor[] resourceCreate = new FieldDescriptor[] {
            fieldWithPath("resourceId").description("Machine readable identifier for this Resource"),
            subsectionWithPath("labels").description("JSON list of tuples that will be used to link Monitors to the Resource"),
            subsectionWithPath("metadata").description(""),
            fieldWithPath("presenceMonitoringEnabled").description("Boolean determining whether to enable presence monitoring"),
            fieldWithPath("region").description("The Region that this Resource lives in")
    };

    final FieldDescriptor[] resourceUpdate = new FieldDescriptor[] {
            subsectionWithPath("labels").description("JSON list of tuples that will be used to link Monitors to the Resource"),
            subsectionWithPath("metadata").description(""),
            fieldWithPath("presenceMonitoringEnabled").description("Boolean determining whether to enable presence monitoring"),
            fieldWithPath("region").description("The Region that this Resource lives in")
    };

    final FieldDescriptor[] resource = new FieldDescriptor[] {
            fieldWithPath("resourceId").description("Machine readable identifier for this Resource"),
            subsectionWithPath("labels").description("JSON list of tuples that will be used to link Monitors to the Resource"),
            subsectionWithPath("metadata").description("Information about the server useful for Monitor Templating"),
            fieldWithPath("presenceMonitoringEnabled").description("Boolean determining whether to enable presence monitoring"),
            fieldWithPath("tenantId").description("The id of the account that owns this Resource"),
            fieldWithPath("region").description("Region that this Resource lives in"),
            fieldWithPath("id").description("System unique identifier")
    };

    final FieldDescriptor[] pagedResource = new FieldDescriptor[] {
            fieldWithPath("content.[].resourceId").description("Machine readable identifier for this Resource"),
            subsectionWithPath("content.[].labels").description("JSON list of tuples that will be used to link Monitors to the Resource"),
            subsectionWithPath("content.[].metadata").description("Information about the server useful for Monitor Templating"),
            fieldWithPath("content.[].presenceMonitoringEnabled").description("Boolean determining whether to enable presence monitoring"),
            fieldWithPath("content.[].tenantId").description("The id of the account that owns this Resource"),
            fieldWithPath("content.[].region").description("Region that this Resource lives in"),
            fieldWithPath("content.[].id").description("System unique identifier"),
            //start of the pageable object
            fieldWithPath("first").description(""),
            fieldWithPath("empty").description(""),
            fieldWithPath("last").description(""),
            fieldWithPath("totalElements").description(""),
            fieldWithPath("totalPages").description(""),
            fieldWithPath("size").description(""),
            fieldWithPath("number").description(""),
            fieldWithPath("numberOfElements").description(""),
            fieldWithPath("sort.sorted").description(""),
            fieldWithPath("sort.unsorted").description(""),
            fieldWithPath("sort.empty").description(""),
            fieldWithPath("pageable.sort.sorted").description(""),
            fieldWithPath("pageable.sort.unsorted").description(""),
            fieldWithPath("pageable.sort.empty").description(""),
            fieldWithPath("pageable.offset").description(""),
            fieldWithPath("pageable.pageSize").description(""),
            fieldWithPath("pageable.pageNumber").description(""),
            fieldWithPath("pageable.paged").description(""),
            fieldWithPath("pageable.unpaged").description("")
    };

    @Before
    public void setup() {

    }

    @Test
    public void testGetAllResources() throws Exception {

        final String content = readContent("/resourceOutput.json");

        when(userService.currentTenantId())
                .thenReturn("t-1");

        server.expect(requestTo("/api/tenant/t-1/resources?size=100&page=0"))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andDo(document("getResources", responseFields(pagedResource)));
    }

    @Test
    public void testCreateResource() throws Exception {
        final String content = readContent("/resourceCreate.json");
        final String outputContent = readContent("/nonPagedResource.json");

        when(userService.currentTenantId())
                .thenReturn("t-1");

        server.expect(requestTo("/api/tenant/t-1/resources"))
                .andRespond(withSuccess(outputContent, MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/resources").
                contentType(MediaType.APPLICATION_JSON)
                .content(content))
                    .andExpect(status().isOk())
                    .andDo(document("createResources", requestFields(resourceCreate), responseFields(resource)));
    }

    @Test
    public void testUpdateResource() throws Exception {
        final String content = readContent("/resourceUpdate.json");
        final String outputContent = readContent("/nonPagedResource.json");

        when(userService.currentTenantId())
                .thenReturn("t-1");

        server.expect(requestTo("/api/tenant/t-1/resources/abcd"))
                .andRespond(withSuccess(outputContent, MediaType.APPLICATION_JSON));

        mockMvc.perform(put("/api/resources/abcd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                    .andExpect(status().isOk())
                    .andDo(document("updateResource",requestFields(resourceUpdate), responseFields(resource)));
    }

    @Test
    public void testDeleteResource() throws Exception {

        when(userService.currentTenantId())
                .thenReturn("t-1");
        String resourceId = "abcd";
        server.expect(requestTo("/api/tenant/t-1/resources/" + resourceId))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(delete("/api/resources/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andDo(document("deleteResources", pathParameters(
                        parameterWithName("resourceId").description(resourceIdDescription)
                )));
    }

    private static String readContent(String resource) throws IOException {
        try (InputStream in = new ClassPathResource(resource).getInputStream()) {
            return FileCopyUtils.copyToString(new InputStreamReader(in));
        }
    }
}