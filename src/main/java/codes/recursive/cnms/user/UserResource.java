/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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

package codes.recursive.cnms.user;

import codes.recursive.cnms.user.model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.TimeZone;

/**
 * Resource for managing users
 */
@Path("/user")
@RequestScoped
public class UserResource {

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private final UserRepository userRepository;

    @Context
    UriInfo uriInfo;

    @Inject
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getDefaultMessage() {
        return JSON.createObjectBuilder()
                .add("OK", true)
                .build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        User user = userRepository.get(id);
        if( user != null ) {
            return Response.ok(user).build();
        }
        else {
            return Response.status(404).build();
        }
    }

    @Path("/list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok(this.userRepository.findAll()).build();
    }

    @Path("/list/{offset}/{max}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersPaginated(@PathParam("offset") int offset, @PathParam("max") int max) {
        return Response.ok(this.userRepository.findAll(offset, max)).build();
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveUser(User user) {
        userRepository.save(user);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("/user/{id}")
                        .build(user.getId())
        ).build();
    }

    @Path("{id}")
    @DELETE
    public Response deletePost(@PathParam("id") String id) {
        userRepository.deleteById(id);
        return Response.noContent().build();
    }


}
