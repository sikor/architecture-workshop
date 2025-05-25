package com.archiwork.commons.restClient;

import java.net.URL;
import java.util.List;

public record ApiBaseUrl(URL baseUrl, List<String> scopes) {
}
