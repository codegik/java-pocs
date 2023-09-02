package com.codegik.poc.restserver.handler

import com.codegik.poc.restserver.model.Constants.Companion.PATH_VARIABLE_PATTERN
import com.codegik.poc.restserver.model.Constants.Companion.PATH_VARIABLE_REPLACEMENT
import com.codegik.poc.restserver.model.HttpRequest
import java.util.concurrent.ConcurrentHashMap

class RequestHandlerMap {
    private val zeroParamEndpointMap = ConcurrentHashMap<Pair<String, String>, RequestHandler>()
    private val oneParamEndpointMap = ConcurrentHashMap<Pair<String, String>, RequestHandler>()
    private val twoParamEndpointMap = ConcurrentHashMap<Pair<String, String>, RequestHandler>()
    private val threeParamEndpointMap = ConcurrentHashMap<Pair<String, String>, RequestHandler>()
    private val manyParamEndpointMap = ConcurrentHashMap<Pair<String, String>, RequestHandler>()
    private val mappedMatchPatterns = ConcurrentHashMap<String, Regex>()


    fun put(count: Int, pair: Pair<String, String>, httpRequestHandler: HttpRequestHandler): Boolean {
        when (count) {
            0 -> zeroParamEndpointMap[pair] = httpRequestHandler
            1 -> oneParamEndpointMap[pair] = httpRequestHandler
            2 -> twoParamEndpointMap[pair] = httpRequestHandler
            3 -> threeParamEndpointMap[pair] = httpRequestHandler
            else -> manyParamEndpointMap[pair] = httpRequestHandler
        }

        if (pair.second.contains("{")) {
            var endpointPattern = pair.second.replace("/", "\\/")

            PATH_VARIABLE_PATTERN.findAll(pair.second).forEach { variable ->
                endpointPattern = endpointPattern.replace(variable.value, PATH_VARIABLE_REPLACEMENT)
            }

            mappedMatchPatterns[pair.second] = endpointPattern.toRegex()
        }

        return true
    }


    fun get(httpRequest: HttpRequest): RequestHandler {
        val pair = Pair("${httpRequest.method}", httpRequest.endpoint)
        val endpointMap = get(0, pair)

        if (endpointMap == null) { // means the endpoint has path parameters
            mappedMatchPatterns.forEach { (endpoint, pathParamPattern) ->
                val matchKey = Pair("${httpRequest.method}", endpoint)
                if (pathParamPattern.matches(httpRequest.endpoint)) {
                    val pathParameters = pathParamPattern.findAll(httpRequest.endpoint)
                        .map { it.groupValues }
                        .filter { it.size > 1 }
                        .flatMap { it.subList(1, it.size)}
                        .toList()
                    val httpRequestEnchant = httpRequest.copy(pathParameters = pathParameters)
                    val endpointMap = get(pathParameters.size, matchKey) ?: return NotFoundRequestHandler()

                    endpointMap?.httpRequest(httpRequestEnchant)

                    return endpointMap
                }
            }
        } else {
            return endpointMap?.httpRequest(httpRequest)
        }

        return NotFoundRequestHandler()
    }


    private fun get(count: Int, pair: Pair<String, String>): RequestHandler? {
        return when (count) {
            0 -> zeroParamEndpointMap[pair]
            1 -> oneParamEndpointMap[pair]
            2 -> twoParamEndpointMap[pair]
            3 -> threeParamEndpointMap[pair]
            else -> manyParamEndpointMap[pair]
        }
    }
}