package com.archiwork.commons.restClient;

import java.util.List;

public record ApiBaseUrl(String baseUrl, List<String> scopes) {
}
