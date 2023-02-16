import com.sap.gateway.ip.core.customdev.util.Message

import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.NameValuePair

import java.nio.charset.Charset
import java.util.function.Predicate
import java.util.List
import java.util.Set

def Message resolveAllRequestParameters(Message message) {
    addRequestParametersWithNameFilter(message, { p -> true })
    return message
}

def Message resolveRequestParameter(Message message, String parameter) {
    addRequestParametersWithNameFilter(message, { p -> StringUtils.equals(parameter, p.name) })
    return message
}

def Message resolveRequestParameters(Message message, Set<String> parameters) {
    addRequestParametersWithNameFilter(message, { p -> parameters.contains(p.name) })
    return message
}

private void addRequestParametersWithNameFilter(Message message, Predicate<NameValuePair> filterByName) {
    getRequestParameters(message).stream()
        .filter(filterByName)
        .forEach({ param -> message.setProperty(param.name, param.value) })
}

private List<NameValuePair> getRequestParameters(Message message) {
    def headers = message.getHeaders()
    def queryString = headers.get("CamelHttpQuery")
    if (StringUtils.isNotBlank(queryString)) {
        return URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"))
    } else {
        return List.of()
    }
}