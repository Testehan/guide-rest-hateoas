// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.microprofile.util;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

public class InventoryUtil {

    private static final int PORT = 9080;
    private static final String PROTOCOL = "http";
    private static final String SYSTEM_PROPERTIES = "/system/properties";

    public static JsonObject getProperties(String hostname) {
        Client client = ClientBuilder.newClient();
        URI propURI = InventoryUtil.buildUri(hostname);
        return client.target(propURI)
                     .request(MediaType.APPLICATION_JSON)
                     .get(JsonObject.class);
    }

    // tag::buildLinksForHost[]
    public static JsonArray buildLinksForHost(String hostname, String invUri) {

        JsonArrayBuilder links = Json.createArrayBuilder();

        links.add(Json.createObjectBuilder()
                      .add("href", StringUtils.appendIfMissing(invUri, "/") + hostname)
                      // tag::self[]
                      .add("rel", "self"));
                      // end::self[]

        if (!hostname.equals("*")) {
            links.add(Json.createObjectBuilder()
                 .add("href", InventoryUtil.buildUri(hostname).toString())
                 // tag::properties[]
                 .add("rel", "properties"));
                 // end::properties[]
        }

        return links.build();
    }
    // end::buildLinksForHost[]

    public static boolean responseOk(String hostname) {
        try {
            URL target = new URL(buildUri(hostname).toString());
            HttpURLConnection http = (HttpURLConnection) target.openConnection();
            http.setConnectTimeout(50);
            int response = http.getResponseCode();
            return (response != 200) ? false : true;
        } catch (Exception e) {
            return false;
        }
    }

    private static URI buildUri(String hostname) {
        return UriBuilder.fromUri(SYSTEM_PROPERTIES)
                .host(hostname)
                .port(PORT)
                .scheme(PROTOCOL)
                .build();
    }

}
